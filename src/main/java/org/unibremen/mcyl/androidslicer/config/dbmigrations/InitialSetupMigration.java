package org.unibremen.mcyl.androidslicer.config.dbmigrations;

import org.unibremen.mcyl.androidslicer.security.AuthoritiesConstants;

import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.unibremen.mcyl.androidslicer.config.Constants;
import org.unibremen.mcyl.androidslicer.domain.SlicerSetting;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Scanner;

/**
 * Creates the initial database setup
 */
@ChangeLog(order = "001")
public class InitialSetupMigration {

    @ChangeSet(order = "01", author = "initiator", id = "03-addDefaultSettings")
    public void addDefaultSettings(MongoTemplate mongoTemplate) {
        SlicerSetting androidSourcePath = new SlicerSetting();
        androidSourcePath.setKey(Constants.ANDROID_SOURCE_PATH_KEY);
        androidSourcePath.setValue("C:\\Daten\\Downloads\\sources");
        mongoTemplate.save(androidSourcePath);

        SlicerSetting androidPlatformPath = new SlicerSetting();
        androidPlatformPath.setKey(Constants.ANDROID_PLATFORM_PATH_KEY);
        androidPlatformPath.setValue("C:\\Daten\\Downloads\\platforms");
        mongoTemplate.save(androidPlatformPath);

        SlicerSetting serviceRegex = new SlicerSetting();
        serviceRegex.setKey(Constants.SERVICE_REGEX_KEY);
        serviceRegex.setValue(".*ManagerService.java");
        mongoTemplate.save(serviceRegex);

        SlicerSetting seedStatements = new SlicerSetting();
        seedStatements.setKey(Constants.SEED_STATEMENTS_KEY);
        seedStatements.setValue("checkPermission; checkCallingPermission; checkCallingOrSelfPermission; enforcePermissionen; enforceCallingPermission; enforceCallingOrSelfPermission; SecurityException");
        mongoTemplate.save(seedStatements);

        SlicerSetting exlusionList = new SlicerSetting();
        exlusionList.setKey(Constants.EXCLUSION_LIST_KEY);
        StringBuilder result = new StringBuilder("");

        // Get file from resources folder
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("wala/ExclusionFile.txt").getFile());

        try (Scanner scanner = new Scanner(file)) {

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                result.append(line).append("\n");
            }

            scanner.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        exlusionList.setValue(result.toString());
        mongoTemplate.save(exlusionList);
    }
}
