package org.unibremen.mcyl.androidslicer.web.rest;

import org.unibremen.mcyl.androidslicer.AndroidSlicerApp;
import org.unibremen.mcyl.androidslicer.domain.SlicerSetting;
import org.unibremen.mcyl.androidslicer.repository.SlicerSettingRepository;
import org.unibremen.mcyl.androidslicer.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;


import java.util.List;

import static org.unibremen.mcyl.androidslicer.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link SlicerSettingResource} REST controller.
 */
@SpringBootTest(classes = AndroidSlicerApp.class)
@Import(TestMongoConfig.class)
public class SlicerSettingResourceIT {

    private static final String DEFAULT_KEY = "AAAAAAAAAA";
    private static final String UPDATED_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_VALUE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    @Autowired
    private SlicerSettingRepository slicerSettingRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private Validator validator;

    private MockMvc restSlicerSettingMockMvc;

    private SlicerSetting slicerSetting;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final SlicerSettingResource slicerSettingResource = new SlicerSettingResource(slicerSettingRepository);
        this.restSlicerSettingMockMvc = MockMvcBuilders.standaloneSetup(slicerSettingResource)
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
    public static SlicerSetting createEntity() {
        SlicerSetting slicerSetting = new SlicerSetting()
            .key(DEFAULT_KEY)
            .value(DEFAULT_VALUE)
            .description(DEFAULT_DESCRIPTION);
        return slicerSetting;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SlicerSetting createUpdatedEntity() {
        SlicerSetting slicerSetting = new SlicerSetting()
            .key(UPDATED_KEY)
            .value(UPDATED_VALUE)
            .description(UPDATED_DESCRIPTION);
        return slicerSetting;
    }

    @BeforeEach
    public void initTest() {
        slicerSettingRepository.deleteAll();
        slicerSetting = createEntity();
    }

    @Test
    public void checkKeyIsRequired() throws Exception {
        int databaseSizeBeforeTest = slicerSettingRepository.findAll().size();
        // set the field null
        slicerSetting.setKey(null);

        // Create the SlicerSetting, which fails.

        restSlicerSettingMockMvc.perform(put("/api/slicer-settings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(slicerSetting)))
            .andExpect(status().isBadRequest());

        List<SlicerSetting> slicerSettingList = slicerSettingRepository.findAll();
        assertThat(slicerSettingList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = slicerSettingRepository.findAll().size();
        // set the field null
        slicerSetting.setValue(null);

        // Create the SlicerSetting, which fails.

        restSlicerSettingMockMvc.perform(put("/api/slicer-settings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(slicerSetting)))
            .andExpect(status().isBadRequest());

        List<SlicerSetting> slicerSettingList = slicerSettingRepository.findAll();
        assertThat(slicerSettingList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void getAllSlicerSettings() throws Exception {
        // Initialize the database
        slicerSettingRepository.save(slicerSetting);

        // Get all the slicerSettingList
        restSlicerSettingMockMvc.perform(get("/api/slicer-settings?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(slicerSetting.getId())))
            .andExpect(jsonPath("$.[*].key").value(hasItem(DEFAULT_KEY)))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }
    
    @Test
    public void getSlicerSetting() throws Exception {
        // Initialize the database
        slicerSettingRepository.save(slicerSetting);

        // Get the slicerSetting
        restSlicerSettingMockMvc.perform(get("/api/slicer-settings/{id}", slicerSetting.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(slicerSetting.getId()))
            .andExpect(jsonPath("$.key").value(DEFAULT_KEY))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    public void getNonExistingSlicerSetting() throws Exception {
        // Get the slicerSetting
        restSlicerSettingMockMvc.perform(get("/api/slicer-settings/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateSlicerSetting() throws Exception {
        // Initialize the database
        slicerSettingRepository.save(slicerSetting);

        int databaseSizeBeforeUpdate = slicerSettingRepository.findAll().size();

        // Update the slicerSetting
        SlicerSetting updatedSlicerSetting = slicerSettingRepository.findById(slicerSetting.getId()).get();
        updatedSlicerSetting
            .key(UPDATED_KEY)
            .value(UPDATED_VALUE)
            .description(UPDATED_DESCRIPTION);

        restSlicerSettingMockMvc.perform(put("/api/slicer-settings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedSlicerSetting)))
            .andExpect(status().isOk());

        // Validate the SlicerSetting in the database
        List<SlicerSetting> slicerSettingList = slicerSettingRepository.findAll();
        assertThat(slicerSettingList).hasSize(databaseSizeBeforeUpdate);
        SlicerSetting testSlicerSetting = slicerSettingList.get(slicerSettingList.size() - 1);
        //assertThat(testSlicerSetting.getKey()).isEqualTo(UPDATED_KEY); // key cannot be updated
        assertThat(testSlicerSetting.getValue()).isEqualTo(UPDATED_VALUE); 
        // assertThat(testSlicerSetting.getDescription()).isEqualTo(UPDATED_DESCRIPTION); // cannot be updated
    }

    @Test
    public void updateNonExistingSlicerSetting() throws Exception {
        int databaseSizeBeforeUpdate = slicerSettingRepository.findAll().size();

        // Create the SlicerSetting

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSlicerSettingMockMvc.perform(put("/api/slicer-settings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(slicerSetting)))
            .andExpect(status().isBadRequest());

        // Validate the SlicerSetting in the database
        List<SlicerSetting> slicerSettingList = slicerSettingRepository.findAll();
        assertThat(slicerSettingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SlicerSetting.class);
        SlicerSetting slicerSetting1 = new SlicerSetting();
        slicerSetting1.setId("id1");
        SlicerSetting slicerSetting2 = new SlicerSetting();
        slicerSetting2.setId(slicerSetting1.getId());
        assertThat(slicerSetting1).isEqualTo(slicerSetting2);
        slicerSetting2.setId("id2");
        assertThat(slicerSetting1).isNotEqualTo(slicerSetting2);
        slicerSetting1.setId(null);
        assertThat(slicerSetting1).isNotEqualTo(slicerSetting2);
    }
}
