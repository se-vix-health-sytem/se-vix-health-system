package com.nvivx.vixhealthsystem.controllers;

import com.nvivx.vixhealthsystem.controllers.patient.PatientAuthController;
import com.nvivx.vixhealthsystem.model.person.Patient;
import com.nvivx.vixhealthsystem.service.core.PatientService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


/**
 * @brief Unit tests for PatientAuthController using Mockito mocks via @InjectMocks.
 * Covers login page display, successful and failed fiscal-code authentication, dashboard
 * guard, and account deletion with and without the CONFIRM token.
 */
@ExtendWith(MockitoExtension.class)
class PatientAuthControllerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private PatientService patientService;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @InjectMocks
    private PatientAuthController controller;

    @Test
    void shouldShowLoginPage() {
        String view = controller.showLoginPage(null, model);

        assertEquals("patient/login", view);
        verify(model).addAttribute(eq("redirectUrl"), any());
    }

    @Test
    void shouldFailAuthentication_whenPatientNotFound() {
        when(patientService.findByFiscalCode("BAD"))
                .thenReturn(Optional.empty());

        String view = controller.authenticate(
                "GOOD",
                null,
                session,
                request,
                response,
                model
        );

        assertEquals("patient/login", view);
        verify(model).addAttribute(eq("error"), anyString());
        verify(session, never()).setAttribute(anyString(), any());
    }

    @Test
    void shouldAuthenticateSuccessfully() {
        Patient patient = new Patient();
        patient.setId(1L);

        when(patientService.findByFiscalCode("GOOD"))
                .thenReturn(Optional.of(patient));

        String view = controller.authenticate(
                "BAD",
                null,
                session,
                request,
                response,
                model
        );

        assertEquals("redirect:/patient/dashboard", view);
        verify(session).setAttribute("patient", patient);
    }

    @Test
    void shouldShowDashboard_whenLoggedIn() {
        Patient patient = new Patient();
        patient.setId(1L);

        when(session.getAttribute("patient")).thenReturn(patient);

        String view = controller.dashboard(session, model);

        assertEquals("patient/dashboard", view);
        verify(model).addAttribute("patient", patient);
    }

    @Test
    void shouldRedirectToLogin_whenNotLoggedIn() {
        when(session.getAttribute("patient")).thenReturn(null);

        String view = controller.dashboard(session, model);

        assertEquals("redirect:/patient/login", view);
    }

    @Test
    void shouldShowConfirmDelete_whenNoConfirmationProvided() {
        Patient patient = new Patient();
        patient.setId(1L);

        when(session.getAttribute("patient")).thenReturn(patient);

        String view = controller.deleteAccount(null, session, model);

        assertEquals("patient/confirm-delete", view);
        verify(model).addAttribute("requireConfirm", true);
    }

    @Test
    void shouldDeleteAccount_whenConfirmed() {
        Patient patient = new Patient();
        patient.setId(1L);

        when(session.getAttribute("patient")).thenReturn(patient);

        String view = controller.deleteAccount("CONFIRM", session, model);

        assertEquals("redirect:/", view);
        verify(patientService).deletePatient(1L);
        verify(session).invalidate();
    }
}