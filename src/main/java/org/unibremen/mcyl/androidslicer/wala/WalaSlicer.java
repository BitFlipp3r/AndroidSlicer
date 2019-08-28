package org.unibremen.mcyl.androidslicer.wala;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.classLoader.Language;
import com.ibm.wala.classLoader.ShrikeBTMethod;
import com.ibm.wala.classLoader.ShrikeClass;
import com.ibm.wala.ipa.callgraph.AnalysisCache;
import com.ibm.wala.ipa.callgraph.AnalysisCacheImpl;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.callgraph.AnalysisOptions.ReflectionOptions;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.CallGraphBuilder;
import com.ibm.wala.ipa.callgraph.CallGraphBuilderCancelException;
import com.ibm.wala.ipa.callgraph.CallGraphStats;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.impl.ArgumentTypeEntrypoint;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.callgraph.propagation.InstanceKey;
import com.ibm.wala.ipa.callgraph.propagation.PointerAnalysis;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ipa.slicer.NormalStatement;
import com.ibm.wala.ipa.slicer.Slicer;
import com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions;
import com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.ipa.slicer.StatementWithInstructionIndex;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.SSAAbstractInvokeInstruction;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSANewInstruction;
import com.ibm.wala.types.TypeName;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.WalaException;
import com.ibm.wala.util.config.AnalysisScopeReader;
import com.ibm.wala.util.intset.IntSet;
import com.ibm.wala.util.strings.Atom;

import org.unibremen.mcyl.androidslicer.domain.enumeration.CFAType;
import org.unibremen.mcyl.androidslicer.service.SliceLogger;

/**
 * This is an implementation of the WALA slicing algorithm described here: http://wala.sourceforge.net/wiki/index.php/UserGuide:Slicer
 * The code based on the work by Markus Gulman (Masterthesis 2014) and Philip Phu Dang Hoan Nguyen (Masterthesis 2018) but has been 
 * heavily altered by Michael Cyl with bug fixed, improvements and refactorings. Most notable changes are the option to choose cfa level
 * for pointer analysis, the usage of multiple entry methods and seed statements, a search for inner classes and a deep search for 
 * seed statements.
 */
public class WalaSlicer {

    public static Map<String, Set<Integer>> doSlicing(File appJar, File exclusionFile, String androidClassName,
            Set<String> entryMethods, Set<String> seedStatements, CFAType cfaType, Integer cfaLevel, ReflectionOptions reflectionOptions,
            DataDependenceOptions dataDependenceOptions, ControlDependenceOptions controlDependenceOptions,
            SliceLogger logger) throws WalaException, IOException, ClassHierarchyException, IllegalArgumentException,
            CallGraphBuilderCancelException, CancelException {

        long start = System.currentTimeMillis();

        /* create an analysis scope representing the appJar as a J2SE application */
        AnalysisScope scope = AnalysisScopeReader.makeJavaBinaryAnalysisScope(appJar.getAbsolutePath(), exclusionFile);
        IClassHierarchy cha = ClassHierarchyFactory.make(scope);

        /* make entry points */
        logger.log("\n== GET ENTRY POINTS =="); 
        Iterable<Entrypoint> entrypoints = getEntrypoints(scope, cha, androidClassName, entryMethods, logger);

        if (!entrypoints.iterator().hasNext()) {
            throw new WalaException("Failed to find any entry points from " + entryMethods + "!");
        } else {
            logger.log("Number of entry points: " + StreamSupport.stream(entrypoints.spliterator(), false).count());
        }

        AnalysisOptions options = new AnalysisOptions(scope, entrypoints);
        /* you can dial down reflection handling if you like */
        options.setReflectionOptions(reflectionOptions);

        logger.log("\n== BUILDING CALL GRAPH ==");
        /*  build the call graph  */
        AnalysisCache cache = new AnalysisCacheImpl();
        /* builders can be constructed with different Util methods (see: https://wala.github.io/javadoc/com/ibm/wala/ipa/callgraph/impl/Util.html)*/
        
        CallGraphBuilder cgBuilder = null;
        
        switch(cfaType){
            case ZERO_CFA:
                cgBuilder = Util.makeZeroCFABuilder(Language.JAVA, options, cache, cha, scope);
                break; 
            case ZERO_ONE_CFA:
                cgBuilder = Util.makeZeroOneCFABuilder(Language.JAVA, options, cache, cha, scope);
                break; 
            case VANILLA_ZERO_ONE_CFA:
                cgBuilder = Util.makeVanillaZeroOneCFABuilder(Language.JAVA, options, cache, cha, scope);
                break; 
            case N_CFA:
                if(cfaLevel != null && cfaLevel >= 0)
                cgBuilder = Util.makeNCFABuilder(cfaLevel, options, cache, cha, scope);
                break; 
            case VANILLA_N_CFA:
                if(cfaLevel != null && cfaLevel >= 0)
                cgBuilder = Util.makeVanillaNCFABuilder(cfaLevel, options, cache, cha, scope);
                break; 
            case ZERO_CONTAINER_CFA:
                cgBuilder = Util.makeZeroContainerCFABuilder(options, cache, cha, scope);
                break; 
            case ZERO_ONE_CONTAINER_CFA:
                cgBuilder = Util.makeZeroOneContainerCFABuilder(options, cache, cha, scope);
                break; 
            case VANILLA_ZERO_ONE_CONTAINER_CFA:
                cgBuilder = Util.makeVanillaZeroOneContainerCFABuilder(options, cache, cha, scope);
                break; 
            default:
                throw new WalaException("No CAF Option Type found to build Call Graph.");
        }
        
        if(cgBuilder == null){
            throw new WalaException("Call Graph Builder could not be initialized.");
        }

        cgBuilder = Util.makeZeroOneContainerCFABuilder(options, cache, cha, scope);
        // CallGraphBuilder builder = Util.makeNCFABuilder(2, options, cache, cha,
        // scope);
        // CallGraphBuilder builder = Util.makeVanillaNCFABuilder(2, options, cache,
        // cha, scope);
        CallGraph cg = cgBuilder.makeCallGraph(options, null);
        long end = System.currentTimeMillis();
        logger.log("Took " + (end - start) + "ms.");
        logger.log(CallGraphStats.getStats(cg));

        logger.log("\n== FIND ENTRY_METHOD(s)==");
        Set<CGNode> methodNodes = new HashSet<CGNode>();
        findMethods(cg, entryMethods, methodNodes, androidClassName, logger);
        if (methodNodes.size() == 0) {
            throw new WalaException("Failed to find any methods from" + entryMethods + "!");
        }
        logger.log("\n== SEED_STATEMENT(s) ==");
        Set<Statement> statements = findSeedStatements(cg, methodNodes, seedStatements, logger);
        if (statements.size() == 0) {
            throw new WalaException("No Seed Statements found!");
        }

        logger.log("\n== SLICING ==");
        String cfaOptionName = cfaType.toString();
        if(cfaLevel != null && cfaLevel > 0){
            cfaOptionName+= " with n = " + cfaLevel;
        }
        logger.log("Computing Pointer Analysis with " + cfaOptionName + ".");
        PointerAnalysis<InstanceKey> pointerAnalysis = cgBuilder.getPointerAnalysis();

        Collection<Statement> sliceList = new HashSet<Statement>();
        for (Statement stmt : statements) {
            logger.log("+ Computing backward slice for " + stmt.getNode().toString());
            sliceList.addAll(Slicer.computeBackwardSlice(stmt, cg, pointerAnalysis, dataDependenceOptions,
                    controlDependenceOptions));
        }
        logger.log("\nNumber of slice statements:  " + sliceList.size());
        
        /* too much to log this for big slices */
        //for (Statement stmt : sliceList) {
        //    logger.log("~ " + stmt.toString());
        //}

        logger.log("\n== GETTING SOURCE FILES ==");
        return getLineNumbersAndSourceFiles(sliceList, logger);
    }

    public static Map<String, Set<Integer>> getLineNumbersAndSourceFiles(final Collection<Statement> slices, SliceLogger logger) {
        Map<String, Set<Integer>> sourceFileLineNumbers = new HashMap<>();

        for (Statement statement : slices) {
            // ignore special kinds of statements
            if (statement.getKind() != null && 
                statement instanceof StatementWithInstructionIndex &&
                    (statement.getKind() == Statement.Kind.NORMAL | 
                    statement.getKind() == Statement.Kind.NORMAL_RET_CALLEE |
                    statement.getKind() == Statement.Kind.NORMAL_RET_CALLER)) {

                int bcIndex, instructionIndex = ((StatementWithInstructionIndex) statement).getInstructionIndex();
                IMethod method = statement.getNode().getMethod();

                // the source line number corresponding to a particular bytecode index, or -1 if
                // the information is not available.
                int srcLineNumber = -1;

                try {
                    if (method != null && method instanceof ShrikeBTMethod) {
                        bcIndex = ((ShrikeBTMethod) method).getBytecodeIndex(instructionIndex);
                        srcLineNumber = ((ShrikeBTMethod) method).getLineNumber(bcIndex);
                    } else {
                        continue; // skip everything that is not a shrike method wrapper, like FakeRootMethod
                    }

                    if (srcLineNumber == -1) {
                        continue;
                    }

                    try {
                        logger.log("+ Statement: " + statement.toString());                       
                        logger.log("~ Source line number: " + srcLineNumber);
                        // construct java file path
                        // (e.g. Lcom/android/.../AlarmMangerService$2 ->
                        // com/android/.../AlarmManagerService.java)
                        String declaringClass = method.getDeclaringClass().getName().toString();
                        if (declaringClass.indexOf("$") > -1) {
                            // remove inner class name (e.g. AlarmMangerService$2 -> AlarmManagerService)
                            declaringClass = declaringClass.substring(0, declaringClass.indexOf("$"));
                        }
                        String declaringClassFile = declaringClass.substring(1, declaringClass.length()) + ".java";
                        logger.log("~ Java source file: " + declaringClassFile + "\n");

                        Set<Integer> currentLineNumbers = sourceFileLineNumbers.get(declaringClassFile);
                        if (currentLineNumbers == null) {
                            currentLineNumbers = new HashSet<Integer>();
                        }
                        currentLineNumbers.add(srcLineNumber);
                        sourceFileLineNumbers.put(declaringClassFile, currentLineNumbers);
                    } catch (Exception e) {
                        logger.log("- Error getting line sourceFileLineNumbers: " + e);
                    }
                } catch (Exception e) {
                    logger.log("- getBytecodeIndex handling failed: " + e);
                }
            }
        }
        return sourceFileLineNumbers;
    }

    public static Set<Entrypoint> getEntrypoints(AnalysisScope scope, IClassHierarchy classHierarchy, String androidClassName,
            Set<String> entryMethods, SliceLogger logger) {

        Set<Entrypoint> entrypoints = new HashSet<Entrypoint>();

        if (classHierarchy == null) {
            throw new IllegalArgumentException("ClassHierarchy is null!");
        }

        for (IClass clazz : classHierarchy) {
            if (clazz instanceof ShrikeClass & !clazz.isInterface()) {
                String typeName = clazz.getName().toString();

                /*
                 * Remove "Service" and package from class name to look for Stub-Classes as well
                 * (e.g. Lcom/android/server/AlarmManagerService -> IAlarmManager$Stub),
                 */
                if (typeName.equals(androidClassName) | typeName.startsWith(androidClassName + "$")) {

                    if (isApplicationClass(scope, clazz)) {
                        for (Iterator<? extends IMethod> methodIt = clazz.getDeclaredMethods().iterator(); methodIt.hasNext();) {
                            IMethod method = (IMethod) methodIt.next();
                            for (String entryMethod : entryMethods) {
                                if (!method.isAbstract() && method.getName().equals(Atom.findOrCreateUnicodeAtom(entryMethod))) {
                                    entrypoints.add(new ArgumentTypeEntrypoint(method, classHierarchy));
                                    logger.log("~ Found entry method: " + entryMethod + "() with object class: " + typeName + ".");
                                }
                            }
                        }
                    }
                }
            }
        }
        return entrypoints;
    }

    /**
     * Based on the work by Markus Gulman (Masterthesis 2014) and Philip Phu Dang Hoan Nguyen (Masterthesis 2018). Modified by 
     * Michael Cyl to search inner classes (like AlarmMangerService$1) and to add CGNode for all inner methods, i.e. callees of
     * the entry methods, as well.
     */
    public static Set<CGNode> findMethods(CallGraph cg, Set<String> methodNames,  Set<CGNode> methodNodes, String androidClassName, SliceLogger logger) throws WalaException {

        for (Iterator<? extends CGNode> nodeIt = cg.iterator(); nodeIt.hasNext();) {
            CGNode node = nodeIt.next();
            // modified to check if class name of current node equals main class name or
            // inner class
            Atom method = node.getMethod().getName();
            TypeName declaringClass = node.getMethod().getDeclaringClass().getName();

            // check if class name of current node equals android class name ...
            if (declaringClass.equals(TypeName.findOrCreate(androidClassName))
            // ... or inner class name
               | declaringClass.toString().startsWith(androidClassName + "$")) {

                // check all entry methods
                for (String methodName : methodNames){
                    // compare method name
                    if (method.equals(Atom.findOrCreateUnicodeAtom(methodName))) {
                        // add node if not already in set
                        if(!methodNodes.contains(node)){
                            methodNodes.add(node);
                            logger.log("~ Found call graph method: " + methodName + "() with object class: " 
                            + declaringClass + " in " + node + ".");
                            // search inner method nodes
                            Set<String> innerMethodNames = getInnerMethodNames(node);
                            if(!innerMethodNames.isEmpty()){
                                findMethods(cg, innerMethodNames, methodNodes, androidClassName, logger);
                            }
                        }
                    }
                }
            }
        }
        return methodNodes;
    }

    private static Set<String> getInnerMethodNames(CGNode node){

        Set<String> innerMethodNames = new HashSet<String>();

        IR ir = node.getIR();
        if (ir != null && ir.iterateAllInstructions() != null) {
            for (Iterator<SSAInstruction> it = ir.iterateAllInstructions(); it.hasNext();) {
                SSAInstruction instruction = it.next();

                // search method invoke instructions
                if (instruction instanceof SSAAbstractInvokeInstruction) {
                    SSAAbstractInvokeInstruction call = (SSAAbstractInvokeInstruction) instruction;
                    innerMethodNames.add(call.getDeclaredTarget().getName().toString());             
                }
            }
        }

        return innerMethodNames;
    }

    public static Set<Statement> findSeedStatements(CallGraph cg, Set<CGNode> nodes, Set<String> seedStatements, SliceLogger logger) throws WalaException {

        Set<Statement> statements = new HashSet<Statement>();

        for (CGNode node : nodes) {
            IR ir = node.getIR();
            if (ir == null || ir.iterateAllInstructions() == null) {
                continue;
            }

            for (int i = 0; i < ir.getInstructions().length; i++) {

                SSAInstruction instruction = ir.getInstructions()[i];

                // check for method- and new-instructions
                // other types of instructions can be found here: 
                // http://wala.sourceforge.net/javadocs/trunk/com/ibm/wala/ssa/SSAInstruction.html

                // add seed statements with method invoke instructions
                if (instruction instanceof SSAAbstractInvokeInstruction) {
                    SSAAbstractInvokeInstruction call = (SSAAbstractInvokeInstruction) instruction;

                    // check all seed statements
                    for (String seedStatementName : seedStatements) {
                        if (Pattern.matches(seedStatementName, call.getCallSite().getDeclaredTarget().getName().toString())) {
                            IntSet indices = ir.getCallInstructionIndices(call.getCallSite());
                            statements.add(new NormalStatement(node, indices.intIterator().next()));
                            logger.log("~ Found seed statement: " + call.getCallSite().getDeclaredTarget().getName().toString() + " in " + node + ".");
                        }
                    }
                }

                // add seed statements with "new" instructions
                if (instruction instanceof SSANewInstruction) {
                    SSANewInstruction call = (SSANewInstruction) instruction;

                    // get class name (e.g. Ljava/lang/SecurityException -> SecurityException)
                    String exceptionName = call.getNewSite().getDeclaredType().getName().toString();
                    exceptionName = exceptionName.substring(exceptionName.lastIndexOf("/") + 1, exceptionName.length());

                    // check all seed statements
                    for (String seedStatementName : seedStatements) {
                        if (exceptionName.equals(seedStatementName)){
                            statements.add(new NormalStatement(node, i));
                            logger.log("~ Found seed statement: new " + seedStatementName + " in " + node + ".");
                        }
                    }
                }
            }
        }

        if (statements.size() == 0) {
            logger.log("- Failed to find any calls to " + seedStatements + " in " + nodes + "!");
        }

        return statements;
    }

    private static boolean isApplicationClass(AnalysisScope scope, IClass clazz) {
        return scope.getApplicationLoader().equals(clazz.getClassLoader().getReference());
    }
}
