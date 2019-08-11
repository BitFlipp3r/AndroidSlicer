package org.unibremen.mcyl.androidslicer.web.rest;

import org.unibremen.mcyl.androidslicer.AndroidSlicerApp;
import org.unibremen.mcyl.androidslicer.domain.CFAOption;
import org.unibremen.mcyl.androidslicer.repository.CFAOptionRepository;
import org.unibremen.mcyl.androidslicer.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Base64Utils;
import org.springframework.validation.Validator;


import java.util.List;

import static org.unibremen.mcyl.androidslicer.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.unibremen.mcyl.androidslicer.domain.enumeration.CFAType;
/**
 * Integration tests for the {@link CFAOptionResource} REST controller.
 */
@SpringBootTest(classes = AndroidSlicerApp.class)
public class CFAOptionResourceIT {

    private static final CFAType DEFAULT_TYPE = CFAType.ZERO_CFA;
    private static final CFAType UPDATED_TYPE = CFAType.ZERO_ONE_CFA;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_DEFAULT = false;
    private static final Boolean UPDATED_IS_DEFAULT = true;

    @Autowired
    private CFAOptionRepository cFAOptionRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private Validator validator;

    private MockMvc restCFAOptionMockMvc;

    private CFAOption cFAOption;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final CFAOptionResource cFAOptionResource = new CFAOptionResource(cFAOptionRepository);
        this.restCFAOptionMockMvc = MockMvcBuilders.standaloneSetup(cFAOptionResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CFAOption createEntity() {
        CFAOption cFAOption = new CFAOption()
            .type(DEFAULT_TYPE)
            .description(DEFAULT_DESCRIPTION)
            .isDefault(DEFAULT_IS_DEFAULT);
        return cFAOption;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CFAOption createUpdatedEntity() {
        CFAOption cFAOption = new CFAOption()
            .type(UPDATED_TYPE)
            .description(UPDATED_DESCRIPTION)
            .isDefault(UPDATED_IS_DEFAULT);
        return cFAOption;
    }

    @BeforeEach
    public void initTest() {
        cFAOptionRepository.deleteAll();
        cFAOption = createEntity();
    }

    @Test
    public void createCFAOption() throws Exception {
        int databaseSizeBeforeCreate = cFAOptionRepository.findAll().size();

        // Create the CFAOption
        restCFAOptionMockMvc.perform(post("/api/cfa-options")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(cFAOption)))
            .andExpect(status().isCreated());

        // Validate the CFAOption in the database
        List<CFAOption> cFAOptionList = cFAOptionRepository.findAll();
        assertThat(cFAOptionList).hasSize(databaseSizeBeforeCreate + 1);
        CFAOption testCFAOption = cFAOptionList.get(cFAOptionList.size() - 1);
        assertThat(testCFAOption.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testCFAOption.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testCFAOption.getIsDefault()).isEqualTo(DEFAULT_IS_DEFAULT);
    }

    @Test
    public void createCFAOptionWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = cFAOptionRepository.findAll().size();

        // Create the CFAOption with an existing ID
        cFAOption.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restCFAOptionMockMvc.perform(post("/api/cfa-options")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(cFAOption)))
            .andExpect(status().isBadRequest());

        // Validate the CFAOption in the database
        List<CFAOption> cFAOptionList = cFAOptionRepository.findAll();
        assertThat(cFAOptionList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    public void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = cFAOptionRepository.findAll().size();
        // set the field null
        cFAOption.setType(null);

        // Create the CFAOption, which fails.

        restCFAOptionMockMvc.perform(post("/api/cfa-options")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(cFAOption)))
            .andExpect(status().isBadRequest());

        List<CFAOption> cFAOptionList = cFAOptionRepository.findAll();
        assertThat(cFAOptionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void getAllCFAOptions() throws Exception {
        // Initialize the database
        cFAOptionRepository.save(cFAOption);

        // Get all the cFAOptionList
        restCFAOptionMockMvc.perform(get("/api/cfa-options?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cFAOption.getId())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].isDefault").value(hasItem(DEFAULT_IS_DEFAULT.booleanValue())));
    }
    
    @Test
    public void getCFAOption() throws Exception {
        // Initialize the database
        cFAOptionRepository.save(cFAOption);

        // Get the cFAOption
        restCFAOptionMockMvc.perform(get("/api/cfa-options/{id}", cFAOption.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(cFAOption.getId()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.isDefault").value(DEFAULT_IS_DEFAULT.booleanValue()));
    }

    @Test
    public void getNonExistingCFAOption() throws Exception {
        // Get the cFAOption
        restCFAOptionMockMvc.perform(get("/api/cfa-options/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateCFAOption() throws Exception {
        // Initialize the database
        cFAOptionRepository.save(cFAOption);

        int databaseSizeBeforeUpdate = cFAOptionRepository.findAll().size();

        // Update the cFAOption
        CFAOption updatedCFAOption = cFAOptionRepository.findById(cFAOption.getId()).get();
        updatedCFAOption
            .type(UPDATED_TYPE)
            .description(UPDATED_DESCRIPTION)
            .isDefault(UPDATED_IS_DEFAULT);

        restCFAOptionMockMvc.perform(put("/api/cfa-options")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedCFAOption)))
            .andExpect(status().isOk());

        // Validate the CFAOption in the database
        List<CFAOption> cFAOptionList = cFAOptionRepository.findAll();
        assertThat(cFAOptionList).hasSize(databaseSizeBeforeUpdate);
        CFAOption testCFAOption = cFAOptionList.get(cFAOptionList.size() - 1);
        assertThat(testCFAOption.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testCFAOption.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testCFAOption.getIsDefault()).isEqualTo(UPDATED_IS_DEFAULT);
    }

    @Test
    public void updateNonExistingCFAOption() throws Exception {
        int databaseSizeBeforeUpdate = cFAOptionRepository.findAll().size();

        // Create the CFAOption

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCFAOptionMockMvc.perform(put("/api/cfa-options")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(cFAOption)))
            .andExpect(status().isBadRequest());

        // Validate the CFAOption in the database
        List<CFAOption> cFAOptionList = cFAOptionRepository.findAll();
        assertThat(cFAOptionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteCFAOption() throws Exception {
        // Initialize the database
        cFAOptionRepository.save(cFAOption);

        int databaseSizeBeforeDelete = cFAOptionRepository.findAll().size();

        // Delete the cFAOption
        restCFAOptionMockMvc.perform(delete("/api/cfa-options/{id}", cFAOption.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<CFAOption> cFAOptionList = cFAOptionRepository.findAll();
        assertThat(cFAOptionList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CFAOption.class);
        CFAOption cFAOption1 = new CFAOption();
        cFAOption1.setId("id1");
        CFAOption cFAOption2 = new CFAOption();
        cFAOption2.setId(cFAOption1.getId());
        assertThat(cFAOption1).isEqualTo(cFAOption2);
        cFAOption2.setId("id2");
        assertThat(cFAOption1).isNotEqualTo(cFAOption2);
        cFAOption1.setId(null);
        assertThat(cFAOption1).isNotEqualTo(cFAOption2);
    }
}
