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

    public void log(String message) {
        this.buffer.append(message + "\n");
        slice.setLog(this.buffer.toString());
        sliceRepository.save(slice);
    }
}