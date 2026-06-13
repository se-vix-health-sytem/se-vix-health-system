package com.nvivx.vixhealthsystem.controllers;

import com.nvivx.vixhealthsystem.controllers.site.MapController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MapControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new MapController())
                .build();
    }

    @Test
    void shouldShowHospitalMap() throws Exception {
        mockMvc.perform(get("/map/hospitals"))
                .andExpect(status().isOk())
                .andExpect(view().name("map/hospitals"))
                .andExpect(model().attributeExists("locations"))
                .andExpect(model().attributeExists("apiKey"))
                .andExpect(model().attributeExists("pageTitle"));
    }

    @Test
    void shouldShowDirectionsPage() throws Exception {
        mockMvc.perform(get("/map/directions"))
                .andExpect(status().isOk())
                .andExpect(view().name("map/directions"))
                .andExpect(model().attributeExists("pageTitle"));
    }
}