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
import org.unibremen.mcyl.androidslicer.domain.SlicerSetting;
import org.unibremen.mcyl.androidslicer.repository.SlicerSettingRepository;
import org.unibremen.mcyl.androidslicer.web.rest.errors.BadRequestAlertException;

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
     * {@code PUT  /slicer-setting} : Updates an existing slicerSetting.
     *
     * @param slicerSetting the slicerSetting to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated slicerSetting,
     * or with status {@code 400 (Bad Request)} if the slicerSetting is not valid,
     * or with status {@code 500 (Internal Server Error)} if the slicerSetting couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/slicer-settings")
    public ResponseEntity<SlicerSetting> updateSlicerSetting(@Valid @RequestBody SlicerSetting slicerSetting) throws URISyntaxException {
        log.debug("REST request to update SlicerSetting : {}", slicerSetting);

        // security first
        if (slicerSetting.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        SlicerSetting slicerSettingToUpdate = slicerSettingRepository.findById(slicerSetting.getId()).orElse(null);
        if (slicerSettingToUpdate == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        slicerSettingToUpdate.setValue(slicerSetting.getValue());

        SlicerSetting result = slicerSettingRepository.save(slicerSettingToUpdate);
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
     * {@code GET  /slicer-settings/:id} : get the "id" slicerSetting.
     *
     * @param id the id of the slicerSetting to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the slicerSetting, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/slicer-settings/{id}")
    public ResponseEntity<SlicerSetting> getSlicerSetting(@PathVariable String id) {
        log.debug("REST request to get SlicerSetting : {}", id);
        Optional<SlicerSetting> slicerSetting = slicerSettingRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(slicerSetting);
    }
}
