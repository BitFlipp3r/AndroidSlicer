package org.unibremen.mcyl.androidslicer.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.WalaException;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.unibremen.mcyl.androidslicer.config.Constants;
import org.unibremen.mcyl.androidslicer.domain.Slice;
import org.unibremen.mcyl.androidslicer.domain.SlicedClass;
import org.unibremen.mcyl.androidslicer.domain.SlicerSetting;
import org.unibremen.mcyl.androidslicer.repository.SliceRepository;
import org.unibremen.mcyl.androidslicer.repository.SlicerSettingRepository;
import org.unibremen.mcyl.androidslicer.wala.WalaSlicer;
import org.unibremen.mcyl.androidslicer.wala.parser.Parser;
import org.unibremen.mcyl.androidslicer.wala.parser.SliceMapper;

/**
 * Service Implementation for managing Slice.
 */
@Service
public class SliceService {

    private final Logger log = LoggerFactory.getLogger(SliceService.class);

    private final SliceRepository sliceRepository;
    private final SlicerSettingRepository slicerSettingRepository;

    public SliceService(SliceRepository sliceRepository, SlicerSettingRepository slicerSettingRepository) {
        this.sliceRepository = sliceRepository;
        this.slicerSettingRepository = slicerSettingRepository;
    }

    /**
     * Process a slice.
     *
     * @param slice the entity to save
     * @return the persistend entity
     */
    @Async
    public CompletableFuture<Slice> process(Slice slice) {
        long start = System.currentTimeMillis();

        // increase slicing priority
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        String newThreadName = UUID.randomUUID().toString();
        Thread.currentThread().setName(newThreadName);
        slice.setThreadId(newThreadName);
        sliceRepository.save(slice);

        SliceLogger logger = new SliceLogger(sliceRepository, slice);

        File exclusionFile;
        try {
            exclusionFile = File.createTempFile("ExclusionList", ".txt");
            BufferedWriter bw = new BufferedWriter(new FileWriter(exclusionFile));
            bw.write(slicerSettingRepository.findOneByKey(Constants.EXCLUSION_LIST_KEY).get().getValue());
            bw.close();
        } catch (IOException e) {
            throw new CompletionException(e);
        }

        SlicerSetting androidBinaryPathSetting = 
        slicerSettingRepository.findOneByKey(Constants.ANDROID_PLATFORM_PATH_KEY).get();
    
        String androidBinaryPath = "";
        if(androidBinaryPathSetting != null){
            androidBinaryPath = androidBinaryPathSetting.getValue();
        }

        File appJar = 
            new File(androidBinaryPath + File.separator + "android-" + slice.getAndroidVersion() + File.separator + "android.jar");

        if (!appJar.exists()) {
            logger.log("Android Binary Jar not found");
        }

        // check if any seed statement is an invalid regex (and remove it)
        for (Iterator<String> seedStatementIterator =  slice.getSeedStatements().iterator(); seedStatementIterator.hasNext();) {
            String seedStatement = seedStatementIterator.next();
            try {
                Pattern.compile(seedStatement);
                } catch (PatternSyntaxException e) {
                logger.log("WARNING: " + seedStatement + " is not a valid regular expression and will be removed!");
                seedStatementIterator.remove();
            }
        }

        Map<String, Set<Integer>> sliceLineNumbers = null;
        try {
            sliceLineNumbers = WalaSlicer.doSlicing(
                    appJar, 
                    exclusionFile,
                    // add "L" to class name and remove .java extension
                    // e.g. com/android/server/AlarmManagerService.java
                    // -> Lcom/android/server/AlarmManagerService
                    "L" + FilenameUtils.removeExtension(slice.getAndroidClassName()),
                    slice.getEntryMethods(), 
                    slice.getSeedStatements(),
                    slice.getCfaType(),
                    slice.getcfaLevel(),
                    slice.getReflectionOptions(), 
                    slice.getDataDependenceOptions(),
                    slice.getControlDependenceOptions(), 
                    logger);
        } catch (IllegalArgumentException | WalaException | IOException | CancelException e) {
            logger.log(e.getMessage());
        }

        if (sliceLineNumbers != null) {

            SliceMapper sliceMapper = new SliceMapper();
            logger.log("\n== RECONSTRUCTING CODE ==");

            // save to file if the setting is enabled
            SlicerSetting saveToFileSetting = 
            slicerSettingRepository.findOneByKey(Constants.SAVE_TO_FILE_KEY).get();
            SlicerSetting outputDirSetting = 
            slicerSettingRepository.findOneByKey(Constants.OUTPUT_DIR_KEY).get();
            boolean saveToFile = false;
            File outputDirectory = null;

            /* e.g. "com/android/server/AlarmManagerService.java" -> "AlarmManagerService" */
            String androidClassName = slice.getAndroidClassName()
                .substring(slice.getAndroidClassName().lastIndexOf("/") + 1, slice.getAndroidClassName().length());
            // remove .java
            androidClassName = androidClassName.substring(0, androidClassName.lastIndexOf("."));

            if(saveToFileSetting != null && 
                Boolean.parseBoolean(saveToFileSetting.getValue()) &&
                outputDirSetting != null && 
                !outputDirSetting.getValue().isEmpty()){
                    
                    // check dir or create it
                    outputDirectory = new File(outputDirSetting.getValue() +
                    File.separator +
                    androidClassName +
                    "-" +
                    slice.getId());

                    if (!outputDirectory.exists()){
                        outputDirectory.mkdirs();
                    }
                    saveToFile = true;                                 
            }

            for (Map.Entry<String, Set<Integer>> sliceLineNumbersEntry : sliceLineNumbers.entrySet()) {

                StringBuilder builder = new StringBuilder();

                String packageAndJavaClass = sliceLineNumbersEntry.getKey(); //e.g. com/android/server/AlarmManagerService
                String sourceLocation = slicerSettingRepository
                    .findOneByKey(Constants.ANDROID_SOURCE_PATH_KEY).get().getValue()
                    + File.separator
                    + "android-"
                    + slice.getAndroidVersion()
                    + File.separator
                    + packageAndJavaClass.replace("/", File.separator);

                // use TreeSet to sort line numbers
                logger.log("Slice line numbers for file " + packageAndJavaClass + ": " + new TreeSet<>(sliceLineNumbersEntry.getValue()));

                try {
                    Set<Integer> sourceCodeLines = Parser.getModifiedSlice(sourceLocation, sliceLineNumbersEntry.getValue(), androidClassName, logger);
                    if (sourceCodeLines != null) {
                        logger.log("Lines of source code: " + new TreeSet<>(sourceCodeLines));
                        /**
                         * Gets the actual source code lines based on the line numbers.
                         */
                        builder.append(sliceMapper.getLinesOfCode(sourceLocation, sourceCodeLines, logger));

                        // add the slice code to the slice entity
                        String javaClassFileName = packageAndJavaClass.substring(packageAndJavaClass.lastIndexOf("/") + 1, packageAndJavaClass.length());
                        String packagePath = packageAndJavaClass.substring(0, packageAndJavaClass.lastIndexOf("/"));
                        slice.getSlicedClasses().add(new SlicedClass(javaClassFileName, packagePath, builder.toString()));

                        if(saveToFile){
                            File javaFileForSlicedClass = new File(outputDirectory + File.separator + javaClassFileName);
                            try{
                                FileWriter fw = new FileWriter(javaFileForSlicedClass.getAbsoluteFile());
                                BufferedWriter bw = new BufferedWriter(fw);
                                bw.write(builder.toString());
                                bw.close();
                            }
                            catch (IOException ex){
                                ex.printStackTrace();
                                logger.log("Could not save slice code for file '" + packageAndJavaClass + "': " + ex);
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    logger.log("Could not reconstruct code with parser for file '" + sliceLineNumbersEntry.getKey() + "': " + ex);
                }
            }
        }

        slice.setRunning(false);
        slice.setThreadId(null);

        Slice result = sliceRepository.save(slice);

        long end = System.currentTimeMillis();
        logger.log("Slicing took " + (end - start) + "ms.");

        return CompletableFuture.completedFuture(result);
    }
}
