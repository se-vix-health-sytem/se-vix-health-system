package com.nvivx.vixhealthsystem.controllers;

import com.nvivx.vixhealthsystem.controllers.staff.SecretaryController;
import com.nvivx.vixhealthsystem.model.person.employee.Secretary;
import com.nvivx.vixhealthsystem.service.core.EmployeeService;
import com.nvivx.vixhealthsystem.service.core.PatientService;
import com.nvivx.vixhealthsystem.service.resources.RoomService;
import com.nvivx.vixhealthsystem.repository.JsonAppointmentRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @class SecretaryControllerTest
 * @brief Unit tests for SecretaryController (hospital administration module).
 *
 * These tests verify core secretary functionalities including:
 * - Dashboard statistics rendering
 * - Room management views (all / available)
 * - Appointment management page
 * - Patient search form rendering
 *
 * The controller is tested using direct method calls with Mockito mocks
 * and without Spring context or MockMvc.
 */
@ExtendWith(MockitoExtension.class)
class SecretaryControllerTest {

    // ---------------- MOCKED DEPENDENCIES ----------------

    @Mock
    private RoomService roomService;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private PatientService patientService;

    @Mock
    private JsonAppointmentRepository appointmentRepository;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    /// Controller under test with injected mocked dependencies.
    @InjectMocks
    private SecretaryController secretaryController;

    /// Sample secretary user used in dashboard tests.
    private Secretary testSecretary;

    /**
     * @brief Initializes test data before each test.
     */
    @BeforeEach
    void setUp() {
        testSecretary = new Secretary();
        testSecretary.setId(1L);
        testSecretary.setName("Test");
        testSecretary.setSurname("Secretary");
    }

    // =========================================================
    // DASHBOARD
    // =========================================================

    /**
     * @brief Verifies secretary dashboard loads with correct statistics.
     */
    @Test
    void testDashboard() {

        // Arrange
        when(session.getAttribute("user")).thenReturn(testSecretary);
        when(employeeService.findById(1L)).thenReturn(testSecretary);
        when(roomService.getAllInpatientRooms()).thenReturn(java.util.Collections.emptyList());
        when(roomService.getAvailableRooms()).thenReturn(java.util.Collections.emptyList());
        when(appointmentRepository.findAll()).thenReturn(java.util.Collections.emptyList());
        when(roomService.getTotalAvailableBeds()).thenReturn(0);

        // Act
        String result = secretaryController.dashboard(session, model);

        // Assert
        assertEquals("secretary/dashboard", result);

        verify(model).addAttribute(eq("pageTitle"), anyString());
        verify(model).addAttribute(eq("currentPage"), eq("dashboard"));
        verify(model).addAttribute(eq("totalRooms"), anyInt());
        verify(model).addAttribute(eq("availableRooms"), anyInt());
        verify(model).addAttribute(eq("totalAppointments"), anyInt());
        verify(model).addAttribute(eq("totalAvailableBeds"), anyInt());
    }

    // =========================================================
    // ROOMS
    // =========================================================

    /**
     * @brief Verifies that all rooms view loads correctly.
     */
    @Test
    void testViewAllRooms() {

        when(roomService.getAllRooms()).thenReturn(java.util.Collections.emptyList());
        when(patientService.findAllPatients()).thenReturn(java.util.Collections.emptyList());

        String result = secretaryController.viewAllRooms(model);

        assertEquals("secretary/rooms", result);

        verify(model).addAttribute(eq("rooms"), any());
        verify(model).addAttribute(eq("patients"), any());
        verify(model).addAttribute(eq("pageTitle"), eq("All Rooms"));
    }

    /**
     * @brief Verifies that available rooms view loads correctly.
     */
    @Test
    void testViewAvailableRooms() {

        when(roomService.getAvailableRooms()).thenReturn(java.util.Collections.emptyList());
        when(patientService.findAllPatients()).thenReturn(java.util.Collections.emptyList());

        String result = secretaryController.viewAvailableRooms(model);

        assertEquals("secretary/rooms", result);

        verify(model).addAttribute(eq("rooms"), any());
        verify(model).addAttribute(eq("patients"), any());
        verify(model).addAttribute(eq("pageTitle"), eq("Available Rooms"));
        verify(model).addAttribute(eq("isAvailableView"), eq(true));
    }

    // =========================================================
    // APPOINTMENTS
    // =========================================================

    /**
     * @brief Verifies appointment management page loads correctly.
     */
    @Test
    void testManageAppointments() {

        when(appointmentRepository.findAll()).thenReturn(java.util.Collections.emptyList());
        when(patientService.findAllPatients()).thenReturn(java.util.Collections.emptyList());
        when(employeeService.findAllMedicalSpecialists()).thenReturn(java.util.Collections.emptyList());

        String result = secretaryController.manageAppointments(model);

        assertEquals("secretary/manage-appointments", result);

        verify(model).addAttribute(eq("appointments"), any());
        verify(model).addAttribute(eq("patients"), any());
        verify(model).addAttribute(eq("specialists"), any());
        verify(model).addAttribute(eq("pageTitle"), eq("Manage Appointments"));
    }

    // =========================================================
    // PATIENT SEARCH
    // =========================================================

    /**
     * @brief Verifies patient search form rendering.
     */
    @Test
    void testShowPatientSearchForm() {

        String result = secretaryController.showPatientSearchForm(model);

        assertEquals("secretary/patient-search", result);

        verify(model).addAttribute(eq("pageTitle"), eq("Search Patients"));
    }
}