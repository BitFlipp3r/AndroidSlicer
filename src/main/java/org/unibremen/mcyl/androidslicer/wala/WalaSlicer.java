package org.unibremen.mcyl.androidslicer.wala;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.StreamSupport;

import com.ibm.wala.cast.tree.CAstType.Method;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.classLoader.ShrikeBTMethod;
import com.ibm.wala.classLoader.ShrikeCTMethod;
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
import com.ibm.wala.shrikeBT.IInstruction;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.ipa.slicer.StatementWithInstructionIndex;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAPhiInstruction;
import com.ibm.wala.types.TypeName;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.WalaException;
import com.ibm.wala.util.config.AnalysisScopeReader;
import com.ibm.wala.util.strings.Atom;

import org.unibremen.mcyl.androidslicer.domain.SlicerOption;
import org.unibremen.mcyl.androidslicer.service.SliceLogger;

import com.ibm.wala.ipa.callgraph.AnalysisOptions.ReflectionOptions;
import com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions;
import com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions;

public class WalaSlicer {

  public static Map<String, Set<Integer>> doSlicing(File appJar, File exclusionFile, String androidClassName,
      List<String> entryMethods, List<String> seedStatements,
      ReflectionOptions reflectionOptions, DataDependenceOptions dataDependenceOptions,
      ControlDependenceOptions controlDependenceOptions,
      SliceLogger logger) throws WalaException, IOException, ClassHierarchyException, IllegalArgumentException,
      CallGraphBuilderCancelException, CancelException {
    long start = System.currentTimeMillis();
    /* create an analysis scope representing the appJar as a J2SE application */
    AnalysisScope scope = AnalysisScopeReader.makeJavaBinaryAnalysisScope(appJar.getAbsolutePath(), exclusionFile);
    IClassHierarchy cha = ClassHierarchyFactory.make(scope);

    /* make entry points */
    logger.log("\n== GET ENTRY POINTS ==");
    // Getting Entrypoints via different methods
    Iterable<Entrypoint> entrypoints = getEntrypoints(scope, cha, androidClassName, entryMethods, logger);

    if (!entrypoints.iterator().hasNext()) {
      throw new WalaException("COULD NOT FIND ENTRYPOINTS - EXIT");
    } else {
      logger.log("Number of entry points: " + StreamSupport.stream(entrypoints.spliterator(), false).count());
    }

    AnalysisOptions options = new AnalysisOptions(scope, entrypoints);
    /* you can dial down reflection handling if you like */
    options.setReflectionOptions(reflectionOptions);

    logger.log("\n== BUILDING CALL GRAPH ==");
    /* build the call graph */
    AnalysisCache cache = new AnalysisCacheImpl();
    /* other builders can be constructed with different Util methods */
    CallGraphBuilder cgBuilder = Util.makeZeroOneContainerCFABuilder(options, cache, cha, scope);
    // CallGraphBuilder builder = Util.makeNCFABuilder(2, options, cache, cha, scope);
    // CallGraphBuilder builder = Util.makeVanillaNCFABuilder(2, options, cache, cha, scope);
    CallGraph cg = cgBuilder.makeCallGraph(options, null);
    long end = System.currentTimeMillis();
    logger.log("Took " + (end - start) + "ms.");
    logger.log(CallGraphStats.getStats(cg));

    logger.log("\n== FIND ENTRY_METHOD(s)==");
    List<CGNode> entryMethodNodes = findMethods(cg, entryMethods, androidClassName, logger);
    logger.log("\n== SEED_STATEMENT(s) ==");
    List<Statement> statements = findCallsTo(entryMethodNodes, seedStatements, logger);

    logger.log("\n== SLICING ==");
    logger.log("Pointer analysis...");
    PointerAnalysis<InstanceKey> pointerAnalysis = cgBuilder.getPointerAnalysis();
    logger.log("done.");

    Collection<Statement> sliceList = new ArrayList<Statement>();
    for (Statement sm : statements) {
      logger.log("Computing backward slice for " + sm.getNode().getMethod().getName().toString());
      sliceList.addAll(
          Slicer.computeBackwardSlice(sm, cg, pointerAnalysis, dataDependenceOptions, controlDependenceOptions));
    }
    logger.log("Slicing done.");
    logger.log("Number of slices:  " + sliceList.size());

    logger.log("\n== GETTING SOURCE FILES ==");
    return dumpSlices(sliceList, logger);
  }

  // source: PN
  public static Map<String, Set<Integer>> dumpSlices(final Collection<Statement> slices, SliceLogger logger) {
    Map<String, Set<Integer>> sourceFileLineNumbers = new HashMap<>();

    for (Statement statement : slices)
      if (statement.getKind() != null && (statement.getKind() == Statement.Kind.NORMAL)) { // ignore special kinds of statements

        int bcIndex, instructionIndex = ((StatementWithInstructionIndex) statement).getInstructionIndex();

        IMethod method = statement.getNode().getMethod();

        // the source line number corresponding to a particular bytecode index, or -1 if
        // the information is not available.
        int srcLineNumber = -1;

        try {
          if (method instanceof ShrikeBTMethod) {
            bcIndex = ((ShrikeBTMethod) method).getBytecodeIndex(instructionIndex);
            srcLineNumber = ((ShrikeBTMethod) method).getLineNumber(bcIndex);
          } else {
            continue; // skip everything that is not a shrike mehtod wrapper, like FakeRootMethod
          }

          if (srcLineNumber == -1) {
            continue;
          }

          try {


            String declaringClass = method.getDeclaringClass().getName().toString();
            logger.log("Source line number " + srcLineNumber + " with method // " + method + " in class " + declaringClass);
            // construct java file path
            // (e.g. Lcom/android/.../AlarmMangerService$2 -> com/android/.../AlarmManagerService.java)
            if(declaringClass.indexOf("$") > -1){
              // remove inner class name (e.g. AlarmMangerService$2 -> AlarmManagerService)
              declaringClass = declaringClass.substring(0, declaringClass.indexOf("$"));
            }
            String declaringClassFile = declaringClass.substring(1, declaringClass.length()) + ".java";
            logger.log("Java source file: " + declaringClassFile);

            Set<Integer> currentLineNumbers = sourceFileLineNumbers.get(declaringClassFile);
            if (currentLineNumbers == null) {
                currentLineNumbers = new HashSet<Integer>();
            }
            currentLineNumbers.add(srcLineNumber);
            sourceFileLineNumbers.put(declaringClassFile, currentLineNumbers);
          } catch (Exception e) {
            logger.log("Error getting line sourceFileLineNumbers: " + e);
          }
        } catch (Exception e) {
          logger.log("getBytecodeIndex handling failed: " + e);
        }
      }
    return sourceFileLineNumbers;
  }

  /**
   * Quelle: Masterthesis Nguyen Datei: \Auditor\src\test\CustomSlicer.java
   *
   * @param scope
   * @param cha
   * @param androidClassName
   * @return
   */
  public static Set<Entrypoint> getEntrypoints(AnalysisScope scope, IClassHierarchy cha, String androidClassName,
      List<String> entryMethods, SliceLogger logger) {
    Set<Entrypoint> entrypoints = new HashSet<>();
    if (cha == null) {
      throw new IllegalArgumentException("cha is null");
    }

    for (IClass clazz : cha) {
      if (clazz instanceof ShrikeClass & !clazz.isInterface()) {
        String typeName = clazz.getName().toString();

        /*
         * Remove "Service" and package from class name to look for Stub-Classes as well (e.g.
         * Lcom/android/server/AlarmManagerService -> IAlarmManager$Stub),
         */
        if (typeName.equals(androidClassName) | typeName.startsWith(androidClassName + "$")) {

          if (isApplicationClass(scope, clazz)) {
            for (Iterator<? extends IMethod> methodIt = clazz.getDeclaredMethods().iterator(); methodIt.hasNext();) {
              IMethod method = (IMethod) methodIt.next();
              for (String entryMethod : entryMethods) {
              if (!method.isAbstract()
                  && method.getName().equals(Atom.findOrCreateUnicodeAtom(entryMethod)) /* && method.isPublic() */) {
                entrypoints.add(new ArgumentTypeEntrypoint(method, cha));
                logger.log("Found entry method " + entryMethod +  "() with object class: " + typeName + ".");
                }
              }
            }
          }
        }
      }
    }
    if (!entrypoints.iterator().hasNext()) {
      logger.log("COULD NOT FIND ENTRYPOINTS - EXIT");
      return null;
    } else {
      return entrypoints;
    }
  }

  // source: PN, modified by MC
  public static List<CGNode> findMethods(CallGraph cg, List<String> entryMethods, String androidClassName, SliceLogger logger)
      throws WalaException {
    List<CGNode> cgnodes = new ArrayList<>();
    for (Iterator<? extends CGNode> it = cg.iterator(); it.hasNext();) {
      CGNode node = it.next();
      // modified to check if class name of current node equals main class name or
      // inner clas

      Atom method = node.getMethod().getName();
      TypeName declaringClass = node.getMethod().getDeclaringClass().getName();

      if (declaringClass.equals(TypeName.findOrCreate(androidClassName))
          | declaringClass.toString().startsWith(androidClassName + "$")) {
            for (String entryMethod : entryMethods)
            if(method.equals(Atom.findOrCreateUnicodeAtom(entryMethod))){ // compare method name
              cgnodes.add(node);
              logger.log("Found call graph method " + entryMethod + "() with object class: " + declaringClass  + " in " + node + ".");
            }
      }
    }
    if (cgnodes.size() == 0) {
      throw new WalaException("Failed to find any methods from" + entryMethods + "!");
    }
    return cgnodes;
  }


  public static List<Statement> findCallsTo(List<CGNode> nodes, List<String> seedStatements, SliceLogger logger) throws WalaException {

    List<Statement> statements = new ArrayList<>();
    for (CGNode node : nodes) {
      IR ir = node.getIR();
      for (Iterator<SSAInstruction> it = ir.iterateAllInstructions(); it.hasNext();) {
        SSAInstruction s = it.next();
        if (s instanceof com.ibm.wala.ssa.SSAAbstractInvokeInstruction) {
          com.ibm.wala.ssa.SSAAbstractInvokeInstruction call = (com.ibm.wala.ssa.SSAAbstractInvokeInstruction) s;
          for(String seedStatementName : seedStatements){
          if (call.getCallSite().getDeclaredTarget().getName().toString().equals(seedStatementName)) {
            com.ibm.wala.util.intset.IntSet indices = ir.getCallInstructionIndices(call.getCallSite());
            statements.add(new com.ibm.wala.ipa.slicer.NormalStatement(node, indices.intIterator().next()));
            logger.log("Found seed statement " + seedStatementName + " in " + node + ".");
          }
        }
        }
      }
    }


    if (statements.size() == 0) {
      throw new WalaException("Failed to find any calls to " + seedStatements + " in " + nodes + "!");
    } else {
      return statements;
    }

  }

  private static boolean isApplicationClass(AnalysisScope scope, IClass clazz) {
    return scope.getApplicationLoader().equals(clazz.getClassLoader().getReference());
  }
}
