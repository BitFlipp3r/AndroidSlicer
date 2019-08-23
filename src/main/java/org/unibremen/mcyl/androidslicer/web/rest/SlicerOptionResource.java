package org.unibremen.mcyl.androidslicer.web.rest;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.unibremen.mcyl.androidslicer.domain.SlicerOption;
import org.unibremen.mcyl.androidslicer.domain.enumeration.SlicerOptionType;
import org.unibremen.mcyl.androidslicer.repository.SlicerOptionRepository;
import org.unibremen.mcyl.androidslicer.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;

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
    @PutMapping("/slicer-options")
    public ResponseEntity<SlicerOption> updateSlicerOption(@Valid @RequestBody SlicerOption slicerOption)
            throws URISyntaxException {
        log.debug("REST request to update SlicerOption : {}", slicerOption);
        
        // security first
        if (slicerOption.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        SlicerOption slicerOptionToUpdate = slicerOptionRepository.findById(slicerOption.getId()).orElse(null);
        if (slicerOptionToUpdate == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        // remove other default settings if this is a new default
        if (slicerOption.getIsDefault()) {
            removeDefaults(slicerOption.getType());
        }

        slicerOptionToUpdate.setDescription(slicerOption.getDescription());
        slicerOptionToUpdate.setIsDefault(slicerOption.getIsDefault());

        SlicerOption result = slicerOptionRepository.save(slicerOptionToUpdate);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME,
                slicerOption.getId().toString())).body(result);
    }

    /**
     * {@code GET  /slicer-option} : get all the slicerOptions.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of slicerOptions in body.
     */
    @GetMapping("/slicer-options")
    public ResponseEntity<List<SlicerOption>> getAllSlicerOptions(Pageable pageable) {
        log.debug("REST request to get a page of SlicerOptions");
        Page<SlicerOption> page = slicerOptionRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /all-slicer-option} : get all the slicerOptions without paging.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of slicerOptions in body.
     */
    @GetMapping("/all-slicer-options")
    public ResponseEntity<List<SlicerOption>> getAllSlicerOptions() {
        log.debug("REST request to get a all SlicerOptions");
        List<SlicerOption> slicerOptions = slicerOptionRepository.findAll();
        return ResponseEntity.ok().body(slicerOptions);
    }

    /**
     * {@code GET  /slicer-options/:id} : get the "id" slicerOption.
     *
     * @param id the id of the slicerOption to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the slicerOption, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/slicer-options/{id}")
    public ResponseEntity<SlicerOption> getSlicerOption(@PathVariable String id) {
        log.debug("REST request to get SlicerOption : {}", id);
        Optional<SlicerOption> slicerOption = slicerOptionRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(slicerOption);
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
