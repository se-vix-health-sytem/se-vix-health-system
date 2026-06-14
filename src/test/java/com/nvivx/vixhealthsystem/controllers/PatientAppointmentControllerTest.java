package com.nvivx.vixhealthsystem.controllers;

import com.nvivx.vixhealthsystem.controllers.patient.PatientAppointmentController;
import com.nvivx.vixhealthsystem.model.person.Patient;
import com.nvivx.vixhealthsystem.repository.JsonAppointmentRepository;
import com.nvivx.vixhealthsystem.service.core.EmployeeService;
import com.nvivx.vixhealthsystem.service.core.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @class PatientAppointmentControllerTest
 * @brief Unit tests for PatientAppointmentController (patient booking module).
 *
 * These tests verify appointment-related workflows for patients, including:
 * - Viewing appointments (logged in / not logged in)
 * - Displaying booking form
 * - Cancelling appointments
 *
 * The controller is tested using standalone MockMvc with mocked dependencies.
 */
@ExtendWith(MockitoExtension.class)
class PatientAppointmentControllerTest {

    /// MockMvc instance used to simulate HTTP requests to the controller.
    private MockMvc mockMvc;

    /// Mocked appointment repository.
    @Mock
    private JsonAppointmentRepository appointmentRepository;

    /// Mocked patient service for patient-related operations.
    @Mock
    private PatientService patientService;

    /// Mocked employee service for retrieving specialists.
    @Mock
    private EmployeeService employeeService;

    /// Controller under test with injected mocked dependencies.
    @InjectMocks
    private PatientAppointmentController controller;

    /**
     * @brief Initializes MockMvc before each test.
     */
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    /**
     * @brief Creates a mock patient for session simulation.
     */
    private Patient mockPatient() {
        Patient p = new Patient();
        p.setId(1L);
        p.setName("John");
        p.setSurname("Doe");
        return p;
    }

    // =========================================================
    // VIEW APPOINTMENTS
    // =========================================================

    /**
     * @brief Verifies appointment page for logged-in patient.
     *
     * Ensures:
     * - Appointments page loads successfully
     * - Model contains upcoming and past appointments
     * - Patient data is included in model
     */
    @Test
    void testViewAppointments_loggedIn() throws Exception {

        Patient patient = mockPatient();

        when(appointmentRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/patient/appointments")
                        .sessionAttr("patient", patient))
                .andExpect(status().isOk())
                .andExpect(view().name("patient/appointments"))
                .andExpect(model().attributeExists("upcomingAppointments"))
                .andExpect(model().attributeExists("pastAppointments"))
                .andExpect(model().attributeExists("patient"));
    }

    /**
     * @brief Verifies redirect when patient is not logged in.
     */
    @Test
    void testViewAppointments_notLoggedIn() throws Exception {

        mockMvc.perform(get("/patient/appointments"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patient/login"));
    }

    // =========================================================
    // BOOKING FORM
    // =========================================================

    /**
     * @brief Verifies that booking form is displayed correctly.
     *
     * Ensures:
     * - Specialists list is loaded
     * - Patient data is present in model
     */
    @Test
    void testShowBookingForm() throws Exception {

        Patient patient = mockPatient();

        when(employeeService.findAllMedicalSpecialists())
                .thenReturn(List.of());

        mockMvc.perform(get("/patient/appointments/book")
                        .sessionAttr("patient", patient))
                .andExpect(status().isOk())
                .andExpect(view().name("patient/book-appointment"))
                .andExpect(model().attributeExists("specialists"))
                .andExpect(model().attributeExists("patient"));
    }

    // =========================================================
    // CANCEL APPOINTMENT
    // =========================================================

    /**
     * @brief Verifies cancel appointment flow when appointment is not found.
     *
     * Ensures:
     * - Request is handled gracefully
     * - User is redirected back to appointments page
     */
    @Test
    void testCancelAppointment_notFound() throws Exception {

        Patient patient = mockPatient();

        mockMvc.perform(post("/patient/appointments/1/cancel")
                        .sessionAttr("patient", patient))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patient/appointments"));
    }
}