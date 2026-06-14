package com.nvivx.vixhealthsystem.controllers.services;

import com.nvivx.vixhealthsystem.dto.CreateAppointmentRequest;
import com.nvivx.vixhealthsystem.model.medical.Appointment;
import com.nvivx.vixhealthsystem.service.medical.AppointmentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @brief REST controller for the appointment resource — base URL {@code /appointments}.
 *
 * Provides a minimal JSON API for creating and retrieving appointments
 * programmatically (e.g., from integration tests or future front-end clients).
 * Patient-facing appointment management lives in
 * {@code PatientAppointmentController}; secretary-facing management lives in
 * {@code SecretaryController}.  This controller is intentionally thin and
 * delegates all business logic to {@link AppointmentService}.
 *
 * @see AppointmentService
 * @see com.nvivx.vixhealthsystem.controllers.patient.PatientAppointmentController
 * @see com.nvivx.vixhealthsystem.controllers.staff.SecretaryController
 */
@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService service;

    public AppointmentController(AppointmentService service) {
        this.service = service;
    }

    // =========================================================
    // POST HANDLERS
    // =========================================================

    /**
     * POST /appointments — book a new appointment and return the saved entity.
     *
     * @param req  JSON body deserialised into a {@link CreateAppointmentRequest};
     *             must contain a valid patient ID, specialist ID, and date/time.
     * @return     The persisted {@link Appointment} with its assigned ID.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Appointment book(@RequestBody CreateAppointmentRequest req) {
        return service.bookAppointment(req);
    }

    // =========================================================
    // GET HANDLERS
    // =========================================================

    /**
     * GET /appointments — retrieve all appointments in the system.
     *
     * @return List of every {@link Appointment} currently stored, unsorted.
     */
    @GetMapping
    public List<Appointment> getAll() {
        return service.getAllAppointments();
    }
}
