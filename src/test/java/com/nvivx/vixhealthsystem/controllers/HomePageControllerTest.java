package com.nvivx.vixhealthsystem.controllers;

import com.nvivx.vixhealthsystem.controllers.site.HomePageController;
import com.nvivx.vixhealthsystem.service.core.DepartmentService;
import com.nvivx.vixhealthsystem.service.core.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * @brief Unit tests for HomePageController using Spring MVC MockMvc with Mockito mocks.
 * Verifies that a GET to "/" returns HTTP 200 and renders the "index" view.
 */
class HomePageControllerTest {

    private MockMvc mockMvc;

    private EmployeeService employeeService;
    private DepartmentService departmentService;

    @BeforeEach
    void setUp() {

        employeeService = Mockito.mock(EmployeeService.class);
        departmentService = Mockito.mock(DepartmentService.class);

        HomePageController controller =
                new HomePageController(
                        employeeService,
                        departmentService
                );

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void shouldReturnHomePage() throws Exception {

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }
}