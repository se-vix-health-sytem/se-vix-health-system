package com.nvivx.vixhealthsystem.controllers;

import com.nvivx.vixhealthsystem.controllers.site.MapController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @class MapControllerTest
 * @brief Unit tests for MapController (site map features).
 *
 * These tests verify that map-related pages (hospital map and directions)
 * are correctly rendered with required model attributes.
 *
 * The controller is tested in isolation using standalone MockMvc.
 */
class MapControllerTest {

    /// MockMvc instance used to simulate HTTP requests to the map controller.
    private MockMvc mockMvc;

    /**
     * @brief Initializes MockMvc before each test.
     *
     * The controller is tested without Spring context loading.
     */
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new MapController())
                .build();
    }

    /**
     * @brief Verifies that the hospital map page loads correctly.
     *
     * Ensures:
     * - HTTP 200 response is returned
     * - Correct view name is rendered
     * - Required model attributes (locations, pageTitle) exist
     */
    @Test
    void shouldShowHospitalMap() throws Exception {

        mockMvc.perform(get("/map/hospitals"))
                .andExpect(status().isOk())
                .andExpect(view().name("site/map/hospitals"))
                .andExpect(model().attributeExists("locations"))
                .andExpect(model().attributeExists("pageTitle"));
    }

    /**
     * @brief Verifies that the directions page loads correctly.
     *
     * Ensures:
     * - HTTP 200 response is returned
     * - Correct view name is rendered
     * - Page title is included in the model
     */
    @Test
    void shouldShowDirectionsPage() throws Exception {

        mockMvc.perform(get("/map/directions"))
                .andExpect(status().isOk())
                .andExpect(view().name("site/map/directions"))
                .andExpect(model().attributeExists("pageTitle"));
    }
}