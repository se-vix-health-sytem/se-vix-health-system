package com.nvivx.vixhealthsystem.controllers;

import com.nvivx.vixhealthsystem.controllers.services.AppointmentController;
import com.nvivx.vixhealthsystem.dto.CreateAppointmentRequest;
import com.nvivx.vixhealthsystem.model.medical.Appointment;
import com.nvivx.vixhealthsystem.service.medical.AppointmentService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @brief Unit tests for AppointmentController (REST layer).
 * Uses a Mockito mock of AppointmentService injected directly into the controller constructor
 * (plain JUnit, no MockMvc); covers successful booking and listing of all appointments.
 */
class AppointmentControllerTest {

    private final AppointmentService service = Mockito.mock(AppointmentService.class);
    private final AppointmentController controller = new AppointmentController(service);

    @Test
    void shouldBookAppointment() {
        CreateAppointmentRequest req = new CreateAppointmentRequest();
        req.setDateTime(LocalDateTime.of(2026, 6, 10, 10, 0));
        req.setDuration(30);
        req.setNotes("General checkup");

        Appointment mockAppointment =
                new Appointment(1, req.getDateTime(), req.getDuration(), req.getNotes());

        Mockito.when(service.bookAppointment(Mockito.any(CreateAppointmentRequest.class)))
                .thenReturn(mockAppointment);

        Appointment result = controller.book(req);

        assertNotNull(result);
        assertEquals(30, result.getDuration());
        assertEquals("General checkup", result.getNotes());

        Mockito.verify(service).bookAppointment(Mockito.any(CreateAppointmentRequest.class));
    }

    @Test
    void shouldReturnAllAppointments() {
        Appointment a1 = new Appointment(1, LocalDateTime.now(), 30, "Visit");

        Mockito.when(service.getAllAppointments())
                .thenReturn(List.of(a1));

        List<Appointment> result = controller.getAll();

        assertEquals(1, result.size());
        Mockito.verify(service).getAllAppointments();
    }
}