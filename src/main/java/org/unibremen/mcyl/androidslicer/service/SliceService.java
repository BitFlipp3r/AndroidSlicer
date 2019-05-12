package org.unibremen.mcyl.androidslicer.service;

import org.unibremen.mcyl.androidslicer.domain.Slice;
import org.unibremen.mcyl.androidslicer.repository.SliceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Slice}.
 */
@Service
public class SliceService {

    private final Logger log = LoggerFactory.getLogger(SliceService.class);

    private final SliceRepository sliceRepository;

    public SliceService(SliceRepository sliceRepository) {
        this.sliceRepository = sliceRepository;
    }

    /**
     * Save a slice.
     *
     * @param slice the entity to save.
     * @return the persisted entity.
     */
    public Slice save(Slice slice) {
        log.debug("Request to save Slice : {}", slice);
        return sliceRepository.save(slice);
    }

    /**
     * Get all the slice.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<Slice> findAll(Pageable pageable) {
        log.debug("Request to get all Slice");
        return sliceRepository.findAll(pageable);
    }


    /**
     * Get one slice by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<Slice> findOne(String id) {
        log.debug("Request to get Slice : {}", id);
        return sliceRepository.findById(id);
    }

    /**
     * Delete the slice by id.
     *
     * @param id the id of the entity.
     */
    public void delete(String id) {
        log.debug("Request to delete Slice : {}", id);
        sliceRepository.deleteById(id);
    }
}
