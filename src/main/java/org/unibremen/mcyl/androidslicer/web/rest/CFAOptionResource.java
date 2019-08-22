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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.unibremen.mcyl.androidslicer.domain.CFAOption;
import org.unibremen.mcyl.androidslicer.repository.CFAOptionRepository;
import org.unibremen.mcyl.androidslicer.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link org.unibremen.mcyl.androidslicer.domain.CFAOption}.
 */
@RestController
@RequestMapping("/api")
public class CFAOptionResource {

    private final Logger log = LoggerFactory.getLogger(CFAOptionResource.class);

    private static final String ENTITY_NAME = "CFA-Option";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CFAOptionRepository cFAOptionRepository;

    public CFAOptionResource(CFAOptionRepository cFAOptionRepository) {
        this.cFAOptionRepository = cFAOptionRepository;
    }

    /**
     * {@code POST  /cfa-options} : Create a new cFAOption.
     *
     * @param cFAOption the cFAOption to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new cFAOption, or with status {@code 400 (Bad Request)} if the cFAOption has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/cfa-options")
    public ResponseEntity<CFAOption> createCFAOption(@Valid @RequestBody CFAOption cFAOption) throws URISyntaxException {
        log.debug("REST request to save CFAOption : {}", cFAOption);
        if (cFAOption.getId() != null) {
            throw new BadRequestAlertException("A new cFAOption cannot already have an ID", ENTITY_NAME, "idexists");
        }

        if(cFAOption.getIsDefault()){
            removeDefaults();
        }

        CFAOption result = cFAOptionRepository.save(cFAOption);
        return ResponseEntity.created(new URI("/api/cfa-options/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /cfa-options} : Updates an existing cFAOption.
     *
     * @param cFAOption the cFAOption to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cFAOption,
     * or with status {@code 400 (Bad Request)} if the cFAOption is not valid,
     * or with status {@code 500 (Internal Server Error)} if the cFAOption couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/cfa-options")
    public ResponseEntity<CFAOption> updateCFAOption(@Valid @RequestBody CFAOption cFAOption) throws URISyntaxException {
        log.debug("REST request to update CFAOption : {}", cFAOption);
        if (cFAOption.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        if(cFAOption.getIsDefault()){
            removeDefaults();
        }

        CFAOption result = cFAOptionRepository.save(cFAOption);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, cFAOption.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /cfa-options} : get all the cFAOptions.
     *

     * @param pageable the pagination information.

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of cFAOptions in body.
     */
    @GetMapping("/cfa-options")
    public ResponseEntity<List<CFAOption>> getAllCFAOptions(Pageable pageable) {
        log.debug("REST request to get a page of CFAOptions");
        Page<CFAOption> page = cFAOptionRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /all-cfa-option} : get all the cfaOptions without paging.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of cfaOptions in body.
     */
    @GetMapping("/all-cfa-options")
    public ResponseEntity<List<CFAOption>> getAllCFAOptions() {
        log.debug("REST request to get a all CFAOptions");
        List<CFAOption> cfaOptions = cFAOptionRepository.findAll();
        return ResponseEntity.ok().body(cfaOptions);
    }

    /**
     * {@code GET  /cfa-options/:id} : get the "id" cFAOption.
     *
     * @param id the id of the cFAOption to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cFAOption, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/cfa-options/{id}")
    public ResponseEntity<CFAOption> getCFAOption(@PathVariable String id) {
        log.debug("REST request to get CFAOption : {}", id);
        Optional<CFAOption> cFAOption = cFAOptionRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(cFAOption);
    }

    /**
     * {@code DELETE  /cfa-options/:id} : delete the "id" cFAOption.
     *
     * @param id the id of the cFAOption to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/cfa-options/{id}")
    public ResponseEntity<Void> deleteCFAOption(@PathVariable String id) {
        log.debug("REST request to delete CFAOption : {}", id);
        cFAOptionRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build();
    }

    private void removeDefaults() {
        cFAOptionRepository.findAll().forEach((CFAOption cFAOption) -> {
            if (cFAOption.getIsDefault()) {
                cFAOption.setIsDefault(false);
                cFAOptionRepository.save(cFAOption);
            }
        });
    }
}
