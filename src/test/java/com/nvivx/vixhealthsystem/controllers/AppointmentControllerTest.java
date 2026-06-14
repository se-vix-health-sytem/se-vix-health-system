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
 * @class AppointmentControllerTest
 * @brief Unit tests for AppointmentController (REST layer).
 *
 * These tests validate the controller logic in isolation by mocking
 * AppointmentService. No Spring context or MockMvc is used.
 *
 * Focus:
 * - Booking an appointment through the controller
 * - Retrieving all stored appointments
 */
class AppointmentControllerTest {

    /// Mocked service layer used to isolate controller logic.
    private final AppointmentService service = Mockito.mock(AppointmentService.class);

    /// Controller under test with injected mocked service.
    private final AppointmentController controller = new AppointmentController(service);

    /**
     * @brief Verifies that an appointment can be successfully booked.
     *
     * Ensures:
     * - Controller forwards request to service layer
     * - Returned appointment contains correct data
     * - Service method is called exactly once
     */
    @Test
    void shouldBookAppointment() {

        // Arrange: create request payload
        CreateAppointmentRequest req = new CreateAppointmentRequest();
        req.setDateTime(LocalDateTime.of(2026, 6, 10, 10, 0));
        req.setDuration(30);
        req.setNotes("General checkup");

        // Arrange: mock service response
        Appointment mockAppointment =
                new Appointment(1, req.getDateTime(), req.getDuration(), req.getNotes());

        Mockito.when(service.bookAppointment(Mockito.any(CreateAppointmentRequest.class)))
                .thenReturn(mockAppointment);

        // Act: call controller method
        Appointment result = controller.book(req);

        // Assert: verify response correctness
        assertNotNull(result);
        assertEquals(30, result.getDuration());
        assertEquals("General checkup", result.getNotes());

        // Verify service interaction
        Mockito.verify(service).bookAppointment(Mockito.any(CreateAppointmentRequest.class));
    }

    /**
     * @brief Verifies that all appointments are returned correctly.
     *
     * Ensures:
     * - Service data is correctly forwarded by controller
     * - Returned list size matches expected value
     * - Service method is called once
     */
    @Test
    void shouldReturnAllAppointments() {

        // Arrange: create mock appointment list
        Appointment a1 = new Appointment(1, LocalDateTime.now(), 30, "Visit");

        Mockito.when(service.getAllAppointments())
                .thenReturn(List.of(a1));

        // Act: call controller method
        List<Appointment> result = controller.getAll();

        // Assert: verify correct mapping
        assertEquals(1, result.size());

        // Verify service interaction
        Mockito.verify(service).getAllAppointments();
    }
}