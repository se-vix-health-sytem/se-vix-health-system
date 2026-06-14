package com.nvivx.vixhealthsystem.controllers;

import com.nvivx.vixhealthsystem.controllers.patient.PatientAuthController;
import com.nvivx.vixhealthsystem.model.person.Patient;
import com.nvivx.vixhealthsystem.service.AuditService;
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
 * @class PatientAuthControllerTest
 * @brief Unit tests for PatientAuthController (patient authentication module).
 *
 * These tests verify authentication flow, dashboard access, and account deletion logic:
 * - Login page rendering
 * - Failed authentication handling
 * - Successful login
 * - Dashboard access control
 * - Account deletion flow
 */
@ExtendWith(MockitoExtension.class)
class PatientAuthControllerTest {

    // ---------------- MOCKED DEPENDENCIES ----------------

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private PatientService patientService;

    @Mock
    private AuditService auditService;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    /// Controller under test with injected mocks.
    @InjectMocks
    private PatientAuthController controller;

    // =========================================================
    // LOGIN PAGE
    // =========================================================

    /**
     * @brief Verifies that login page is rendered correctly.
     */
    @Test
    void shouldShowLoginPage() {

        String view = controller.showLoginPage(null, model);

        assertEquals("patient/login", view);
        verify(model).addAttribute(eq("redirectUrl"), anyString());
    }

    // =========================================================
    // FAILED LOGIN
    // =========================================================

    /**
     * @brief Verifies authentication failure when patient is not found.
     *
     * Ensures:
     * - Login page is returned
     * - Error message is added to model
     * - No session or audit actions occur
     */
    @Test
    void shouldFailAuthentication_whenPatientNotFound() {

        when(patientService.findByFiscalCode("BAD"))
                .thenReturn(Optional.empty());

        String view = controller.authenticate(
                "BAD",
                null,
                session,
                request,
                response,
                model
        );

        assertEquals("patient/login", view);

        verify(model).addAttribute(eq("error"), anyString());
        verify(session, never()).setAttribute(eq("patient"), any());
        verify(auditService, never()).log(any(), any(), any(), any());
    }

    // =========================================================
    // SUCCESS LOGIN
    // =========================================================

    /**
     * @brief Verifies successful patient authentication.
     *
     * Ensures:
     * - Patient is stored in session
     * - Role is set correctly
     * - Audit log is recorded
     * - Redirect to dashboard occurs
     */
    @Test
    void shouldAuthenticateSuccessfully() {

        Patient patient = new Patient();
        patient.setId(1L);
        patient.setFiscalCode("GOOD");

        when(patientService.findByFiscalCode("GOOD"))
                .thenReturn(Optional.of(patient));

        String view = controller.authenticate(
                "GOOD",
                null,
                session,
                request,
                response,
                model
        );

        assertEquals("redirect:/patient/dashboard", view);

        verify(session).setAttribute("patient", patient);
        verify(session).setAttribute("role", "PATIENT");
        verify(auditService).log(eq("PATIENT_LOGIN"), eq("Patient"), eq("1"), anyString());
    }

    // =========================================================
    // DASHBOARD
    // =========================================================

    /**
     * @brief Verifies dashboard rendering for logged-in patient.
     */
    @Test
    void shouldShowDashboard_whenLoggedIn() {

        Patient patient = new Patient();
        patient.setId(1L);

        when(session.getAttribute("patient")).thenReturn(patient);
        when(patientService.findById(1L)).thenReturn(patient);

        String view = controller.dashboard(session, model);

        assertEquals("patient/dashboard", view);

        verify(model).addAttribute("patient", patient);
        verify(model).addAttribute("pageTitle", "Patient Dashboard");
    }

    /**
     * @brief Verifies redirect to login when patient is not authenticated.
     */
    @Test
    void shouldRedirectToLogin_whenNotLoggedIn() {

        when(session.getAttribute("patient")).thenReturn(null);

        String view = controller.dashboard(session, model);

        assertEquals("redirect:/patient/login", view);
    }

    // =========================================================
    // ACCOUNT DELETION
    // =========================================================

    /**
     * @brief Verifies confirmation page is shown when no confirmation is provided.
     */
    @Test
    void shouldShowConfirmDelete_whenNoConfirmationProvided() {

        Patient patient = new Patient();
        patient.setId(1L);

        when(session.getAttribute("patient")).thenReturn(patient);

        String view = controller.deleteAccount(null, session, model);

        assertEquals("patient/confirm-delete", view);

        verify(model).addAttribute("requireConfirm", true);
        verify(model).addAttribute("patient", patient);
    }

    /**
     * @brief Verifies successful patient account deletion.
     *
     * Ensures:
     * - Patient is removed from system
     * - Session is invalidated
     * - Redirect to home page occurs
     */
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