package com.nvivx.vixhealthsystem.controllers;

import com.nvivx.vixhealthsystem.controllers.site.DepartmentController;
import com.nvivx.vixhealthsystem.model.facility.Department;
import com.nvivx.vixhealthsystem.service.core.DepartmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @class DepartmentControllerTest
 * @brief Unit tests for DepartmentController (public site module).
 *
 * These tests verify that department listing and department detail pages
 * are correctly rendered and that proper redirects occur when data is missing.
 *
 * The controller is tested using MockMvc in standalone mode with mocked services.
 */
@ExtendWith(MockitoExtension.class)
class DepartmentControllerTest {

    /// MockMvc instance used to simulate HTTP requests to the controller.
    private MockMvc mockMvc;

    /// Mocked service layer responsible for department-related business logic.
    @Mock
    private DepartmentService departmentService;

    /// Controller under test with mocked dependencies injected automatically.
    @InjectMocks
    private DepartmentController departmentController;

    /**
     * @brief Sets up MockMvc before each test.
     *
     * Initializes the controller in standalone mode without loading Spring context.
     */
    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(departmentController)
                .build();
    }

    /**
     * @brief Verifies that the department listing page loads correctly.
     *
     * Ensures:
     * - HTTP 200 response is returned
     * - Correct view name is rendered
     * - Model contains required attributes
     * - Service layer is called once
     */
    @Test
    void shouldListDepartments() throws Exception {

        // Arrange: create sample department
        Department dept = new Department();
        dept.setId(1L);
        dept.setName("Cardiology");

        when(departmentService.getAllDepartments()).thenReturn(List.of(dept));

        // Act & Assert: perform request and validate response
        mockMvc.perform(get("/departments"))
                .andExpect(status().isOk())
                .andExpect(view().name("departments/list"))
                .andExpect(model().attributeExists("departments"))
                .andExpect(model().attributeExists("pageTitle"));

        // Verify service interaction
        verify(departmentService).getAllDepartments();
    }

    /**
     * @brief Verifies that department detail page loads correctly when department exists.
     *
     * Ensures:
     * - Department details are loaded
     * - Associated doctors and services are included
     * - Doctor image mapping is provided
     * - Correct view is returned
     */
    @Test
    void shouldShowDepartmentDetailWhenFound() throws Exception {

        // Arrange: sample department
        Department dept = new Department();
        dept.setId(1L);
        dept.setName("Cardiology");

        when(departmentService.getDepartmentById("1")).thenReturn(dept);
        when(departmentService.getDoctorsByDepartment(1L)).thenReturn(List.of());
        when(departmentService.getServicesByDepartment(1L)).thenReturn(List.of());
        when(departmentService.getDoctorImageMap(1L)).thenReturn(Map.of());

        // Act & Assert
        mockMvc.perform(get("/departments/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("departments/detail"))
                .andExpect(model().attributeExists("department"))
                .andExpect(model().attributeExists("doctors"))
                .andExpect(model().attributeExists("services"))
                .andExpect(model().attributeExists("doctorImages"))
                .andExpect(model().attributeExists("pageTitle"));

        // Verify correct service usage
        verify(departmentService).getDepartmentById("1");
    }

    /**
     * @brief Verifies redirection when a department is not found.
     *
     * Ensures:
     * - Service returns null for missing department
     * - User is redirected to department list page
     */
    @Test
    void shouldRedirectWhenDepartmentNotFound() throws Exception {

        when(departmentService.getDepartmentById("999")).thenReturn(null);

        mockMvc.perform(get("/departments/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/departments"));

        verify(departmentService).getDepartmentById("999");
    }
}