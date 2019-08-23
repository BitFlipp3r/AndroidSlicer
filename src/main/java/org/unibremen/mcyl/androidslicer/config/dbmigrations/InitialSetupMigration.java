package org.unibremen.mcyl.androidslicer.config.dbmigrations;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.unibremen.mcyl.androidslicer.config.Constants;
import org.unibremen.mcyl.androidslicer.domain.CFAOption;
import org.unibremen.mcyl.androidslicer.domain.SlicerOption;
import org.unibremen.mcyl.androidslicer.domain.SlicerSetting;
import org.unibremen.mcyl.androidslicer.domain.enumeration.CFAType;
import org.unibremen.mcyl.androidslicer.domain.enumeration.SlicerOptionType;

/**
 * Creates the initial database setup
 */
@ChangeLog(order = "001")
public class InitialSetupMigration {

    @ChangeSet(order = "01", author = "initiator", id = "01-addDefaultSettings")
    public void addDefaultSettings(MongoTemplate mongoTemplate) {
        SlicerSetting androidSourcePath = new SlicerSetting();
        androidSourcePath.setKey(Constants.ANDROID_SOURCE_PATH_KEY);
        androidSourcePath.setValue("android-resources");
        androidSourcePath.setDescription("This is the file path to the android source code files (system services and AIDL-files). The files should be inside a subfolder named android-xx, where xx represents the API-Level. Can be relative to the execution directory of the Android-Slicer or an absolute Path.");
        mongoTemplate.save(androidSourcePath);

        SlicerSetting androidPlatformPath = new SlicerSetting();
        androidPlatformPath.setKey(Constants.ANDROID_PLATFORM_PATH_KEY);
        androidPlatformPath.setValue("android-resources");
        androidPlatformPath.setDescription("This is the file path to the android binary code (android.jar). The android.jar should be inside a subfolder named android-xx, where xx represents the API-Level. Can be relative to the execution directory of the Android-Slicer or an absolute Path.");
        mongoTemplate.save(androidPlatformPath);

        SlicerSetting serviceRegex = new SlicerSetting();
        serviceRegex.setKey(Constants.SERVICE_REGEX_KEY);
        serviceRegex.setValue(".*ManagerService.java");
        serviceRegex.setDescription("This is the regular expression which the Android-Slicer uses to find system service source files inside the source code files location.");
        mongoTemplate.save(serviceRegex);

        SlicerSetting seedStatements = new SlicerSetting();
        seedStatements.setKey(Constants.SEED_STATEMENTS_KEY);
        seedStatements.setValue(
                "checkCallingOrSelfPermission; checkCallingOrSelfUriPermission; checkCallingPermission; checkCallingUriPermission;checkPermission; checkSelfPermission; checkUriPermission; enforceCallingOrSelfPermission; enforceCallingOrSelfUriPermission; enforceCallingPermission; enforceCallingUriPermission; enforcePermission; enforceUriPermission; checkUriPermission; SecurityException");
        seedStatements.setDescription("These are the default service hook-methods from android. They are available as standard selection options for seed statements. This default list can be edited here. Entries should be separated with a semicolon.");
        mongoTemplate.save(seedStatements);

        SlicerSetting exlusionList = new SlicerSetting();
        exlusionList.setKey(Constants.EXCLUSION_LIST_KEY);
        StringBuilder result = new StringBuilder("");

        // Get file from resources folder
        // ClassLoader classLoader = getClass().getClassLoader();
        // File file = new
        // File(classLoader.getResource("wala/ExclusionFile.txt").getFile());
        InputStream inputStream = null;
        try {
            ClassPathResource classPathResource = new ClassPathResource("wala/ExclusionFile.txt");
            inputStream = classPathResource.getInputStream();
            File exclusionFile = File.createTempFile("ExclusionFile", ".txt");

            FileUtils.copyInputStreamToFile(inputStream, exclusionFile);

            Scanner scanner = new Scanner(exclusionFile);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                result.append(line).append("\n");
            }

            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            if (inputStream != null) {
                IOUtils.closeQuietly(inputStream);
            }
        }

        exlusionList.setValue(result.toString());
        exlusionList.setDescription("These classes will be excluded during the slicing analysis to prevent the slicer to go too deep into the java framework. Entries should be separated with new lines.");
        mongoTemplate.save(exlusionList);

    }

    /*
     * sources:
     * https://github.com/wala/WALA/blob/096e2f796f3351bf7c4f296ccd23b6fc98983d40/
     * com.ibm.wala.core/src/com/ibm/wala/ipa/callgraph/AnalysisOptions.java#L80
     * http://wala.sourceforge.net/wiki/index.php/UserGuide:Slicer
     */
    @ChangeSet(order = "02", author = "initiator", id = "02-addSlicerOptions")
    public void addSlicerOptions(MongoTemplate mongoTemplate) {

        /* Reflection Options */

        SlicerOption reflectionOption_FULL = new SlicerOption();
        reflectionOption_FULL.setType(SlicerOptionType.REFLECTION_OPTION);
        reflectionOption_FULL.setKey("FULL");
        reflectionOption_FULL.setDescription(
                "Analyzes all reflections, including 1: Flows from calls of newInstance() to casts, where a new instance is cast to a specific objet type. 2: Calls to Method.invoke(), where the underlying method represented by a method object is invoked. 3: Calls to Class.getMethod(), where a method object that reflects member method of the class is returned.");
        reflectionOption_FULL.setIsDefault(false);
        mongoTemplate.save(reflectionOption_FULL);

        SlicerOption reflectionOption_APPLICATION_GET_METHOD = new SlicerOption();
        reflectionOption_APPLICATION_GET_METHOD.setType(SlicerOptionType.REFLECTION_OPTION);
        reflectionOption_APPLICATION_GET_METHOD.setKey("APPLICATION_GET_METHOD");
        reflectionOption_APPLICATION_GET_METHOD.setDescription(
                "Like FULL, but models calls to Class.getMethod() only for application classes, meaning classes which are inside the android.jar-Archive.");
        reflectionOption_APPLICATION_GET_METHOD.setIsDefault(false);
        mongoTemplate.save(reflectionOption_APPLICATION_GET_METHOD);

        SlicerOption reflectionOption_NO_FLOW_TO_CASTS = new SlicerOption();
        reflectionOption_NO_FLOW_TO_CASTS.setType(SlicerOptionType.REFLECTION_OPTION);
        reflectionOption_NO_FLOW_TO_CASTS.setKey("NO_FLOW_TO_CASTS");
        reflectionOption_NO_FLOW_TO_CASTS.setDescription(
                "Does not analyze any flows from calls of newInstance() to casts, where a new instance is cast to a specific objet type.");
        reflectionOption_NO_FLOW_TO_CASTS.setIsDefault(false);
        mongoTemplate.save(reflectionOption_NO_FLOW_TO_CASTS);

        SlicerOption reflectionOption_NO_FLOW_TO_CASTS_APPLICATION_GET_METHOD = new SlicerOption();
        reflectionOption_NO_FLOW_TO_CASTS_APPLICATION_GET_METHOD.setType(SlicerOptionType.REFLECTION_OPTION);
        reflectionOption_NO_FLOW_TO_CASTS_APPLICATION_GET_METHOD.setKey("NO_FLOW_TO_CASTS_APPLICATION_GET_METHOD");
        reflectionOption_NO_FLOW_TO_CASTS_APPLICATION_GET_METHOD.setDescription(
                "Like FULL, but models calls to Class.getMethod() only for application classes and does not analyze any calls of newInstance() casts.");
                reflectionOption_NO_FLOW_TO_CASTS_APPLICATION_GET_METHOD.setIsDefault(false);
        mongoTemplate.save(reflectionOption_NO_FLOW_TO_CASTS_APPLICATION_GET_METHOD);

        SlicerOption reflectionOption_NO_METHOD_INVOKE = new SlicerOption();
        reflectionOption_NO_METHOD_INVOKE.setType(SlicerOptionType.REFLECTION_OPTION);
        reflectionOption_NO_METHOD_INVOKE.setKey("NO_METHOD_INVOKE");
        reflectionOption_NO_METHOD_INVOKE.setDescription(
                "Like FULL, but does not analyze any calls to Method.invoke(), where the underlying method represented by a method object is invoked.");
        reflectionOption_NO_METHOD_INVOKE.setIsDefault(false);
        mongoTemplate.save(reflectionOption_NO_METHOD_INVOKE);

        SlicerOption reflectionOption_NO_FLOW_TO_CASTS_NO_METHOD_INVOKE = new SlicerOption();
        reflectionOption_NO_FLOW_TO_CASTS_NO_METHOD_INVOKE.setType(SlicerOptionType.REFLECTION_OPTION);
        reflectionOption_NO_FLOW_TO_CASTS_NO_METHOD_INVOKE.setKey("NO_FLOW_TO_CASTS_NO_METHOD_INVOKE");
        reflectionOption_NO_FLOW_TO_CASTS_NO_METHOD_INVOKE.setDescription(
                "Does not analyze any flows from calls of newInstance() to casts or any calls to Method.invoke().");
        reflectionOption_NO_FLOW_TO_CASTS_NO_METHOD_INVOKE.setIsDefault(false);
        mongoTemplate.save(reflectionOption_NO_FLOW_TO_CASTS_NO_METHOD_INVOKE);

        SlicerOption reflectionOption_ONE_FLOW_TO_CASTS_NO_METHOD_INVOKE = new SlicerOption();
        reflectionOption_ONE_FLOW_TO_CASTS_NO_METHOD_INVOKE.setType(SlicerOptionType.REFLECTION_OPTION);
        reflectionOption_ONE_FLOW_TO_CASTS_NO_METHOD_INVOKE.setKey("ONE_FLOW_TO_CASTS_NO_METHOD_INVOKE");
        reflectionOption_ONE_FLOW_TO_CASTS_NO_METHOD_INVOKE.setDescription(
                "The analyzed number of flows from newInstance() calls to casts is limited to 1. Does not analyze any calls to Method.invoke().");
        reflectionOption_ONE_FLOW_TO_CASTS_NO_METHOD_INVOKE.setIsDefault(false);
        mongoTemplate.save(reflectionOption_ONE_FLOW_TO_CASTS_NO_METHOD_INVOKE);

        SlicerOption reflectionOption_ONE_FLOW_TO_CASTS_APPLICATION_GET_METHOD = new SlicerOption();
        reflectionOption_ONE_FLOW_TO_CASTS_APPLICATION_GET_METHOD.setType(SlicerOptionType.REFLECTION_OPTION);
        reflectionOption_ONE_FLOW_TO_CASTS_APPLICATION_GET_METHOD.setKey("ONE_FLOW_TO_CASTS_APPLICATION_GET_METHOD");
        reflectionOption_ONE_FLOW_TO_CASTS_APPLICATION_GET_METHOD.setDescription(
                "The analyzed number of flows from newInstance() calls to casts is limited to 1 and the algorithm models calls to Class.getMethod() only for application classes.");
                reflectionOption_ONE_FLOW_TO_CASTS_APPLICATION_GET_METHOD.setIsDefault(false);
        mongoTemplate.save(reflectionOption_ONE_FLOW_TO_CASTS_APPLICATION_GET_METHOD);

        SlicerOption reflectionOption_MULTI_FLOW_TO_CASTS_APPLICATION_GET_METHOD = new SlicerOption();
        reflectionOption_MULTI_FLOW_TO_CASTS_APPLICATION_GET_METHOD.setType(SlicerOptionType.REFLECTION_OPTION);
        reflectionOption_MULTI_FLOW_TO_CASTS_APPLICATION_GET_METHOD.setKey("MULTI_FLOW_TO_CASTS_APPLICATION_GET_METHOD");
        reflectionOption_MULTI_FLOW_TO_CASTS_APPLICATION_GET_METHOD.setDescription(
                "The analyzed number of flows from newInstance() calls to casts is limited to 100 and the algorithm models calls to Class.getMethod() only for application classes.");
                reflectionOption_MULTI_FLOW_TO_CASTS_APPLICATION_GET_METHOD.setIsDefault(false);
        mongoTemplate.save(reflectionOption_MULTI_FLOW_TO_CASTS_APPLICATION_GET_METHOD);

        SlicerOption reflectionOption_NO_STRING_CONSTANTS = new SlicerOption();
        reflectionOption_NO_STRING_CONSTANTS.setType(SlicerOptionType.REFLECTION_OPTION);
        reflectionOption_NO_STRING_CONSTANTS.setKey("NO_STRING_CONSTANTS");
        reflectionOption_NO_STRING_CONSTANTS.setDescription(
                "Like FULL, but ignores calls to reflective methods, which take string constant as arguments.");
        reflectionOption_NO_STRING_CONSTANTS.setIsDefault(false);
        mongoTemplate.save(reflectionOption_NO_STRING_CONSTANTS);

        SlicerOption reflectionOption_STRING_ONLY = new SlicerOption();
        reflectionOption_STRING_ONLY.setType(SlicerOptionType.REFLECTION_OPTION);
        reflectionOption_STRING_ONLY.setKey("STRING_ONLY");
        reflectionOption_STRING_ONLY.setDescription(
                "Does not analyze any flows from calls of newInstance() to casts or any calls to Method.invoke(), but only calls to reflective methods, which take string constant as arguments.");
                reflectionOption_STRING_ONLY.setIsDefault(false);
        mongoTemplate.save(reflectionOption_STRING_ONLY);

        SlicerOption reflectionOption_NONE = new SlicerOption();
        reflectionOption_NONE.setType(SlicerOptionType.REFLECTION_OPTION);
        reflectionOption_NONE.setKey("NONE");
        reflectionOption_NONE.setDescription(
                "Does not analyze any reflections. This is recommended since reflections are not used often in the android framework.");
        reflectionOption_NONE.setIsDefault(true);
        mongoTemplate.save(reflectionOption_NONE);

        /* Data Dependence Options */

        SlicerOption dataDependenceOptions_FULL = new SlicerOption();
        dataDependenceOptions_FULL.setType(SlicerOptionType.DATA_DEPENDENCE_OPTION);
        dataDependenceOptions_FULL.setKey("FULL");
        dataDependenceOptions_FULL.setDescription(
                "Tracks all data dependencies. Produces the largest SDG and uses the most computing resources.");
        dataDependenceOptions_FULL.setIsDefault(false);
        mongoTemplate.save(dataDependenceOptions_FULL);

        SlicerOption dataDependenceOptions_NO_BASE_PTRS = new SlicerOption();
        dataDependenceOptions_NO_BASE_PTRS.setType(SlicerOptionType.DATA_DEPENDENCE_OPTION);
        dataDependenceOptions_NO_BASE_PTRS.setKey("NO_BASE_PTRS");
        dataDependenceOptions_NO_BASE_PTRS.setDescription(
                "Ignore data dependence edges that define base pointers for indirect memory access. Base pointers are more commonly known as 'frame pointers' which point to the location where the stack pointer was, just before a method call moved the stack pointer to the methods own local variables.");
        dataDependenceOptions_NO_BASE_PTRS.setIsDefault(false);
        mongoTemplate.save(dataDependenceOptions_NO_BASE_PTRS);

        SlicerOption dataDependenceOptions_NO_HEAP = new SlicerOption();
        dataDependenceOptions_NO_HEAP.setType(SlicerOptionType.DATA_DEPENDENCE_OPTION);
        dataDependenceOptions_NO_HEAP.setKey("NO_HEAP");
        dataDependenceOptions_NO_HEAP
                .setDescription("Ignores all data dependence edges to and from heap locations, e.g. due to accessing an objects member variable. This recommended because heap data dependencies consume a large of amount of memory during analysis.");
        dataDependenceOptions_NO_HEAP.setIsDefault(true);
        mongoTemplate.save(dataDependenceOptions_NO_HEAP);

        SlicerOption dataDependenceOptions_NO_EXCEPTIONS = new SlicerOption();
        dataDependenceOptions_NO_EXCEPTIONS.setType(SlicerOptionType.DATA_DEPENDENCE_OPTION);
        dataDependenceOptions_NO_EXCEPTIONS.setKey("NO_EXCEPTIONS");
        dataDependenceOptions_NO_EXCEPTIONS
                .setDescription("Ignores all data dependence edges to and from throw and catch statements.");
        dataDependenceOptions_NO_EXCEPTIONS.setIsDefault(false);
        mongoTemplate.save(dataDependenceOptions_NO_EXCEPTIONS);

        SlicerOption dataDependenceOptions_NO_BASE_NO_EXCEPTIONS = new SlicerOption();
        dataDependenceOptions_NO_BASE_NO_EXCEPTIONS.setType(SlicerOptionType.DATA_DEPENDENCE_OPTION);
        dataDependenceOptions_NO_BASE_NO_EXCEPTIONS.setKey("NO_BASE_NO_EXCEPTIONS");
        dataDependenceOptions_NO_BASE_NO_EXCEPTIONS.setDescription(
                "Ignores data dependence edges that define base pointers and exclude all edges to/from throw and catch statements.");
        dataDependenceOptions_NO_BASE_NO_EXCEPTIONS.setIsDefault(false);
        mongoTemplate.save(dataDependenceOptions_NO_BASE_NO_EXCEPTIONS);

        SlicerOption dataDependenceOptions_NO_BASE_NO_HEAP = new SlicerOption();
        dataDependenceOptions_NO_BASE_NO_HEAP.setType(SlicerOptionType.DATA_DEPENDENCE_OPTION);
        dataDependenceOptions_NO_BASE_NO_HEAP.setKey("NO_BASE_NO_HEAP");
        dataDependenceOptions_NO_BASE_NO_HEAP.setDescription(
                "Ignores data dependence edges that define base pointers and exclude all edges to and from heap locations.");
        dataDependenceOptions_NO_BASE_NO_HEAP.setIsDefault(false);
        mongoTemplate.save(dataDependenceOptions_NO_BASE_NO_HEAP);

        SlicerOption dataDependenceOptions_NO_HEAP_NO_EXCEPTIONS = new SlicerOption();
        dataDependenceOptions_NO_HEAP_NO_EXCEPTIONS.setType(SlicerOptionType.DATA_DEPENDENCE_OPTION);
        dataDependenceOptions_NO_HEAP_NO_EXCEPTIONS.setKey("NO_HEAP_NO_EXCEPTIONS");
        dataDependenceOptions_NO_HEAP_NO_EXCEPTIONS.setDescription(
                "Ignore all data dependence edges to/from heap locations and and exclude all edges to/from throw and catch statements.");
        dataDependenceOptions_NO_HEAP_NO_EXCEPTIONS.setIsDefault(false);
        mongoTemplate.save(dataDependenceOptions_NO_HEAP_NO_EXCEPTIONS);

        SlicerOption dataDependenceOptions_NO_BASE_NO_HEAP_NO_EXCEPTIONS = new SlicerOption();
        dataDependenceOptions_NO_BASE_NO_HEAP_NO_EXCEPTIONS.setType(SlicerOptionType.DATA_DEPENDENCE_OPTION);
        dataDependenceOptions_NO_BASE_NO_HEAP_NO_EXCEPTIONS.setKey("NO_BASE_NO_HEAP_NO_EXCEPTIONS");
        dataDependenceOptions_NO_BASE_NO_HEAP_NO_EXCEPTIONS.setDescription(
                "Ignore data dependence edges that define base pointers, to/from heap locations and to/from throw and catch statements. Therefore only local, stack-based data dependence edges will be analyzed.");
        dataDependenceOptions_NO_BASE_NO_HEAP_NO_EXCEPTIONS.setIsDefault(false);
        mongoTemplate.save(dataDependenceOptions_NO_BASE_NO_HEAP_NO_EXCEPTIONS);

        SlicerOption dataDependenceOptions_REFLECTION = new SlicerOption();
        dataDependenceOptions_REFLECTION.setType(SlicerOptionType.DATA_DEPENDENCE_OPTION);
        dataDependenceOptions_REFLECTION.setKey("REFLECTION");
        dataDependenceOptions_REFLECTION.setDescription(
                "Like NO_BASE_NO_HEAP, but also excludes data dependence edges originating from checkcast instructions. The checkcast instruction is very similar to the instanceof instruction but throws an exception instead of returning a boolean. This is the dependence algorithm used to unsoundly track from newInstance to casts.");
        dataDependenceOptions_REFLECTION.setIsDefault(false);
        mongoTemplate.save(dataDependenceOptions_REFLECTION);

        SlicerOption dataDependenceOptions_NONE = new SlicerOption();
        dataDependenceOptions_NONE.setType(SlicerOptionType.DATA_DEPENDENCE_OPTION);
        dataDependenceOptions_NONE.setKey("NONE");
        dataDependenceOptions_NONE.setDescription(
                "Ignore all data dependencies. Produces the smallest SDG and uses the least computing resources.");
        dataDependenceOptions_NONE.setIsDefault(false);
        mongoTemplate.save(dataDependenceOptions_NONE);

        /* Control Dependence Options */

        SlicerOption controlDependenceOptions_FULL = new SlicerOption();
        controlDependenceOptions_FULL.setType(SlicerOptionType.CONTROL_DEPENDENCE_OPTION);
        controlDependenceOptions_FULL.setKey("FULL");
        controlDependenceOptions_FULL.setDescription("Track all control dependencies. This option should be preferred to analyse SecurityExceptions.");
        controlDependenceOptions_FULL.setIsDefault(false);
        mongoTemplate.save(controlDependenceOptions_FULL);

        SlicerOption controlDependenceOptions_NO_EXCEPTIONAL_EDGES = new SlicerOption();
        controlDependenceOptions_NO_EXCEPTIONAL_EDGES.setType(SlicerOptionType.CONTROL_DEPENDENCE_OPTION);
        controlDependenceOptions_NO_EXCEPTIONAL_EDGES.setKey("NO_EXCEPTIONAL_EDGES");
        controlDependenceOptions_NO_EXCEPTIONAL_EDGES
                .setDescription("Control dependencies transmitted via exception objects will be ignored. This is useful to reduce analysis costs but it does not to track SecurityExceptions.");
        controlDependenceOptions_NO_EXCEPTIONAL_EDGES.setIsDefault(true);
        mongoTemplate.save(controlDependenceOptions_NO_EXCEPTIONAL_EDGES);

        SlicerOption controlDependenceOptions_NONE = new SlicerOption();
        controlDependenceOptions_NONE.setType(SlicerOptionType.CONTROL_DEPENDENCE_OPTION);
        controlDependenceOptions_NONE.setKey("NONE");
        controlDependenceOptions_NONE.setDescription("Ignore all control dependencies. Produces the smallest and fastest slices.");
        controlDependenceOptions_NONE.setIsDefault(false);
        mongoTemplate.save(controlDependenceOptions_NONE);
    }

    /*
     * sources:
     * http://wala.sourceforge.net/javadocs/trunk/com/ibm/wala/ipa/callgraph/impl/Util.html
     */
    @ChangeSet(order = "03", author = "initiator", id = "03-addCFAOptions")
    public void addCFAOptions(MongoTemplate mongoTemplate) {
        CFAOption zeroCFA = new CFAOption();
        zeroCFA.setType(CFAType.ZERO_CFA);
        zeroCFA.setDescription("Creates a 0-CFA call graph builder. Fastest and context-insensitive analyses. There is only one single method reference- and instance-context. Therefore it does not distinguish between different instances of the same object type and therefore might lack precision.");
        zeroCFA.setIsDefault(false);
        mongoTemplate.save(zeroCFA);

        CFAOption zeroOneCFA = new CFAOption();
        zeroOneCFA.setType(CFAType.ZERO_ONE_CFA);
        zeroOneCFA.setDescription("Creates a 0-1-CFA call graph builder. More expensive than 0-CFA since a call stack based instance-context is created for every object, with one level of call stack tracing. There is still only a single level of method reference-context, meaning 0 level of call stack tracing. Appropriate for most slicing szenarios since it's the best tradeoff between precision and computing resources.");
        zeroOneCFA.setIsDefault(true);
        mongoTemplate.save(zeroOneCFA);

        CFAOption vanillaZeroOneCFA = new CFAOption();
        vanillaZeroOneCFA.setType(CFAType.VANILLA_ZERO_ONE_CFA);
        vanillaZeroOneCFA.setDescription("Creates 0-1-CFA Call graph builder. With 1 level call stack tracing for object instances and no call stack tracing for method references. Standard optimizations in the heap abstraction like smushing of strings, meaning allocation sites for Strings and StringBuffers are not disambiguated, are disabled.");
        vanillaZeroOneCFA.setIsDefault(false);
        mongoTemplate.save(vanillaZeroOneCFA);

        CFAOption nCFA = new CFAOption();
        nCFA.setType(CFAType.N_CFA);
        nCFA.setDescription("Creates a call graph builder that uses call stack context-sensitivity for method references and a call stack context-sensitive allocation-site-based heap abstraction for different instance contexts. The with call stack tracing is limited to n methods.");
        nCFA.setIsDefault(false);
        mongoTemplate.save(nCFA);

        CFAOption vanillaNcFA = new CFAOption();
        vanillaNcFA.setType(CFAType.VANILLA_N_CFA);
        vanillaNcFA.setDescription("Creates a call graph builder that uses call stack context-sensitivity for method references and a call stack context-sensitive allocation-site-based heap abstraction for different instance contexts. The with call stack tracing is limited to n methods. Standard optimizations in the heap abstraction like smushing of strings, meaning allocation sites for Strings and StringBuffers are not disambiguated, are disabled.");
        vanillaNcFA.setIsDefault(false);
        mongoTemplate.save(vanillaNcFA);

        CFAOption zeroContainerCFA = new CFAOption();
        zeroContainerCFA.setType(CFAType.ZERO_CONTAINER_CFA);
        zeroContainerCFA.setDescription("Creates a 0-CFA call graph builder augmented with extra logic for containers. There is only one single method reference- and instance-context. Therefore it does not distinguish between different instances of the same object type and therefore might lack precision. Method calls on container objects, like List, are distinguished by the allocation site of the receiver object, meaning the container the method was called upon.");
        zeroContainerCFA.setIsDefault(false);
        mongoTemplate.save(zeroContainerCFA);

        CFAOption zeroOneContainerCFA = new CFAOption();
        zeroOneContainerCFA.setType(CFAType.ZERO_ONE_CONTAINER_CFA);
        zeroOneContainerCFA.setDescription("Creates a 0-1-CFA call graph builder augmented with extra logic for containers. Uses a call stack based instance-context created for every object, with one level of call stack tracing. There is still only a single level of method reference-context, meaning 0 level of call stack tracing. Method calls on container objects, like List, are distinguished by the allocation site of the receiver object, meaning the container the method was called upon.");
        zeroOneContainerCFA.setIsDefault(false);
        mongoTemplate.save(zeroOneContainerCFA);

        CFAOption vanillaZeroOneContainerCFA = new CFAOption();
        vanillaZeroOneContainerCFA.setType(CFAType.VANILLA_ZERO_ONE_CONTAINER_CFA);
        vanillaZeroOneContainerCFA.setDescription("Creates a 0-1-CFA call graph builder augmented with extra logic for containers. Uses a call stack based instance-context created for every object, with one level of call stack tracing. There is still only a single level of method reference-context, meaning 0 level of call stack tracing. Method calls on container objects, like List, are distinguished by the allocation site of the receiver object, meaning the container the method was called upon. Standard optimizations in the heap abstraction like smushing of strings, meaning allocation sites for Strings and StringBuffers are not disambiguated, are disabled");
        vanillaZeroOneContainerCFA.setIsDefault(false);
        mongoTemplate.save(vanillaZeroOneContainerCFA);
    }
}
