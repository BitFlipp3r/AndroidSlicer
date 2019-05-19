package org.unibremen.mcyl.androidslicer.web.rest;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;


import org.apache.commons.io.FileUtils;
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

    public AndroidOptionsResource(SlicerSettingRepository slicerSettingRepository) {
        this.slicerSettingRepository = slicerSettingRepository;
    }

    /**
     * GET /android-options/android-versions : get available android source versions.
     *
     * @return the ResponseEntity with status 200 (OK) and with body the
     *         AndroidVersionVM, or with status 404 (Not Found)
     */

    @GetMapping("/android-options/android-versions")
    public ResponseEntity<List<AndroidVersionVM>> getAndroidVersions() {
        log.debug("REST request to get android versions");
        SlicerSetting androidSourcesPath = slicerSettingRepository.findOneByKey(Constants.ANDROID_SOURCE_PATH_KEY).get();
        if (androidSourcesPath != null) {
            // get android versions from folder names (e.g. android-28 -> 28)
            File file = new File(androidSourcesPath.getValue());
            File[] directories = file.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File current, String name) {
                    return new File(current, name).isDirectory();
                }
            });

            List<AndroidVersionVM> androidVersions = new ArrayList<AndroidVersionVM>();
            for (File directory : directories) {
                AndroidVersionVM androidVersion = new AndroidVersionVM();
                androidVersion.setKey(Integer.parseInt(directory.getName().split("-")[1]));
                androidVersion.setPath(directory.getAbsolutePath());
                androidVersions.add(androidVersion);
            }

            return ResponseEntity.ok().body(androidVersions);
        }
        throw new BadRequestAlertException("Anroid Sources not found", ENTITY_NAME, "idnull");
    }

    /**
     * GET /android-options/system-services : get android system services inside "path".
     *
     * @param path android source folder path
     * @return the ResponseEntity with status 200 (OK) and with body the service
     *         files, or with status 404 (Not Found)
     */
    @GetMapping("/android-options/system-services")
    public ResponseEntity<List<AndroidServiceClassesVM>> getAndroidServices(@RequestParam("path") String path) {
        log.debug("REST request to get android services");
        SlicerSetting serviceRegex = slicerSettingRepository.findOneByKey(Constants.SERVICE_REGEX_KEY).get();
        File androidSourceFolder = new File(path);
        if (serviceRegex != null && androidSourceFolder.exists()) {
            Collection<File> serviceClassFiles = FileUtils.listFiles(androidSourceFolder,
                    new RegexFileFilter(serviceRegex.getValue()), TrueFileFilter.INSTANCE);

            List<AndroidServiceClassesVM> serviceClasses = new ArrayList<AndroidServiceClassesVM>();
            for (File serviceClassFile : serviceClassFiles) {
                AndroidServiceClassesVM serviceClass = new AndroidServiceClassesVM();
                serviceClass.setName(androidSourceFolder.toURI().relativize(serviceClassFile.toURI()).getPath()); // get package path and file name
                serviceClass.setPath(serviceClassFile.getAbsolutePath());
                serviceClasses.add(serviceClass);
            }

            return ResponseEntity.ok().body(serviceClasses);
        }
        throw new BadRequestAlertException("Anroid Sources not found", ENTITY_NAME, "idnull");
    }

    /**
     * GET /android-options/source-file : get android system services inside "path".
     *
     * @param path android source file path
     * @return the ResponseEntity with status 200 (OK) and with body the source
     *         file, or with status 404 (Not Found)
     */
    @GetMapping("/android-options/source-file")
    public ResponseEntity<String> getAndroidSourceFile(@RequestParam("path") String path) {
        log.debug("REST request to get android source file");
        File file = new File(path);
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
        throw new BadRequestAlertException("Source File not found", ENTITY_NAME, "idnull");
    }

    /** 
    * GET /android-options/seed-statements : get default seed statements.
    *
    * @return the ResponseEntity with status 200 (OK) and with body the seed statement list, or with status 404 (Not Found)
    */
   @GetMapping("/android-options/seed-statements")
   public ResponseEntity<List<String>> getAndroidSeedStatements() {
       log.debug("REST request to get seed statements");
       SlicerSetting seedStatements = slicerSettingRepository.findOneByKey(Constants.SEED_STATEMENTS_KEY).get();
       if (seedStatements != null) {
           return ResponseEntity.ok().body(Arrays.asList(seedStatements.getValue().split(";")));
       }
       throw new BadRequestAlertException("Seed Statements not found", ENTITY_NAME, "idnull");
   }
}
