package org.unibremen.mcyl.androidslicer.service;

import org.unibremen.mcyl.androidslicer.domain.Slice;
import org.unibremen.mcyl.androidslicer.repository.SliceRepository;

public class SliceLogger {

    private final SliceRepository sliceRepository;
    private final Slice slice;

    public SliceLogger(SliceRepository sliceRepository, Slice slice) {
        this.sliceRepository = sliceRepository;
        this.slice = slice;
    }

    public void log(String message){
        String log = slice.getLog();
        if(log == null){
            slice.setLog(message + "\n");
        }
        else{
            slice.setLog(log.concat(message + "\n"));
        }
        sliceRepository.save(slice);
    }
}