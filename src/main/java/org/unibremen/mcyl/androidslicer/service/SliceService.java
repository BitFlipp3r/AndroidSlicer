package org.unibremen.mcyl.androidslicer.service;

import org.unibremen.mcyl.androidslicer.config.Constants;
import org.unibremen.mcyl.androidslicer.domain.Slice;
import org.unibremen.mcyl.androidslicer.repository.SliceRepository;
import org.unibremen.mcyl.androidslicer.repository.SlicerSettingRepository;
import org.unibremen.mcyl.androidslicer.wala.WalaSlicer;
import org.unibremen.mcyl.androidslicer.wala.parser.Parser;
import org.unibremen.mcyl.androidslicer.wala.parser.SliceMapper;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.WalaException;

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

        File appJar = new File(
                slicerSettingRepository.findOneByKey(Constants.ANDROID_PLATFORM_PATH_KEY).get().getValue()
                        + "\\android-" + slice.getAndroidVersion() + "\\android.jar");

        if (!appJar.exists()) {
            logger.log("Android Platform Jar not found");
        }

        Map<String, Set<Integer>> sliceLineNumbers = null;
        try {
            sliceLineNumbers = WalaSlicer.doSlicing(appJar, exclusionFile,
                    // add "L" to class name and remove .java extension
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
                    .getValue()
                    + "\\android-"
                    + slice.getAndroidVersion()
                    + "\\"
                    + sliceLineNumbersEntry.getKey().replace("/", File.separator);

                Set<Integer> sliceLineNumbersSorted = new TreeSet<>(sliceLineNumbersEntry.getValue());

                logger.log("Slice line numbers (sorted) for file " + sliceLineNumbersEntry.getKey() + ": " + sliceLineNumbersSorted);

                try {
                    Set<Integer> modSlice = Parser.getModifiedSlice(sourceLocation, sliceLineNumbersEntry.getValue(), logger);
                    logger.log("Lines of code: " + modSlice.toString());
                    if (modSlice != null) {
                        builder.append(sliceMapper.getLinesOfCode(sourceLocation, modSlice, logger));
                    }
                } catch (Exception ex) {
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
