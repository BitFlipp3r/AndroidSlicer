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
import com.ibm.wala.util.debug.UnimplementedError;
import com.ibm.wala.util.intset.IntSet;
import com.ibm.wala.util.strings.Atom;

import org.bson.BSONException;
import org.unibremen.mcyl.androidslicer.domain.enumeration.CFAType;
import org.unibremen.mcyl.androidslicer.service.SliceLogger;

/**
 * This is an implementation of the WALA slicing algorithm described here: http://wala.sourceforge.net/wiki/index.php/UserGuide:Slicer
 * The code based on the work by Markus Gulman (Masterthesis 2014) and Philip Phu Dang Hoan Nguyen (Masterthesis 2018) but has been 
 * heavily altered by Michael Cyl with bug fixed, improvements and refactorings. Most notable changes are the option to choose cfa level
 * for pointer analysis, the usage of multiple entry methods and seed statements (regex inclusive), a search for inner classes and a 
 * deep search for seed statements.
 */
public class WalaSlicer {

    // maximum number of wala slice statements that will be logged to not blow up the db (maximum BSON document size for a MongoDB entity is 16MB)
    private static final int MAX_DETAILED_LOG = 1000;

    public static Map<String, Set<Integer>> doSlicing(File appJar, File exclusionFile, String androidClassName,
            Set<String> entryMethods, Set<String> seedStatementNames, CFAType cfaType, Integer cfaLevel, ReflectionOptions reflectionOptions,
            DataDependenceOptions dataDependenceOptions, ControlDependenceOptions controlDependenceOptions,
            SliceLogger logger) throws WalaException, IOException, ClassHierarchyException, IllegalArgumentException,
            CallGraphBuilderCancelException, CancelException {

        long start = System.currentTimeMillis();

        /* create an analysis scope representing the appJar as a J2SE application */
        AnalysisScope scope = AnalysisScopeReader.makeJavaBinaryAnalysisScope(appJar.getAbsolutePath(), exclusionFile);
        IClassHierarchy classHierarchy = ClassHierarchyFactory.make(scope);

        /* make entry points */
        logger.log("\n== GET ENTRY POINTS =="); 
        Iterable<Entrypoint> entrypoints = WalaSlicer.getEntrypoints(scope, classHierarchy, androidClassName, entryMethods, logger);

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

        String cfaOptionName = cfaType.toString();
        if(cfaLevel != null && cfaLevel > 0){
            cfaOptionName+= " with n = " + cfaLevel;
        }
        logger.log("Computing Pointer Analysis with " + cfaOptionName + ".");
        
        CallGraphBuilder cgBuilder = null;
        
        switch(cfaType){
            case ZERO_CFA:
                cgBuilder = Util.makeZeroCFABuilder(Language.JAVA, options, cache, classHierarchy, scope);
                break; 
            case ZERO_ONE_CFA:
                cgBuilder = Util.makeZeroOneCFABuilder(Language.JAVA, options, cache, classHierarchy, scope);
                break; 
            case VANILLA_ZERO_ONE_CFA:
                cgBuilder = Util.makeVanillaZeroOneCFABuilder(Language.JAVA, options, cache, classHierarchy, scope);
                break; 
            case N_CFA:
                if(cfaLevel != null && cfaLevel >= 0)
                cgBuilder = Util.makeNCFABuilder(cfaLevel, options, cache, classHierarchy, scope);
                break; 
            case VANILLA_N_CFA:
                if(cfaLevel != null && cfaLevel >= 0)
                cgBuilder = Util.makeVanillaNCFABuilder(cfaLevel, options, cache, classHierarchy, scope);
                break; 
            case ZERO_CONTAINER_CFA:
                cgBuilder = Util.makeZeroContainerCFABuilder(options, cache, classHierarchy, scope);
                break; 
            case ZERO_ONE_CONTAINER_CFA:
                cgBuilder = Util.makeZeroOneContainerCFABuilder(options, cache, classHierarchy, scope);
                break; 
            case VANILLA_ZERO_ONE_CONTAINER_CFA:
                cgBuilder = Util.makeVanillaZeroOneContainerCFABuilder(options, cache, classHierarchy, scope);
                break; 
            default:
                throw new WalaException("No CAF Option Type found to build Call Graph.");
        }
        
        if(cgBuilder == null){
            throw new WalaException("Call Graph Builder could not be initialized.");
        }

        CallGraph callGraph = cgBuilder.makeCallGraph(options, null);
        long end = System.currentTimeMillis();
        logger.log("Took " + (end - start) + "ms.");
        logger.log(CallGraphStats.getStats(callGraph));

        logger.log("\n== FIND ENTRY_METHOD(s)==");
        Set<CGNode> methodNodes = new HashSet<CGNode>();
        WalaSlicer.findMethodNodes(callGraph, entryMethods, methodNodes, androidClassName, logger);
        if (methodNodes.size() == 0) {
            throw new WalaException("Failed to find any methods from" + entryMethods + "!");
        }
        logger.log("\n== SEED_STATEMENT(s) ==");
        Set<Statement> seedStatements = WalaSlicer.findSeedStatements(methodNodes, seedStatementNames, logger);
        if (seedStatements.size() == 0) {
            throw new WalaException("No Seed Statements found!");
        }

        logger.log("\n== SLICING ==");
        Collection<Statement> sliceList = new HashSet<Statement>();
        for (Statement seedStatement : seedStatements) {
            logger.log("+ Computing backward slice for " + seedStatement.getNode().toString());
            try{
            sliceList.addAll(Slicer.computeBackwardSlice(seedStatement, callGraph, cgBuilder.getPointerAnalysis(), dataDependenceOptions, controlDependenceOptions));
            }
            catch(UnimplementedError ue){
                throw new WalaException("One of the parameters is not implemented yet: " + ue.getStackTrace());
                // TOOO Stack Trace to string
            }
        }
        logger.log("\nNumber of slice statements:  " + sliceList.size());
        
        /* Its too much to log this for big slices... */
        //for (Statement seedStatement : sliceList) {
        //    logger.log("~ " + seedStatement.toString());
        //}

        logger.log("\n== GETTING SOURCE FILE LINE NUMBERS ==");
        return WalaSlicer.getLineNumbersGroupedBySourceFiles(sliceList, logger);
    }

    /**
     * This methods takes the slice statements collection found by the wala slicer and returns their line numbers corresponding to the source code.
     * 
     * @param sliceStatements
     * @param logger
     * @return sourceFileLineNumbers: HashMap that has slice line numbers grouped by source file names of the class (e.g. [com/android/.../AlarmManagerService.java] : {3,5,6})
     */
    private static Map<String, Set<Integer>> getLineNumbersGroupedBySourceFiles(final Collection<Statement> sliceStatements, SliceLogger logger) {
        Map<String, Set<Integer>> sourceFileLineNumbers = new HashMap<>(); // map to group slice line numbers by class

        boolean doLogging = true;
        if(sliceStatements.size() > MAX_DETAILED_LOG){
            logger.log("Number of Slice Statements exceeded maximum Statements to log(" + MAX_DETAILED_LOG +"). Therefore Slice Statements will not be logged in detail.");
            doLogging = false;
        }

        for (Statement statement : sliceStatements) {
            // ignore special kinds of statements
            if (statement.getKind() != null && 
                statement instanceof StatementWithInstructionIndex &&
                    (statement.getKind() == Statement.Kind.NORMAL | 
                    statement.getKind() == Statement.Kind.NORMAL_RET_CALLEE |
                    statement.getKind() == Statement.Kind.NORMAL_RET_CALLER)) {

                int instructionIndex = ((StatementWithInstructionIndex) statement).getInstructionIndex();
                IMethod method = statement.getNode().getMethod();

                // the source line number corresponding to a particular bytecode index, or -1 if
                // the information is not available.
                int srcLineNumber = -1;

                try {
                    if (method != null && method instanceof ShrikeBTMethod) {
                        int bcIndex = ((ShrikeBTMethod) method).getBytecodeIndex(instructionIndex);
                        srcLineNumber = ((ShrikeBTMethod) method).getLineNumber(bcIndex);
                    } else {
                        continue; // skip everything that is not a shrike method wrapper, like FakeRootMethod
                    }

                    if (srcLineNumber == -1) {
                        continue;
                    }

                    try {
                        if(doLogging){
                            logger.logWithBuffer("+ Statement: " + statement.toString());                       
                            logger.logWithBuffer("~ Source line number: " + srcLineNumber);
                        }
                        // construct java file path
                        // (e.g. Lcom/android/server/AlarmManagerService$2 ->
                        // com/android/server/AlarmManagerService.java)
                        String declaringClass = method.getDeclaringClass().getName().toString();
                        if (declaringClass.indexOf("$") > -1) {
                            // remove inner class name (e.g. AlarmManagerService$2 -> AlarmManagerService)
                            declaringClass = declaringClass.substring(0, declaringClass.indexOf("$"));
                        }
                        String declaringClassFile = declaringClass.substring(1, declaringClass.length()) + ".java";
                        if(doLogging){
                            logger.logWithBuffer("~ Java source file: " + declaringClassFile + "\n");
                        }
                        Set<Integer> currentLineNumbers = sourceFileLineNumbers.get(declaringClassFile);
                        if (currentLineNumbers == null) {
                            currentLineNumbers = new HashSet<Integer>();
                        }
                        currentLineNumbers.add(srcLineNumber);
                        sourceFileLineNumbers.put(declaringClassFile, currentLineNumbers);
                    } catch (Exception e) {
                        if(doLogging){
                            logger.logWithBuffer("- Error getting line sourceFileLineNumbers: " + e);
                        }                      
                    }
                } catch (Exception e) {
                    if(doLogging){
                        logger.logWithBuffer("- getBytecodeIndex handling failed: " + e);
                    }
                }
            }
        }
        return sourceFileLineNumbers;
    }

    /**
     * This method searches for entry points inside the class hierarchy. 
     * 
     * @param scope AnalysisScope
     * @param classHierarchy IClassHierarchy
     * @param androidClassName Android class name to compare type names to (e.g. Lcom/android/server/AlarmManagerService)
     * @param entryMethods The list of user specified enty method names to search for
     * @param logger
     * @return entrypoints: Set of entry points
     */
    private static Set<Entrypoint> getEntrypoints(AnalysisScope scope, IClassHierarchy classHierarchy, String androidClassName,
            Set<String> entryMethods, SliceLogger logger) {

        Set<Entrypoint> entrypoints = new HashSet<Entrypoint>();

        if (classHierarchy == null) {
            throw new IllegalArgumentException("ClassHierarchy is null!");
        }

        for (IClass clazz : classHierarchy) {
            if (clazz instanceof ShrikeClass & !clazz.isInterface() & isApplicationClass(scope, clazz)) {
                String typeName = clazz.getName().toString();

                // check if type name of current class equals android class name ...
                if (typeName.equals(androidClassName) 
                    // .. also check inner class names as well (e.g Lcom/android/server/AlarmManagerService$[InnerClassName])
                    | typeName.startsWith(androidClassName + "$")) {

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
        return entrypoints;
    }

    /**
     * Find the method nodes which belong to the entry methods of the call graph. Also look for any methods that are called within these methods recursively  {@link #getInnerMethodNames(CGNode node) getInnerMethodNames}. 
     * 
     * Based on the work by Markus Gulman (Masterthesis 2014) and Philip Phu Dang Hoan Nguyen (Masterthesis 2018). Modified by 
     * Michael Cyl to search inner classes (like AlarmMangerService$1) and to add CGNode for all inner methods, i.e. callees of
     * the entry methods, as well.
     * 
     * @param callGraph CallGraph
     * @param methodNames Method names to compare the method nodes to
     * @param methodNodes Collection of found method nodes to keep during recursion
     * @param androidClassName  Android class name to compare type names to (e.g. Lcom/android/server/AlarmManagerService)
     * @param logger
     * @return methodNodes: Collection of found method nodes
     * @throws WalaException
     */
    private static Set<CGNode> findMethodNodes(CallGraph callGraph, Set<String> methodNames,  Set<CGNode> methodNodes, String androidClassName, SliceLogger logger) throws WalaException {

        for (Iterator<? extends CGNode> nodeIt = callGraph.iterator(); nodeIt.hasNext();) {
            CGNode node = nodeIt.next();

            // get the method name
            Atom methodNodeName = node.getMethod().getName();
            // get the class name
            TypeName declaringClassName = node.getMethod().getDeclaringClass().getName();

            // check if class name of current node equals android class name ...
            if (declaringClassName.equals(TypeName.findOrCreate(androidClassName))
                // ... or inner class name
                | declaringClassName.toString().startsWith(androidClassName + "$")) {

                // check all given method names (i.e. entry methods and inner methods)
                for (String methodName : methodNames){
                    // compare method name
                    if (methodNodeName.equals(Atom.findOrCreateUnicodeAtom(methodName))) {
                        // add node if not already in set
                        if(!methodNodes.contains(node)){
                            methodNodes.add(node);
                            logger.log("~ Found call graph method: " + methodName + "() with object class: " 
                            + declaringClassName + " in " + node + ".");
                            // search inner method nodes
                            Set<String> innerMethodNames = getInnerMethodNames(node);
                            if(!innerMethodNames.isEmpty()){
                                findMethodNodes(callGraph, innerMethodNames, methodNodes, androidClassName, logger);
                            }
                        }
                    }
                }
            }
        }
        return methodNodes;
    }

    /**
     * This is a helper method for  {@link #findMethodNodes(CallGraph cg, Set<String> methodNames,  Set<CGNode> methodNodes, String androidClassName, SliceLogger logger) findMethodNodes} to find all methods (i.e their names) which are called inside of a given node.
     * 
     * @param node to search.
     * @return innerMethodNames List of method names.
     */
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

    /**
     * Search for the user specified seed statement inside the method nodes which have been found by. The algorithm also iterates over all method nodes found by {@link #findMethodNodes(CallGraph cg, Set<String> methodNames,  Set<CGNode> methodNodes, String androidClassName, SliceLogger logger) findMethodNodes}.
     * 
     * @param nodes Method nodes to search
     * @param seedStatementNames User specified seed statement names to compare to.
     * @param logger
     * @return Set of Statements
     * @throws WalaException
     */
    private static Set<Statement> findSeedStatements(Set<CGNode> nodes, Set<String> seedStatementNames, SliceLogger logger) throws WalaException {

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
                    String abstractInvokeInstructionName = call.getCallSite().getDeclaredTarget().getName().toString();

                    // check all seed statements
                    for (String seedStatementName : seedStatementNames) {
                        // mcyl: use regex for seed statements
                        if (Pattern.matches(seedStatementName, abstractInvokeInstructionName)) {
                            IntSet indices = ir.getCallInstructionIndices(call.getCallSite());
                            statements.add(new NormalStatement(node, indices.intIterator().next()));
                            logger.log("~ Found seed statement: " + call.getCallSite().getDeclaredTarget().getName().toString() + " in " + node + ".");
                        }
                    }
                }

                // mcyl: add seed statements with "new" instructions
                if (instruction instanceof SSANewInstruction) {
                    SSANewInstruction call = (SSANewInstruction) instruction;

                    // get class name (e.g. Ljava/lang/SecurityException -> SecurityException)
                    String newInstructionName = call.getNewSite().getDeclaredType().getName().toString();
                    newInstructionName = newInstructionName.substring(newInstructionName.lastIndexOf("/") + 1, newInstructionName.length());

                    // check all seed statements
                    for (String seedStatementName : seedStatementNames) {
                        if (Pattern.matches(seedStatementName, newInstructionName)){
                            statements.add(new NormalStatement(node, i));
                            logger.log("~ Found seed statement: new " + seedStatementName + " in " + node + ".");
                        }
                    }
                }
            }
        }

        if (statements.size() == 0) {
            logger.log("- Failed to find any calls to " + seedStatementNames + " in " + nodes + "!");
        }

        return statements;
    }

    private static boolean isApplicationClass(AnalysisScope scope, IClass clazz) {
        return scope.getApplicationLoader().equals(clazz.getClassLoader().getReference());
    }
}
