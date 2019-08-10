package org.unibremen.mcyl.androidslicer.web.rest;

import org.unibremen.mcyl.androidslicer.domain.Slice;
import org.unibremen.mcyl.androidslicer.service.SliceService;
import org.unibremen.mcyl.androidslicer.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

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

    public SliceResource(SliceService sliceService) {
        this.sliceService = sliceService;
    }

    /**
     * {@code POST  /slice} : Create a new slice.
     *
     * @param slice the slice to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new slice, or with status {@code 400 (Bad Request)} if the slice has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/slice")
    public ResponseEntity<Slice> createSlice(@Valid @RequestBody Slice slice) throws URISyntaxException {
        log.debug("REST request to save Slice : {}", slice);
        if (slice.getId() != null) {
            throw new BadRequestAlertException("A new slice cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Slice result = sliceService.save(slice);
        return ResponseEntity.created(new URI("/api/slice/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /slice} : Updates an existing slice.
     *
     * @param slice the slice to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated slice,
     * or with status {@code 400 (Bad Request)} if the slice is not valid,
     * or with status {@code 500 (Internal Server Error)} if the slice couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/slice")
    public ResponseEntity<Slice> updateSlice(@Valid @RequestBody Slice slice) throws URISyntaxException {
        log.debug("REST request to update Slice : {}", slice);
        if (slice.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Slice result = sliceService.save(slice);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, slice.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /slice} : get all the slice.
     *

     * @param pageable the pagination information.

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of slice in body.
     */
    @GetMapping("/slice")
    public ResponseEntity<List<Slice>> getAllSlice(Pageable pageable) {
        log.debug("REST request to get a page of Slice");
        Page<Slice> page = sliceService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /slice/:id} : get the "id" slice.
     *
     * @param id the id of the slice to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the slice, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/slice/{id}")
    public ResponseEntity<Slice> getSlice(@PathVariable String id) {
        log.debug("REST request to get Slice : {}", id);
        Optional<Slice> slice = sliceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(slice);
    }

    /**
     * {@code DELETE  /slice/:id} : delete the "id" slice.
     *
     * @param id the id of the slice to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/slice/{id}")
    public ResponseEntity<Void> deleteSlice(@PathVariable String id) {
        log.debug("REST request to delete Slice : {}", id);
        sliceService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build();
    }
}
