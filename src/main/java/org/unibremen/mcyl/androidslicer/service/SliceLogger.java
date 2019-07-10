package org.unibremen.mcyl.androidslicer.service;

import org.unibremen.mcyl.androidslicer.domain.Slice;
import org.unibremen.mcyl.androidslicer.repository.SliceRepository;

public class SliceLogger {

    private final SliceRepository sliceRepository;
    private final Slice slice;
    private final StringBuffer buffer;

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
        this.slice.setLog(this.buffer.toString());
        this.sliceRepository.save(slice);
    }
}