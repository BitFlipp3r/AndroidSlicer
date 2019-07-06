package org.unibremen.mcyl.androidslicer.config.dbmigrations;

import org.unibremen.mcyl.androidslicer.security.AuthoritiesConstants;

import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.unibremen.mcyl.androidslicer.config.Constants;
import org.unibremen.mcyl.androidslicer.domain.SlicerOption;
import org.unibremen.mcyl.androidslicer.domain.SlicerSetting;
import org.unibremen.mcyl.androidslicer.domain.enumeration.SlicerOptionType;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Scanner;

/**
 * Creates the initial database setup
 */
@ChangeLog(order = "001")
public class InitialSetupMigration {

    @ChangeSet(order = "01", author = "initiator", id = "01-addDefaultSettings")
    public void addDefaultSettings(MongoTemplate mongoTemplate) {
        SlicerSetting androidSourcePath = new SlicerSetting();
        androidSourcePath.setKey(Constants.ANDROID_SOURCE_PATH_KEY);
        androidSourcePath.setValue("~\\android-resources");
        mongoTemplate.save(androidSourcePath);

        SlicerSetting androidPlatformPath = new SlicerSetting();
        androidPlatformPath.setKey(Constants.ANDROID_PLATFORM_PATH_KEY);
        androidPlatformPath.setValue("~\\android-resources");
        mongoTemplate.save(androidPlatformPath);

        SlicerSetting serviceRegex = new SlicerSetting();
        serviceRegex.setKey(Constants.SERVICE_REGEX_KEY);
        serviceRegex.setValue(".*ManagerService.java");
        mongoTemplate.save(serviceRegex);

        SlicerSetting seedStatements = new SlicerSetting();
        seedStatements.setKey(Constants.SEED_STATEMENTS_KEY);
        seedStatements.setValue(
                "checkPermission; checkCallingPermission; checkCallingOrSelfPermission; enforcePermissionen; enforceCallingPermission; enforceCallingOrSelfPermission; SecurityException");
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
        SlicerOption reflectionOption_FULL = new SlicerOption();
        reflectionOption_FULL.setType(SlicerOptionType.REFLECTION_OPTION);
        reflectionOption_FULL.setKey("FULL");
        reflectionOption_FULL.setDescription("Analyzes Integer.MAX_VALUE flows from newInstance() calls to casts.");
        reflectionOption_FULL.setIsDefault(false);
        mongoTemplate.save(reflectionOption_FULL);

        SlicerOption reflectionOption_NO_FLOW_TO_CASTS = new SlicerOption();
        reflectionOption_NO_FLOW_TO_CASTS.setType(SlicerOptionType.REFLECTION_OPTION);
        reflectionOption_NO_FLOW_TO_CASTS.setKey("NO_FLOW_TO_CASTS");
        reflectionOption_NO_FLOW_TO_CASTS.setDescription("Analyzes no flows from newInstance() calls to casts.");
        reflectionOption_NO_FLOW_TO_CASTS.setIsDefault(false);
        mongoTemplate.save(reflectionOption_NO_FLOW_TO_CASTS);

        SlicerOption reflectionOption_NO_FLOW_TO_CASTS_NO_METHOD_INVOKE = new SlicerOption();
        reflectionOption_NO_FLOW_TO_CASTS_NO_METHOD_INVOKE.setType(SlicerOptionType.REFLECTION_OPTION);
        reflectionOption_NO_FLOW_TO_CASTS_NO_METHOD_INVOKE.setKey("NO_FLOW_TO_CASTS_NO_METHOD_INVOKE");
        reflectionOption_NO_FLOW_TO_CASTS_NO_METHOD_INVOKE.setDescription(
                "Analyzes no flows from newInstance() calls to casts and ignores calls to Method.invoke().");
        reflectionOption_NO_FLOW_TO_CASTS_NO_METHOD_INVOKE.setIsDefault(false);
        mongoTemplate.save(reflectionOption_NO_FLOW_TO_CASTS_NO_METHOD_INVOKE);

        SlicerOption reflectionOption_NO_METHOD_INVOKE = new SlicerOption();
        reflectionOption_NO_METHOD_INVOKE.setType(SlicerOptionType.REFLECTION_OPTION);
        reflectionOption_NO_METHOD_INVOKE.setKey("NO_METHOD_INVOKE");
        reflectionOption_NO_METHOD_INVOKE.setDescription(
                "Analyzes Integer.MAX_VALUE flows from newInstance() calls to casts and ignores calls to Method.invoke().");
        reflectionOption_NO_METHOD_INVOKE.setIsDefault(false);
        mongoTemplate.save(reflectionOption_NO_METHOD_INVOKE);

        SlicerOption reflectionOption_NO_STRING_CONSTANTS = new SlicerOption();
        reflectionOption_NO_STRING_CONSTANTS.setType(SlicerOptionType.REFLECTION_OPTION);
        reflectionOption_NO_STRING_CONSTANTS.setKey("NO_STRING_CONSTANTS");
        reflectionOption_NO_STRING_CONSTANTS.setDescription(
                "Analyzes Integer.MAX_VALUE flows from newInstance() calls to casts and ignores calls to reflective methods with String constant arguments.");
        reflectionOption_NO_STRING_CONSTANTS.setIsDefault(false);
        mongoTemplate.save(reflectionOption_NO_STRING_CONSTANTS);

        SlicerOption reflectionOption_NONE = new SlicerOption();
        reflectionOption_NONE.setType(SlicerOptionType.REFLECTION_OPTION);
        reflectionOption_NONE.setKey("NONE");
        reflectionOption_NONE.setDescription(
                "Analyzes no flows from newInstance() calls to casts and ignores calls to Method.invoke() and ignores calls to reflective methods with String constant arguments.");
        reflectionOption_NONE.setIsDefault(true);
        mongoTemplate.save(reflectionOption_NONE);

        SlicerOption reflectionOption_FONE_FLOW_TO_CASTS_NO_METHOD_INVOKE = new SlicerOption();
        reflectionOption_FONE_FLOW_TO_CASTS_NO_METHOD_INVOKE.setType(SlicerOptionType.REFLECTION_OPTION);
        reflectionOption_FONE_FLOW_TO_CASTS_NO_METHOD_INVOKE.setKey("ONE_FLOW_TO_CASTS_NO_METHOD_INVOKE");
        reflectionOption_FONE_FLOW_TO_CASTS_NO_METHOD_INVOKE.setDescription(
                "Analyzes one flow from newInstance() calls to casts and and ignores calls to Method.invoke().");
        reflectionOption_FONE_FLOW_TO_CASTS_NO_METHOD_INVOKE.setIsDefault(false);
        mongoTemplate.save(reflectionOption_FONE_FLOW_TO_CASTS_NO_METHOD_INVOKE);

        SlicerOption dataDependenceOptions_FULL = new SlicerOption();
        dataDependenceOptions_FULL.setType(SlicerOptionType.DATA_DEPENDENCE_OPTION);
        dataDependenceOptions_FULL.setKey("FULL");
        dataDependenceOptions_FULL.setDescription(
                "Tracks all data dependencies. Produces the largest SDG and uses the most computing resouces.");
        dataDependenceOptions_FULL.setIsDefault(false);
        mongoTemplate.save(dataDependenceOptions_FULL);

        SlicerOption dataDependenceOptions_NO_BASE_NO_EXCEPTIONS = new SlicerOption();
        dataDependenceOptions_NO_BASE_NO_EXCEPTIONS.setType(SlicerOptionType.DATA_DEPENDENCE_OPTION);
        dataDependenceOptions_NO_BASE_NO_EXCEPTIONS.setKey("NO_BASE_NO_EXCEPTIONS");
        dataDependenceOptions_NO_BASE_NO_EXCEPTIONS.setDescription(
                "Like FULL, but ignore data dependence edges that define base pointers for indirect memory access and ignore all data dependence edges to/from throw and catch statements.");
        dataDependenceOptions_NO_BASE_NO_EXCEPTIONS.setIsDefault(false);
        mongoTemplate.save(dataDependenceOptions_NO_BASE_NO_EXCEPTIONS);

        SlicerOption dataDependenceOptions_NO_BASE_NO_HEAP = new SlicerOption();
        dataDependenceOptions_NO_BASE_NO_HEAP.setType(SlicerOptionType.DATA_DEPENDENCE_OPTION);
        dataDependenceOptions_NO_BASE_NO_HEAP.setKey("NO_BASE_NO_HEAP");
        dataDependenceOptions_NO_BASE_NO_HEAP.setDescription(
                "Like NO_BASE_PTS, and additionally ignore all data dependence edges to/from heap locations.");
        dataDependenceOptions_NO_BASE_NO_HEAP.setIsDefault(false);
        mongoTemplate.save(dataDependenceOptions_NO_BASE_NO_HEAP);

        SlicerOption dataDependenceOptions_NO_BASE_NO_HEAP_NO_EXCEPTIONS = new SlicerOption();
        dataDependenceOptions_NO_BASE_NO_HEAP_NO_EXCEPTIONS.setType(SlicerOptionType.DATA_DEPENDENCE_OPTION);
        dataDependenceOptions_NO_BASE_NO_HEAP_NO_EXCEPTIONS.setKey("NO_BASE_NO_HEAP_NO_EXCEPTIONS");
        dataDependenceOptions_NO_BASE_NO_HEAP_NO_EXCEPTIONS.setDescription(
                "Like FULL, but ignore data dependence edges that define base pointers for indirect memory access and ignore all data dependence edges to/from heap locations and ignore all data dependence edges to/from throw and catch statements.");
        dataDependenceOptions_NO_BASE_NO_HEAP_NO_EXCEPTIONS.setIsDefault(false);
        mongoTemplate.save(dataDependenceOptions_NO_BASE_NO_HEAP_NO_EXCEPTIONS);

        SlicerOption dataDependenceOptions_NO_BASE_PTRS = new SlicerOption();
        dataDependenceOptions_NO_BASE_PTRS.setType(SlicerOptionType.DATA_DEPENDENCE_OPTION);
        dataDependenceOptions_NO_BASE_PTRS.setKey("NO_BASE_PTRS");
        dataDependenceOptions_NO_BASE_PTRS.setDescription(
                "Like FULL, but ignore data dependence edges that define base pointers for indirect memory access.");
        dataDependenceOptions_NO_BASE_PTRS.setIsDefault(false);
        mongoTemplate.save(dataDependenceOptions_NO_BASE_PTRS);

        SlicerOption dataDependenceOptions_NO_EXCEPTIONS = new SlicerOption();
        dataDependenceOptions_NO_EXCEPTIONS.setType(SlicerOptionType.DATA_DEPENDENCE_OPTION);
        dataDependenceOptions_NO_EXCEPTIONS.setKey("NO_EXCEPTIONS");
        dataDependenceOptions_NO_EXCEPTIONS
                .setDescription("Like FULL, ignore all data dependence edges to/from throw and catch statements.");
        dataDependenceOptions_NO_EXCEPTIONS.setIsDefault(false);
        mongoTemplate.save(dataDependenceOptions_NO_EXCEPTIONS);

        SlicerOption dataDependenceOptions_NO_HEAP = new SlicerOption();
        dataDependenceOptions_NO_HEAP.setType(SlicerOptionType.DATA_DEPENDENCE_OPTION);
        dataDependenceOptions_NO_HEAP.setKey("NO_HEAP");
        dataDependenceOptions_NO_HEAP
                .setDescription("Like FULL, and additionally ignore all data dependence edges to/from heap locations.");
        dataDependenceOptions_NO_HEAP.setIsDefault(false);
        mongoTemplate.save(dataDependenceOptions_NO_HEAP);

        SlicerOption dataDependenceOptions_NO_HEAP_NO_EXCEPTIONS = new SlicerOption();
        dataDependenceOptions_NO_HEAP_NO_EXCEPTIONS.setType(SlicerOptionType.DATA_DEPENDENCE_OPTION);
        dataDependenceOptions_NO_HEAP_NO_EXCEPTIONS.setKey("NO_HEAP_NO_EXCEPTIONS");
        dataDependenceOptions_NO_HEAP_NO_EXCEPTIONS.setDescription(
                "Like FULL, and additionally ignore all data dependence edges to/from heap locations and ignore all data dependence edges to/from throw and catch statements.");
        dataDependenceOptions_NO_HEAP_NO_EXCEPTIONS.setIsDefault(false);
        mongoTemplate.save(dataDependenceOptions_NO_HEAP_NO_EXCEPTIONS);

        SlicerOption dataDependenceOptions_NONE = new SlicerOption();
        dataDependenceOptions_NONE.setType(SlicerOptionType.DATA_DEPENDENCE_OPTION);
        dataDependenceOptions_NONE.setKey("NONE");
        dataDependenceOptions_NONE.setDescription(
                "Ignore all data dependencies. Produces the smallest SDG and uses the least computing resouces.");
        dataDependenceOptions_NONE.setIsDefault(true);
        mongoTemplate.save(dataDependenceOptions_NONE);

        SlicerOption dataDependenceOptions_REFLECTION = new SlicerOption();
        dataDependenceOptions_REFLECTION.setType(SlicerOptionType.DATA_DEPENDENCE_OPTION);
        dataDependenceOptions_REFLECTION.setKey("REFLECTION");
        dataDependenceOptions_REFLECTION.setDescription(
                "Like NO_BASE_NO_HEAP, but also ignore data dependence edges originating from checkcast statements. This is the dependence algorithm used to unsoundly track from newInstance to casts.");
        dataDependenceOptions_REFLECTION.setIsDefault(false);
        mongoTemplate.save(dataDependenceOptions_REFLECTION);

        SlicerOption controlDependenceOptions_FULL = new SlicerOption();
        controlDependenceOptions_FULL.setType(SlicerOptionType.CONTROL_DEPENDENCE_OPTION);
        controlDependenceOptions_FULL.setKey("FULL");
        controlDependenceOptions_FULL.setDescription("Track all control dependencies.");
        controlDependenceOptions_FULL.setIsDefault(false);
        mongoTemplate.save(controlDependenceOptions_FULL);

        SlicerOption controlDependenceOptions_NO_EXCEPTIONAL_EDGES = new SlicerOption();
        controlDependenceOptions_NO_EXCEPTIONAL_EDGES.setType(SlicerOptionType.CONTROL_DEPENDENCE_OPTION);
        controlDependenceOptions_NO_EXCEPTIONAL_EDGES.setKey("NO_EXCEPTIONAL_EDGES");
        controlDependenceOptions_NO_EXCEPTIONAL_EDGES
                .setDescription("Data dependencies transmitted via exception objects will be ignored.");
        controlDependenceOptions_NO_EXCEPTIONAL_EDGES.setIsDefault(true);
        mongoTemplate.save(controlDependenceOptions_NO_EXCEPTIONAL_EDGES);

        SlicerOption controlDependenceOptions_NONE = new SlicerOption();
        controlDependenceOptions_NONE.setType(SlicerOptionType.CONTROL_DEPENDENCE_OPTION);
        controlDependenceOptions_NONE.setKey("NONE");
        controlDependenceOptions_NONE.setDescription("Ignore all control dependencies.");
        controlDependenceOptions_NONE.setIsDefault(false);
        mongoTemplate.save(controlDependenceOptions_NONE);
    }
}
