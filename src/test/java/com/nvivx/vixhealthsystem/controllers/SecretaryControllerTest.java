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

@ExtendWith(MockitoExtension.class)
class SecretaryControllerTest {

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

    @InjectMocks
    private SecretaryController secretaryController;

    private Secretary testSecretary;

    @BeforeEach
    void setUp() {
        testSecretary = new Secretary();
        testSecretary.setId(1L);
        testSecretary.setName("Test");
        testSecretary.setSurname("Secretary");
    }

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

    @Test
    void testViewAllRooms() {
        // Arrange
        when(roomService.getAllRooms()).thenReturn(java.util.Collections.emptyList());
        when(patientService.findAllPatients()).thenReturn(java.util.Collections.emptyList());

        // Act
        String result = secretaryController.viewAllRooms(model);

        // Assert
        assertEquals("secretary/rooms", result);
        verify(model).addAttribute(eq("rooms"), any());
        verify(model).addAttribute(eq("patients"), any());
        verify(model).addAttribute(eq("pageTitle"), eq("All Rooms"));
    }

    @Test
    void testViewAvailableRooms() {
        // Arrange
        when(roomService.getAvailableRooms()).thenReturn(java.util.Collections.emptyList());
        when(patientService.findAllPatients()).thenReturn(java.util.Collections.emptyList());

        // Act
        String result = secretaryController.viewAvailableRooms(model);

        // Assert
        assertEquals("secretary/rooms", result);
        verify(model).addAttribute(eq("rooms"), any());
        verify(model).addAttribute(eq("patients"), any());
        verify(model).addAttribute(eq("pageTitle"), eq("Available Rooms"));
        verify(model).addAttribute(eq("isAvailableView"), eq(true));
    }

    @Test
    void testManageAppointments() {
        // Arrange
        when(appointmentRepository.findAll()).thenReturn(java.util.Collections.emptyList());
        when(patientService.findAllPatients()).thenReturn(java.util.Collections.emptyList());
        when(employeeService.findAllMedicalSpecialists()).thenReturn(java.util.Collections.emptyList());

        // Act
        String result = secretaryController.manageAppointments(model);

        // Assert
        assertEquals("secretary/manage-appointments", result);
        verify(model).addAttribute(eq("appointments"), any());
        verify(model).addAttribute(eq("patients"), any());
        verify(model).addAttribute(eq("specialists"), any());
        verify(model).addAttribute(eq("pageTitle"), eq("Manage Appointments"));
    }

    @Test
    void testShowPatientSearchForm() {
        // Act
        String result = secretaryController.showPatientSearchForm(model);

        // Assert
        assertEquals("secretary/patient-search", result);
        verify(model).addAttribute(eq("pageTitle"), eq("Search Patients"));
    }
}