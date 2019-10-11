package org.unibremen.mcyl.androidslicer.service;

import org.unibremen.mcyl.androidslicer.domain.Slice;
import org.unibremen.mcyl.androidslicer.repository.SliceRepository;

public class SliceLogger {

    private final SliceRepository sliceRepository;
    private final Slice slice;
    private final StringBuilder sliceLog;
    private final StringBuffer logBuffer;
    
    private static final int LOG_BUFFER_SIZE = 50000;

    public SliceLogger(SliceRepository sliceRepository, Slice slice) {
        this.sliceRepository = sliceRepository;
        this.slice = slice;
        this.sliceLog = new StringBuilder("");
        this.logBuffer = new StringBuffer("");
    }

    /**
     * Log the slicing messages. 
     *
     * @param message to log
     */
    public void log(String message) {

        // flush any buffered messages
        if (logBuffer.length() > 0){
            this.sliceLog.append(logBuffer.toString());
        }

        // add message
        this.sliceLog.append(message + "\n");
        // write to db
        this.slice.setLog(this.sliceLog.toString());
        this.sliceRepository.save(slice);
    }

    /**
     * Log the slicing messages. Write in batches to minimize 
     * db connection.
     *
     * @param message to log
     */
    public void logWithBuffer(String message) {

        // add message to buffer
        this.logBuffer.append(message + "\n");

        // check if buffer size is exceeded
        if(logBuffer.length() > LOG_BUFFER_SIZE){
            // flush the buffer
            this.sliceLog.append(logBuffer.toString());
            // write to db
            this.slice.setLog(this.sliceLog.toString());
            this.sliceRepository.save(slice);
            // clear the buffer
            logBuffer.setLength(0);
        }
    }
}