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

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;


import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link org.unibremen.mcyl.androidslicer.domain.SlicerSetting}.
 */
@RestController
@RequestMapping("/api")
public class SlicerSettingResource {

    private final Logger log = LoggerFactory.getLogger(SlicerSettingResource.class);

    private static final String ENTITY_NAME = "slicerSetting";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SlicerSettingRepository slicerSettingRepository;

    public SlicerSettingResource(SlicerSettingRepository slicerSettingRepository) {
        this.slicerSettingRepository = slicerSettingRepository;
    }

    /**
     * {@code POST  /slicer-setting} : Create a new slicerSetting.
     *
     * @param slicerSetting the slicerSetting to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new slicerSetting, or with status {@code 400 (Bad Request)} if the slicerSetting has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/slicer-setting")
    public ResponseEntity<SlicerSetting> createSlicerSetting(@Valid @RequestBody SlicerSetting slicerSetting) throws URISyntaxException {
        log.debug("REST request to save SlicerSetting : {}", slicerSetting);
        if (slicerSetting.getId() != null) {
            throw new BadRequestAlertException("A new slicerSetting cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SlicerSetting result = slicerSettingRepository.save(slicerSetting);
        return ResponseEntity.created(new URI("/api/slicer-setting/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /slicer-setting} : Updates an existing slicerSetting.
     *
     * @param slicerSetting the slicerSetting to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated slicerSetting,
     * or with status {@code 400 (Bad Request)} if the slicerSetting is not valid,
     * or with status {@code 500 (Internal Server Error)} if the slicerSetting couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/slicer-setting")
    public ResponseEntity<SlicerSetting> updateSlicerSetting(@Valid @RequestBody SlicerSetting slicerSetting) throws URISyntaxException {
        log.debug("REST request to update SlicerSetting : {}", slicerSetting);
        if (slicerSetting.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        SlicerSetting result = slicerSettingRepository.save(slicerSetting);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, slicerSetting.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /slicer-setting} : get all the slicerSettings.
     *

     * @param pageable the pagination information.

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of slicerSettings in body.
     */
    @GetMapping("/slicer-settings")
    public ResponseEntity<List<SlicerSetting>> getAllSlicerSettings(Pageable pageable) {
        log.debug("REST request to get a page of SlicerSettings");
        Page<SlicerSetting> page = slicerSettingRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /slicer-setting/:id} : get the "id" slicerSetting.
     *
     * @param id the id of the slicerSetting to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the slicerSetting, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/slicer-setting/{id}")
    public ResponseEntity<SlicerSetting> getSlicerSetting(@PathVariable String id) {
        log.debug("REST request to get SlicerSetting : {}", id);
        Optional<SlicerSetting> slicerSetting = slicerSettingRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(slicerSetting);
    }

    /**
     * {@code DELETE  /slicer-setting/:id} : delete the "id" slicerSetting.
     *
     * @param id the id of the slicerSetting to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/slicer-setting/{id}")
    public ResponseEntity<Void> deleteSlicerSetting(@PathVariable String id) {
        log.debug("REST request to delete SlicerSetting : {}", id);
        slicerSettingRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build();
    }
}
