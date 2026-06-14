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
 * @brief Unit tests for PatientAppointmentController using Spring MVC MockMvc and Mockito mocks.
 * Covers appointment listing for logged-in and guest patients, the booking form, and
 * the cancel-appointment redirect when the appointment is not found.
 */
@ExtendWith(MockitoExtension.class)
class PatientAppointmentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private JsonAppointmentRepository appointmentRepository;

    @Mock
    private PatientService patientService;

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private PatientAppointmentController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    private Patient mockPatient() {
        Patient p = new Patient();
        p.setId(1L);
        p.setName("John");
        p.setSurname("Doe");
        return p;
    }

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

    @Test
    void testViewAppointments_notLoggedIn() throws Exception {

        mockMvc.perform(get("/patient/appointments"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patient/login"));
    }

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

    @Test
    void testCancelAppointment_notFound() throws Exception {

        Patient patient = mockPatient();

        mockMvc.perform(post("/patient/appointments/1/cancel")
                        .sessionAttr("patient", patient))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patient/appointments"));
    }
}