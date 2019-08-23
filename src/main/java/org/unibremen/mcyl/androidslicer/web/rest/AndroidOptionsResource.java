package org.unibremen.mcyl.androidslicer.web.rest;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.unibremen.mcyl.androidslicer.config.Constants;
import org.unibremen.mcyl.androidslicer.domain.SlicerSetting;
import org.unibremen.mcyl.androidslicer.repository.SlicerSettingRepository;
import org.unibremen.mcyl.androidslicer.web.rest.errors.BadRequestAlertException;
import org.unibremen.mcyl.androidslicer.web.rest.vm.AndroidServiceClassesVM;
import org.unibremen.mcyl.androidslicer.web.rest.vm.AndroidVersionVM;

/**
 * REST controller for managing SlicerSetting.
 */
@RestController
@RequestMapping("/api")
public class AndroidOptionsResource {

    private final Logger log = LoggerFactory.getLogger(AndroidOptionsResource.class);

    private static final String ENTITY_NAME = "options";

    private final SlicerSettingRepository slicerSettingRepository;

    private static final Pattern sourceFilePathPattern = Pattern.compile("android-\\d+");
    private static final Pattern methodPattern = Pattern.compile("\\s([a-zA-Z]+)\\(");

    public AndroidOptionsResource(SlicerSettingRepository slicerSettingRepository) {
        this.slicerSettingRepository = slicerSettingRepository;
    }

    /**
     * GET /android-options/android-versions : get available android source
     * versions.
     *
     * @return the ResponseEntity with status 200 (OK) and with body the
     *         AndroidVersionVM, or with status 404 (Not Found)
     */

    @GetMapping("/android-options/android-versions")
    public ResponseEntity<List<AndroidVersionVM>> getAndroidVersions() {
        log.debug("REST request to get android versions");

        SlicerSetting androidSourcesPathSetting = 
            slicerSettingRepository.findOneByKey(Constants.ANDROID_SOURCE_PATH_KEY).get();
        
        String androidSourcesPath = "";
        if(androidSourcesPathSetting != null){
            androidSourcesPath = androidSourcesPathSetting.getValue();
        }
        
        if (!androidSourcesPath.isEmpty()) {
            // get android versions from folder names (e.g. android-28 -> 28)
            File file = new File(androidSourcesPath);
            File[] directories = file.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File current, String name) {
                    return new File(current, name).isDirectory();
                }
            });

            if(directories != null && directories.length > 0){
                List<AndroidVersionVM> androidVersions = new ArrayList<AndroidVersionVM>();
                for (File directory : directories) {
                    if(directory.getName().contains("-")){
                        AndroidVersionVM androidVersion = new AndroidVersionVM();
                        androidVersion.setKey(Integer.parseInt(directory.getName().split("-")[1]));
                        androidVersion.setPath(directory.getAbsolutePath());
                        androidVersions.add(androidVersion);
                    }
                }
                return ResponseEntity.ok().body(androidVersions);
            }     
        }
        throw new BadRequestAlertException("Android Sources not found in " + androidSourcesPath + " !", ENTITY_NAME, "idnull");
    }

    /**
     * GET /android-options/system-services : get android system services inside
     * "androidSourceFolderPath".
     *
     * @param androidSourceFolderPath android source folder path
     * @return the ResponseEntity with status 200 (OK) and with body the service
     *         files, or with status 404 (Not Found)
     */
    @GetMapping("/android-options/system-services")
    public ResponseEntity<List<AndroidServiceClassesVM>> getAndroidServices(
            @RequestParam("path") String androidSourceFolderPath) {
        log.debug("REST request to get android services");

        SlicerSetting serviceRegex = slicerSettingRepository.findOneByKey(Constants.SERVICE_REGEX_KEY).get();
        File androidSourceFolder = new File(androidSourceFolderPath);

        if (serviceRegex != null && androidSourceFolder.exists()) {
            Collection<File> serviceClassFiles = FileUtils.listFiles(androidSourceFolder,
                    new RegexFileFilter(serviceRegex.getValue()), TrueFileFilter.INSTANCE);

            List<AndroidServiceClassesVM> serviceClasses = new ArrayList<AndroidServiceClassesVM>();
            for (File serviceClassFile : serviceClassFiles) {
                AndroidServiceClassesVM serviceClass = new AndroidServiceClassesVM();

                // get package path and file name
                serviceClass.setName(androidSourceFolder.toURI().relativize(serviceClassFile.toURI()).getPath());
                serviceClass.setPath(serviceClassFile.getAbsolutePath());

                serviceClasses.add(serviceClass);
            }

            return ResponseEntity.ok().body(serviceClasses);
        }
        throw new BadRequestAlertException("Android services not found in " + androidSourceFolderPath +"!", ENTITY_NAME, "idnull");
    }

    /**
     * GET /android-options/source-file : get android system service code from
     * "androidVersion" and "androidClassName".
     *
     * @param androidVersion android version
     * @param androidClassName name of the class (fully qualified, i.e. com/android/server/AlarmManagerService.java)
     * @return the ResponseEntity with status 200 (OK) and with body the source
     *         file, or with status 404 (Not Found)
     */
    @GetMapping("/android-options/source-file")
    public ResponseEntity<String> getAndroidSourceFile(@RequestParam("version") Integer androidVersion, @RequestParam("name") String androidClassName) {
        log.debug("REST request to get android source file");

        String sourceFilePath = slicerSettingRepository
        .findOneByKey(Constants.ANDROID_SOURCE_PATH_KEY).get()
        + File.separator
        + "android-"
        + androidVersion
        + File.separator
        + androidClassName.replace("/", File.separator);

        File file = new File(sourceFilePath);
        if (file.exists()) {

            StringBuilder result = new StringBuilder("");
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    result.append(line).append("\n");
                }
                scanner.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(org.springframework.http.MediaType.TEXT_PLAIN);
            return new ResponseEntity<String>(result.toString(), httpHeaders, HttpStatus.OK);
        }
        throw new BadRequestAlertException("Source File not found in " + sourceFilePath + " !", ENTITY_NAME, "idnull");
    }

    /**
     * GET /android-options/entry-methods : get service's public methods based on
     * the AIDL specification.
     *
     * @param serviceClassName android system service name
     * @param sourceFilePath   file path for system service
     * @return the ResponseEntity with status 200 (OK) and with body the source
     *         file, or with status 404 (Not Found)
     */
    @GetMapping("/android-options/entry-methods")
    public ResponseEntity<List<String>> getServiceEntryMethods(@RequestParam("name") String serviceClassName,
            @RequestParam("path") String sourceFilePath) {
        log.debug("REST request to get get service's public methods based on the AIDL specification");

        // trim system service file path down to source root (i.e. .../android-xx/)

        Matcher sourceFilePathMatcher = sourceFilePathPattern.matcher(sourceFilePath);
        if (sourceFilePathMatcher.find()) {

            String androidRootPath = sourceFilePath.substring(0, sourceFilePathMatcher.end()) + File.separator;

            File androidSourceFolder = new File(androidRootPath);
            if (androidSourceFolder.exists()) {

                // construct AIDl file name from service class name
                // (e.g. com/android/server/AlarmManagerService.java -> IAlarmManager.aidl
                final String aidlFileName = "I" + FilenameUtils.removeExtension(
                        serviceClassName.substring(serviceClassName.lastIndexOf("/") + 1, serviceClassName.length()))
                        .replace("Service", "") + ".aidl";

                // find the file
                File aidlFile = null;
                Iterator<File> fileSearcher = FileUtils.iterateFiles(androidSourceFolder, null, true);
                while (fileSearcher.hasNext()) {
                    File currentFile = fileSearcher.next();
                    if (currentFile.getName().equals(aidlFileName)) {
                        aidlFile = currentFile;
                        break;
                    }
                }

                // get method names
                if (aidlFile != null && aidlFile.exists()) {
                    List<String> methodNames = new ArrayList<String>();
                    try (Scanner scanner = new Scanner(aidlFile)) {
                        while (scanner.hasNextLine()) {
                            String line = scanner.nextLine();
                            Matcher methodMatcher = methodPattern.matcher(line);
                            if(methodMatcher.find()){
                                methodNames.add(methodMatcher.group(1));
                            }
                        }
                        scanner.close();
                    } catch (IOException e) {
                        throw new BadRequestAlertException("Error parsing AIDL File", ENTITY_NAME, "idnull");
                    }

                    if(!methodNames.isEmpty()){
                        return ResponseEntity.ok().body(methodNames);
                    }
                }
                else{
                    throw new BadRequestAlertException("AIDL File " + aidlFileName + " not found in " + androidRootPath + " !", ENTITY_NAME, "idnull");
                }
            }
            else{
                throw new BadRequestAlertException("Android source folder not found in " + androidRootPath + " !", ENTITY_NAME, "idnull");
            }
        }
        throw new BadRequestAlertException("AIDL File not found for " + sourceFilePath + " !", ENTITY_NAME, "idnull");
    }

    /**
     * GET /android-options/seed-statements : get default seed statements.
     *
     * @return the ResponseEntity with status 200 (OK) and with body the seed
     *         statement list, or with status 404 (Not Found)
     */
    @GetMapping("/android-options/seed-statements")
    public ResponseEntity<List<String>> getAndroidSeedStatements() {
        log.debug("REST request to get seed statements");
        SlicerSetting seedStatements = slicerSettingRepository.findOneByKey(Constants.SEED_STATEMENTS_KEY).get();
        if (seedStatements != null) {
            return ResponseEntity.ok().body(Arrays.asList(seedStatements.getValue().replace(" ", "").split(";")));
        }
        throw new BadRequestAlertException("Seed Statements not found", ENTITY_NAME, "idnull");
    }
}
