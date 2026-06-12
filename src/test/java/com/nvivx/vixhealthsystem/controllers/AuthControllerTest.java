package com.nvivx.vixhealthsystem.controllers;

import com.nvivx.vixhealthsystem.model.person.Patient;
import com.nvivx.vixhealthsystem.model.person.employee.*;
import com.nvivx.vixhealthsystem.service.core.EmployeeService;
import com.nvivx.vixhealthsystem.service.core.PatientService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private EmployeeService employeeService;

    @Mock
    private PatientService patientService;

    @Mock
    private Model model;

    @Mock
    private HttpSession session;

    @InjectMocks
    private AuthController controller;

    @Test
    void shouldReturnLoginPage() {
        String view = controller.loginPage(null, model);
        assertEquals("login", view);
    }

    @Test
    void shouldShowErrorOnLoginPage() {
        String view = controller.loginPage("error", model);

        verify(model).addAttribute("error", "Invalid credentials");
        assertEquals("login", view);
    }

    @Test
    void shouldAuthenticateMedicalSpecialist() {
        MedicalSpecialist employee = mock(MedicalSpecialist.class);

        when(employeeService.findByEmail("doc@test.com")).thenReturn(employee);

        String result = controller.authenticate(
                "doc@test.com",
                "pass",
                session,
                model
        );

        assertEquals("redirect:/medical-specialist/dashboard", result);
        verify(session).setAttribute("user", employee);
        verify(session).setAttribute(eq("role"), any());
    }

    @Test
    void shouldAuthenticateTechnician() {
        Technician employee = mock(Technician.class);

        when(employeeService.findByEmail("tech@test.com")).thenReturn(employee);

        String result = controller.authenticate(
                "tech@test.com",
                "pass",
                session,
                model
        );

        assertEquals("redirect:/technician/dashboard", result);
    }

    @Test
    void shouldAuthenticateBuyer() {
        Buyer employee = mock(Buyer.class);

        when(employeeService.findByEmail("buyer@test.com")).thenReturn(employee);

        String result = controller.authenticate(
                "buyer@test.com",
                "pass",
                session,
                model
        );

        assertEquals("redirect:/buyer/dashboard", result);
    }

    @Test
    void shouldAuthenticatePatientWhenEmployeeFails() {
        when(employeeService.findByEmail("pat@test.com"))
                .thenThrow(new RuntimeException());

        Patient patient = mock(Patient.class);

        when(patientService.findByFiscalCode("pat@test.com"))
                .thenReturn(Optional.of(patient));

        String result = controller.authenticate(
                "pat@test.com",
                "pass",
                session,
                model
        );

        assertEquals("redirect:/patient/dashboard", result);
        verify(session).setAttribute("patient", patient);
        verify(session).setAttribute("role", "PATIENT");
    }

    @Test
    void shouldFailAuthentication() {
        when(employeeService.findByEmail(anyString()))
                .thenThrow(new RuntimeException());

        when(patientService.findByFiscalCode(anyString()))
                .thenReturn(Optional.empty());

        String result = controller.authenticate(
                "wrong",
                "wrong",
                session,
                model
        );

        verify(model).addAttribute("error", "Invalid credentials");
        assertEquals("login", result);
    }

    @Test
    void shouldLogout() {
        String result = controller.logout(session);

        assertEquals("redirect:/", result);
        verify(session).invalidate();
    }

    @Test
    void shouldProcessRoleSelection() {
        String result = controller.processLogin("TECHNICIAN", session);

        assertEquals("redirect:/technician/dashboard", result);
        verify(session).setAttribute("demoRole", "TECHNICIAN");
    }

    @Test
    void shouldRedirectUnknownRoleToLogin() {
        String result = controller.processLogin("UNKNOWN", session);

        assertEquals("redirect:/login", result);
    }
}