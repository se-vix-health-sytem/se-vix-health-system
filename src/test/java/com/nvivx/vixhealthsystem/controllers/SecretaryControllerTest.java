package com.nvivx.vixhealthsystem.controllers;

import com.nvivx.vixhealthsystem.model.facility.Room;
import com.nvivx.vixhealthsystem.model.facility.InternationRoom;
import com.nvivx.vixhealthsystem.model.medical.Appointment;
import com.nvivx.vixhealthsystem.model.person.Patient;
import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import com.nvivx.vixhealthsystem.repository.JsonAppointmentRepository;
import com.nvivx.vixhealthsystem.service.core.EmployeeService;
import com.nvivx.vixhealthsystem.service.core.PatientService;
import com.nvivx.vixhealthsystem.service.resources.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecretaryControllerTest {

    private RoomService roomService;
    private EmployeeService employeeService;
    private PatientService patientService;
    private JsonAppointmentRepository appointmentRepository;

    private SecretaryController controller;

    @BeforeEach
    void setUp() {
        roomService = mock(RoomService.class);
        employeeService = mock(EmployeeService.class);
        patientService = mock(PatientService.class);
        appointmentRepository = mock(JsonAppointmentRepository.class);

        controller = new SecretaryController(
                roomService,
                employeeService,
                patientService,
                appointmentRepository
        );
    }

    @Test
    void shouldLoadDashboard() {

        when(roomService.getAllInpatientRooms()).thenReturn(List.of());
        when(roomService.getAvailableRooms()).thenReturn(List.of());
        when(roomService.getTotalAvailableBeds()).thenReturn(10);
        when(appointmentRepository.findAll()).thenReturn(List.of());

        Model model = mock(Model.class);

        String view = controller.dashboard(model);

        assertEquals("secretary/dashboard", view);

        verify(model).addAttribute(eq("totalRooms"), any());
        verify(model).addAttribute(eq("availableRooms"), any());
        verify(model).addAttribute(eq("totalAppointments"), any());
    }

    @Test
    void shouldAdmitPatientSuccessfully() {

        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        String view = controller.admitPatient(1L, 2L, redirectAttributes);

        assertEquals("redirect:/secretary/rooms", view);

        verify(roomService).admitPatient(1L, 2L);
        verify(redirectAttributes).addFlashAttribute(eq("message"), any());
    }

    @Test
    void shouldHandleAdmitPatientFailure() {

        doThrow(new RuntimeException("Room full"))
                .when(roomService).admitPatient(anyLong(), anyLong());

        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        String view = controller.admitPatient(1L, 2L, redirectAttributes);

        assertEquals("redirect:/secretary/rooms", view);

        verify(redirectAttributes).addFlashAttribute(eq("error"), contains("Room full"));
    }

    @Test
    void shouldDismissPatientSuccessfully() {

        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        String view = controller.dismissPatient(1L, 2L, redirectAttributes);

        assertEquals("redirect:/secretary/rooms", view);

        verify(roomService).dismissPatient(1L, 2L);
    }

    @Test
    void shouldBookAppointmentForPatient() {

        Patient patient = new Patient();
        MedicalSpecialist specialist = mock(MedicalSpecialist.class);

        when(patientService.findById(1L)).thenReturn(patient);
        when(employeeService.findById(2L)).thenReturn(specialist);

        Appointment saved = new Appointment();
        saved.setId(10);

        when(appointmentRepository.save(any())).thenReturn(saved);

        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        String view = controller.bookAppointmentForPatient(
                1L,
                2L,
                "2026-06-10T10:00:00",
                redirectAttributes
        );

        assertEquals("redirect:/secretary/appointments", view);

        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void shouldCancelAppointment() {

        Appointment appointment = new Appointment();
        appointment.setId(1);

        when(appointmentRepository.findById(1)).thenReturn(appointment);

        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        String view = controller.cancelAppointment(1, redirectAttributes);

        assertEquals("redirect:/secretary/appointments", view);

        verify(appointmentRepository).save(appointment);
    }

    @Test
    void shouldRescheduleAppointment() {

        Appointment appointment = new Appointment();
        appointment.setId(1);

        when(appointmentRepository.findById(1)).thenReturn(appointment);

        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        String view = controller.rescheduleAppointment(
                1,
                "2026-06-11T10:00:00",
                redirectAttributes
        );

        assertEquals("redirect:/secretary/appointments", view);

        verify(appointmentRepository).save(appointment);
    }
}