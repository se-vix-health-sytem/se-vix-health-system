package com.nvivx.vixhealthsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @brief Slim response payload returned after creating or updating an appointment.
 *
 * Carries only the minimum information the client needs to confirm the operation:
 * the assigned appointment identifier and its current workflow status.
 *
 * @see CreateAppointmentRequest
 */
@Data
@AllArgsConstructor
public class AppointmentResponse {

    // =========================================================
    // FIELDS
    // =========================================================

    /** Auto-assigned appointment identifier. */
    private int id;

    /**
     * Current workflow status of the appointment
     * (e.g. {@code "SCHEDULED"}, {@code "CANCELLED"}, {@code "COMPLETED"}).
     */
    private String status;
}