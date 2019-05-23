package org.unibremen.mcyl.androidslicer.web.rest;

import org.unibremen.mcyl.androidslicer.domain.SlicerOption;
import org.unibremen.mcyl.androidslicer.domain.enumeration.SlicerOptionType;
import org.unibremen.mcyl.androidslicer.repository.SlicerOptionRepository;
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
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing
 * {@link org.unibremen.mcyl.androidslicer.domain.SlicerOption}.
 */
@RestController
@RequestMapping("/api")
public class SlicerOptionResource {

    private final Logger log = LoggerFactory.getLogger(SlicerOptionResource.class);

    private static final String ENTITY_NAME = "slicerOption";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SlicerOptionRepository slicerOptionRepository;

    public SlicerOptionResource(SlicerOptionRepository slicerOptionRepository) {
        this.slicerOptionRepository = slicerOptionRepository;
    }

    /**
     * {@code POST  /slicer-option} : Create a new slicerOption.
     *
     * @param slicerOption the slicerOption to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
     *         body the new slicerOption, or with status {@code 400 (Bad Request)}
     *         if the slicerOption has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/slicer-option")
    public ResponseEntity<SlicerOption> createSlicerOption(@Valid @RequestBody SlicerOption slicerOption)
            throws URISyntaxException {
        log.debug("REST request to save SlicerOption : {}", slicerOption);
        if (slicerOption.getId() != null) {
            throw new BadRequestAlertException("A new slicerOption cannot already have an ID", ENTITY_NAME, "idexists");
        }

        // remove other default settings if this is a new default
        if (slicerOption.getIsDefault()) {
            removeDefaults(slicerOption.getType());
        }

        SlicerOption result = slicerOptionRepository.save(slicerOption);
        return ResponseEntity
                .created(new URI("/api/slicer-option/" + result.getId())).headers(HeaderUtil
                        .createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                .body(result);
    }

    /**
     * {@code PUT  /slicer-option} : Updates an existing slicerOption.
     *
     * @param slicerOption the slicerOption to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated slicerOption, or with status {@code 400 (Bad Request)} if
     *         the slicerOption is not valid, or with status
     *         {@code 500 (Internal Server Error)} if the slicerOption couldn't be
     *         updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/slicer-option")
    public ResponseEntity<SlicerOption> updateSlicerOption(@Valid @RequestBody SlicerOption slicerOption)
            throws URISyntaxException {
        log.debug("REST request to update SlicerOption : {}", slicerOption);
        if (slicerOption.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        // remove other default settings if this is a new default
        if (slicerOption.getIsDefault()) {
            removeDefaults(slicerOption.getType());
        }

        SlicerOption result = slicerOptionRepository.save(slicerOption);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME,
                slicerOption.getId().toString())).body(result);
    }

    /**
     * {@code GET  /slicer-option} : get all the slicerOptions.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of slicerOptions in body.
     */
    @GetMapping("/slicer-option")
    public ResponseEntity<List<SlicerOption>> getAllSlicerOptions(Pageable pageable,
            @RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder) {
        log.debug("REST request to get a page of SlicerOptions");
        Page<SlicerOption> page = slicerOptionRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /slicer-option/:id} : get the "id" slicerOption.
     *
     * @param id the id of the slicerOption to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the slicerOption, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/slicer-option/{id}")
    public ResponseEntity<SlicerOption> getSlicerOption(@PathVariable String id) {
        log.debug("REST request to get SlicerOption : {}", id);
        Optional<SlicerOption> slicerOption = slicerOptionRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(slicerOption);
    }

    /**
     * {@code DELETE  /slicer-option/:id} : delete the "id" slicerOption.
     *
     * @param id the id of the slicerOption to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/slicer-option/{id}")
    public ResponseEntity<Void> deleteSlicerOption(@PathVariable String id) {
        log.debug("REST request to delete SlicerOption : {}", id);
        slicerOptionRepository.deleteById(id);
        return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build();
    }

    private void removeDefaults(SlicerOptionType slicerOptionType) {
        slicerOptionRepository.findByType(slicerOptionType).forEach((SlicerOption slicerOption) -> {
            if (slicerOption.getIsDefault()) {
                slicerOption.setIsDefault(false);
                slicerOptionRepository.save(slicerOption);
            }
        });
    }
}
