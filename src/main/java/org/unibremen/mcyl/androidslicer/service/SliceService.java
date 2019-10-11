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
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.WalaException;

import org.apache.commons.io.FilenameUtils;
import org.bson.BsonMaximumSizeExceededException;
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
import org.unibremen.mcyl.androidslicer.web.rest.errors.BadRequestAlertException;

/**
 * Service Implementation for managing Slice.
 */
@Service
public class SliceService {

    private final SliceRepository sliceRepository;
    private final SlicerSettingRepository slicerSettingRepository;

    // get system OS type to start vs code server accordingly
    private static String OS = System.getProperty("os.name").toLowerCase();
    // keep track of the vs code server process
    private static Process vsCodeServerProcess;

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
        } catch (Exception ex) {
            logger.log(ex.getMessage());
        }

        if (sliceLineNumbers != null) {

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
                    Set<Integer> sourceCodeLineNumbers = Parser.getModifiedSlice(sourceLocation, sliceLineNumbersEntry.getValue(), androidClassName, logger);
                    if (sourceCodeLineNumbers != null) {
                        logger.log("Lines of source code: " + new TreeSet<>(sourceCodeLineNumbers));
                        /**
                         * Gets the actual source code lines based on the line numbers.
                         */
                        builder.append(SliceMapper.getLinesOfCode(sourceLocation, sourceCodeLineNumbers, logger));

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


    /**
     * Start a vs code server with the slices as default directory. See
     * https://github.com/cdr/code-server/blob/master/doc/self-hosted/index.md for
     * documentation.
     * 
     * @param slice Slice entity to find slice output directory.
     * @param hostname Hostname of this server which the client used. Needed for vs code server link.
     * @return serverLink
     * @throws IOException
     * @throws InterruptedException
     */
    public String openVsCodeServer(Slice slice, String hostname) throws IOException, InterruptedException {
        // find vscode server settings
        SlicerSetting vsCodeBinaryPathSetting = 
        slicerSettingRepository.findOneByKey(Constants.CODE_SERVER_DIR_KEY).get();
        SlicerSetting vsCodePortSetting = 
        slicerSettingRepository.findOneByKey(Constants.CODE_SERVER_PORT_KEY).get();

        if(vsCodeBinaryPathSetting != null && vsCodePortSetting != null){
            // check operating system
            String osPath = "";
            String binaryFileExtension = "";
            if (OS.indexOf("win") >= 0) {
                osPath = "win";
                binaryFileExtension = ".exe";
            } else if (OS.indexOf("mac") >= 0) {
                osPath = "mac";
            } else if (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0) {
                osPath = "lin";
            }

            if (osPath == ""){
                throw new BadRequestAlertException("Operation system is not supported.", null, null);
            }

            // find vscode server settings binary
            File vsCodeBinary = new File(vsCodeBinaryPathSetting.getValue()
             + File.separator 
             + osPath
             + File.separator 
             + "code-server" 
             + binaryFileExtension);

            if(vsCodeBinary.exists()){
                // Runtime.getRuntime().exec  waitForProcessOutput()

                /* get output directory for this slice */
                File outputDirectory = null;
                SlicerSetting outputDirSetting = 
                slicerSettingRepository.findOneByKey(Constants.OUTPUT_DIR_KEY).get();
                String androidClassName = slice.getAndroidClassName()
                    .substring(slice.getAndroidClassName().lastIndexOf("/") + 1, slice.getAndroidClassName().length());
                // remove .java
                androidClassName = androidClassName.substring(0, androidClassName.lastIndexOf("."));

                if(outputDirSetting != null && !outputDirSetting.getValue().isEmpty()){                     
                        // check dir or create it
                        outputDirectory = new File(outputDirSetting.getValue() +
                        File.separator +
                        androidClassName +
                        "-" +
                        slice.getId());

                        if (!outputDirectory.exists()){
                            throw new BadRequestAlertException("Slice output directory not found.", null, null);
                        }                           
                }
              
                if(vsCodeServerProcess == null){
                    String dataDir = vsCodeBinaryPathSetting.getValue() + File.separator + osPath + File.separator;
                    String installCommand = vsCodeBinary.getAbsolutePath() + " --install-extension=redhat.java --user-data-dir=" + dataDir;
                    String startCommand = vsCodeBinary.getAbsolutePath() + " --port=" + vsCodePortSetting.getValue() + " --no-auth --allow-http --disable-telemetry --user-data-dir=" + dataDir;

                    // install java extension for vs code (see https://marketplace.visualstudio.com/items?itemName=redhat.java)
                    System.out.println("Running: " + installCommand);
                    Runtime.getRuntime().exec(installCommand).waitFor(30, TimeUnit.SECONDS);

                    // start server
                    System.out.println("Running: " + startCommand);
                    vsCodeServerProcess = Runtime.getRuntime().exec(startCommand);
                }

                return "http://" + hostname + ":" + vsCodePortSetting.getValue().toString() +"/?folder=" + outputDirectory.getAbsolutePath();
            }
            else{
                throw new BadRequestAlertException("VS code server binary not found.", null, null);
            }

        }
        else{
            throw new BadRequestAlertException("VS code server path or port is not set.", null, null);
        }
    }
}
