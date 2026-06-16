package com.nvivx.vixhealthsystem.controllers.site;

import com.nvivx.vixhealthsystem.model.facility.Department;
import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import com.nvivx.vixhealthsystem.service.core.DepartmentService;
import com.nvivx.vixhealthsystem.service.core.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @class HomePageControllerTest
 * @brief Unit tests for HomePageController (public landing page).
 *
 * These tests ensure that the homepage loads correctly with featured specialists,
 * departments, and aggregated statistics.
 *
 * The controller is tested in isolation using MockMvc with mocked services.
 */
class HomePageControllerTest {

    /// MockMvc instance used to simulate HTTP requests to the homepage controller.
    private MockMvc mockMvc;

    /// Mocked service providing employee (medical specialist) data.
    private EmployeeService employeeService;

    /// Mocked service providing department-related data.
    private DepartmentService departmentService;

    /**
     * @brief Initializes controller and MockMvc before each test.
     *
     * Dependencies are mocked manually and injected into the controller.
     */
    @BeforeEach
    void setUp() {

        employeeService = mock(EmployeeService.class);
        departmentService = mock(DepartmentService.class);

        HomePageController controller =
                new HomePageController(employeeService, departmentService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    /**
     * @brief Verifies that the homepage loads with all required data.
     *
     * Ensures:
     * - Featured medical specialists are included
     * - Departments list is present
     * - Doctor image mapping is provided
     * - Aggregate statistics are calculated and added to model
     */
    @Test
    void shouldLoadHomePageWithCorrectData() throws Exception {

        // ---------------- MOCK SPECIALISTS ----------------
        MedicalSpecialist doc1 = mock(MedicalSpecialist.class);
        MedicalSpecialist doc2 = mock(MedicalSpecialist.class);

        when(employeeService.findAllMedicalSpecialists())
                .thenReturn(List.of(doc1, doc2));

        // ---------------- MOCK DEPARTMENTS ----------------
        Department d1 = mock(Department.class);
        Department d2 = mock(Department.class);

        when(d1.getName()).thenReturn("Cardiology");
        when(d2.getName()).thenReturn("Administration");

        when(departmentService.getAllDepartments())
                .thenReturn(List.of(d1, d2));

        // ---------------- MOCK IMAGE MAP ----------------
        when(departmentService.getAllDoctorImageMap())
                .thenReturn(Map.of(1L, "img1.jpg"));

        // ---------------- EXECUTE TEST ----------------
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("site/index"))
                .andExpect(model().attributeExists("featuredSpecialists"))
                .andExpect(model().attributeExists("departments"))
                .andExpect(model().attributeExists("doctorImages"))
                .andExpect(model().attributeExists("totalSpecialists"))
                .andExpect(model().attributeExists("totalDepartments"));
    }
}