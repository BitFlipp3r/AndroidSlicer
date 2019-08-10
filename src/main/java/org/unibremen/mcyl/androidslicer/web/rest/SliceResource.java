package org.unibremen.mcyl.androidslicer.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.unibremen.mcyl.androidslicer.domain.Slice;
import org.unibremen.mcyl.androidslicer.repository.SliceRepository;
import org.unibremen.mcyl.androidslicer.service.SliceService;
import org.unibremen.mcyl.androidslicer.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link org.unibremen.mcyl.androidslicer.domain.Slice}.
 */
@RestController
@RequestMapping("/api")
public class SliceResource {

    private final Logger log = LoggerFactory.getLogger(SliceResource.class);

    private static final String ENTITY_NAME = "slice";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SliceService sliceService;
    private final SliceRepository sliceRepository;

    public SliceResource(SliceService sliceService, SliceRepository sliceRepository) {
        this.sliceService = sliceService;
        this.sliceRepository = sliceRepository;
    }

    /**
     * {@code POST  /slice} : Create a new slice.
     *
     * @param slice the slice to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new slice, or with status {@code 400 (Bad Request)} if the slice has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/slices")
    public ResponseEntity<Slice> createSlice(@Valid @RequestBody Slice slice) throws URISyntaxException {
        log.debug("REST request to save Slice : {}", slice);
        if (slice.getId() != null) {
            throw new BadRequestAlertException("A new slice cannot already have an ID", ENTITY_NAME, "idexists");
        }

        slice.setRunning(true);
        Slice result = sliceRepository.save(slice);

        sliceService.process(slice); //async

        return ResponseEntity.created(new URI("/api/slices/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /slice} : get all the slice.
     *

     * @param pageable the pagination information.

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of slice in body.
     */
    @GetMapping("/slices")
    public ResponseEntity<List<Slice>> getAllSlice(Pageable pageable) {
        log.debug("REST request to get a page of Slice");
        Page<Slice> page = sliceRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /slices/:id} : get the "id" slice.
     *
     * @param id the id of the slice to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the slice, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/slices/{id}")
    public ResponseEntity<Slice> getSlice(@PathVariable String id) {
        log.debug("REST request to get Slice : {}", id);
        Optional<Slice> slice = sliceRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(slice);
    }

    /**
     * {@code DELETE  /slices/:id} : delete the "id" slice.
     *
     * @param id the id of the slice to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/slices/{id}")
    public ResponseEntity<Void> deleteSlice(@PathVariable String id) {
        log.debug("REST request to delete Slice : {}", id);
        if (id != null && !id.isEmpty()){
            Slice slice = sliceRepository.findById(id).get();
            Thread slicerThread = Thread.getAllStackTraces().keySet().stream().filter(thread -> thread.getName().equals(slice.getThreadId())).findFirst().orElse(null);
            if(slicerThread != null){
                slicerThread.interrupt();
            }
            sliceRepository.deleteById(id);
        }
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build();
    }
}
