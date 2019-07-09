package org.unibremen.mcyl.androidslicer.service;

import java.util.concurrent.Executors;

import org.unibremen.mcyl.androidslicer.domain.Slice;
import org.unibremen.mcyl.androidslicer.repository.SliceRepository;

public class SliceLogger {

    private final SliceRepository sliceRepository;
    private final Slice slice;
    private final StringBuffer buffer;
    private Boolean waitingToWrite = false;
    private static final int DB_WRITE_TIMEOUT = 2500;

    public SliceLogger(SliceRepository sliceRepository, Slice slice) {
        this.sliceRepository = sliceRepository;
        this.slice = slice;
        this.buffer = new StringBuffer("");
    }

    /**
     * Log the slicing messages. Use async methods to take load away from the
     * slicing thread. Write in batches to not continually utilize the db
     * connection.
     *
     * @param slice the entity to save
     * @return the persisted entity
     */
    public void log(String message) {
        this.buffer.append(message + "\n");

        synchronized(waitingToWrite){

            if (!waitingToWrite){
                Executors.newSingleThreadExecutor().submit(() -> {
                    waitingToWrite = true;
                    this.slice.setLog(this.buffer.toString());
                    this.sliceRepository.save(slice);

                    try {
                        Thread.sleep(DB_WRITE_TIMEOUT);
                    } catch (InterruptedException e) {
                        //do nothing
                    }
                    finally {
                        waitingToWrite = false;
                    }
                });
            }
        }
    }

    public void finishLogs() {
        this.slice.setLog(this.buffer.toString());
        this.sliceRepository.save(slice);
    }
}