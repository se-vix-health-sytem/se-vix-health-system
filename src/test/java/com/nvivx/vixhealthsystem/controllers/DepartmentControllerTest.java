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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DepartmentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DepartmentService departmentService;

    @InjectMocks
    private DepartmentController departmentController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(departmentController)
                .build();
    }

    @Test
    void shouldListDepartments() throws Exception {
        when(departmentService.getAllDepartments()).thenReturn(List.of());

        mockMvc.perform(get("/departments"))
                .andExpect(status().isOk())
                .andExpect(view().name("departments/list"))
                .andExpect(model().attributeExists("departments"))
                .andExpect(model().attributeExists("pageTitle"));

        verify(departmentService).getAllDepartments();
    }

    @Test
    void shouldShowDepartmentDetailWhenFound() throws Exception {
        Department dept = new Department();
        dept.setId(1L);
        dept.setName("Cardiology");

        when(departmentService.getDepartmentById("1")).thenReturn(dept);
        when(departmentService.getDoctorsByDepartment("1")).thenReturn(List.of());
        when(departmentService.getServicesByDepartment("1")).thenReturn(List.of());

        mockMvc.perform(get("/departments/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("departments/detail"))
                .andExpect(model().attributeExists("department"))
                .andExpect(model().attributeExists("doctors"))
                .andExpect(model().attributeExists("services"))
                .andExpect(model().attributeExists("pageTitle"));

        verify(departmentService).getDepartmentById("1");
    }

    @Test
    void shouldRedirectWhenDepartmentNotFound() throws Exception {
        when(departmentService.getDepartmentById("999")).thenReturn(null);

        mockMvc.perform(get("/departments/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/departments"));

        verify(departmentService).getDepartmentById("999");
    }
}