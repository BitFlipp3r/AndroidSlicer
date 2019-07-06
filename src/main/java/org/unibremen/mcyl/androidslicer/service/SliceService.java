package org.unibremen.mcyl.androidslicer.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.WalaException;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.unibremen.mcyl.androidslicer.config.Constants;
import org.unibremen.mcyl.androidslicer.domain.Slice;
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
     * @return the persisted entity
     */
    @Async
    public CompletableFuture<Slice> process(Slice slice) {

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
            // replace "~" with working dir
            if(androidBinaryPath.startsWith("~")){
                androidBinaryPath = androidBinaryPath.replace("~", System.getProperty("user.dir"));
            }
        }

        File appJar = 
            new File(androidBinaryPath + File.separator + "android-" + slice.getAndroidVersion() + File.separator + "android.jar");

        if (!appJar.exists()) {
            logger.log("Android Binary Jar not found");
        }

        Map<String, Set<Integer>> sliceLineNumbers = null;
        try {
            sliceLineNumbers = WalaSlicer.doSlicing(appJar, exclusionFile,
                    // add "L" to class name and remove .java extension
                    // e.g. com/android/server/AlarmManagerService.java
                    // -> Lcom/android/server/AlarmManagerService
                    "L" + FilenameUtils.removeExtension(slice.getAndroidClassName()),
                    slice.getEntryMethods(), slice.getSeedStatements(),
                    slice.getReflectionOptions(), slice.getDataDependenceOptions(),
                    slice.getControlDependenceOptions(), logger);
        } catch (IllegalArgumentException | WalaException | IOException | CancelException e) {
            logger.log(e.getMessage());
        }

        if (sliceLineNumbers != null) {

            SliceMapper sliceMapper = new SliceMapper();
            StringBuilder builder = new StringBuilder();

            logger.log("\n== RECONSTRUCTING CODE ==");

            for (Map.Entry<String, Set<Integer>> sliceLineNumbersEntry : sliceLineNumbers.entrySet()) {

                String sourceLocation = slicerSettingRepository
                    .findOneByKey(Constants.ANDROID_SOURCE_PATH_KEY).get()
                    .getValue().replace("~", System.getProperty("user.dir"))
                    + File.separator
                    + "android-"
                    + slice.getAndroidVersion()
                    + File.separator
                    + sliceLineNumbersEntry.getKey().replace("/", File.separator);

                // use TreeSet to sort line numbers
                logger.log("Slice line numbers for file " + sliceLineNumbersEntry.getKey() + ": " + new TreeSet<>(sliceLineNumbersEntry.getValue()));

                try {
                    Set<Integer> sourceCodeLines = Parser.getModifiedSlice(sourceLocation, sliceLineNumbersEntry.getValue(), logger);
                    logger.log("Lines of source code: " + new TreeSet<>(sourceCodeLines));
                    if (sourceCodeLines != null) {
                        builder.append(sliceMapper.getLinesOfCode(sourceLocation, sourceCodeLines, logger));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    logger.log("Could not reconstruct code with parser for file '" + sliceLineNumbersEntry.getKey() + "': " + ex);
                }
            }
            slice.setSlice(builder.toString());
        }
        slice.setRunning(false);
        slice.setThreadId(null);

        Slice result = sliceRepository.save(slice);
        return CompletableFuture.completedFuture(result);
    }
}
