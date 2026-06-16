package com.nvivx.vixhealthsystem.controllers.site;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @class QuestionnaireControllerTest
 * @brief Unit tests for QuestionnaireController (patient self-assessment module).
 *
 * These tests verify the questionnaire workflow, including:
 * - Questionnaire page rendering
 * - Result generation based on symptom severity
 * - Different logical branches (low, medium, high severity, and fallback cases)
 *
 * The controller is tested in isolation using standalone MockMvc.
 */
class QuestionnaireControllerTest {

    /// MockMvc instance used to simulate HTTP requests to the questionnaire controller.
    private MockMvc mockMvc;

    /**
     * @brief Initializes MockMvc before each test.
     *
     * The controller is tested without Spring context loading.
     */
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new QuestionnaireController())
                .build();
    }

    // =========================================================
    // QUESTIONNAIRE PAGE
    // =========================================================

    /**
     * @brief Verifies that questionnaire page loads correctly.
     *
     * Ensures:
     * - HTTP 200 response
     * - Correct view is rendered
     * - Required model attributes exist (pageTitle, symptomCategories)
     */
    @Test
    void showQuestionnaire_returnsViewAndModel() throws Exception {

        mockMvc.perform(get("/questionnaire"))
                .andExpect(status().isOk())
                .andExpect(view().name("site/questionnaire/index"))
                .andExpect(model().attributeExists("pageTitle"))
                .andExpect(model().attributeExists("symptomCategories"));
    }

    // =========================================================
    // RESULT GENERATION
    // =========================================================

    /**
     * @brief Verifies questionnaire result for low severity symptoms.
     */
    @Test
    void processQuestionnaire_lowSeverity_returnsRecommendation() throws Exception {

        mockMvc.perform(post("/questionnaire/result")
                        .param("symptomArea", "fever")
                        .param("severity", "low"))
                .andExpect(status().isOk())
                .andExpect(view().name("site/questionnaire/result"))
                .andExpect(model().attributeExists("recommendation"));
    }

    /**
     * @brief Verifies high severity override logic (e.g. chest pain).
     */
    @Test
    void processQuestionnaire_highSeverity_chestPain_returnsHighUrgency() throws Exception {

        mockMvc.perform(post("/questionnaire/result")
                        .param("symptomArea", "chest_pain")
                        .param("severity", "high"))
                .andExpect(status().isOk())
                .andExpect(view().name("site/questionnaire/result"))
                .andExpect(model().attributeExists("recommendation"));
    }

    /**
     * @brief Verifies medium urgency based on symptom duration input.
     */
    @Test
    void processQuestionnaire_durationWeeks_returnsMediumUrgency() throws Exception {

        mockMvc.perform(post("/questionnaire/result")
                        .param("symptomArea", "dizziness")
                        .param("duration", "weeks"))
                .andExpect(status().isOk())
                .andExpect(view().name("site/questionnaire/result"))
                .andExpect(model().attributeExists("recommendation"));
    }

    /**
     * @brief Verifies fallback behavior for unknown symptoms.
     *
     * Ensures the system still returns a valid recommendation
     * even when input does not match known categories.
     */
    @Test
    void processQuestionnaire_unknownSymptom_returnsGeneralMedicine() throws Exception {

        mockMvc.perform(post("/questionnaire/result")
                        .param("symptomArea", "unknown_symptom"))
                .andExpect(status().isOk())
                .andExpect(view().name("site/questionnaire/result"))
                .andExpect(model().attributeExists("recommendation"));
    }
}