package com.nvivx.vixhealthsystem.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @brief Request body for scheduling a new appointment (UC05 : Book Appointment).
 *
 * Both {@code patientId} and {@code specialistId} must reference existing entities;
 * the service layer validates slot availability before persisting.
 *
 * @see AppointmentResponse
 */
@Data
public class CreateAppointmentRequest {

    // =========================================================
    // FIELDS
    // =========================================================

    /** Desired start date and time of the appointment; must be in the future. */
    private LocalDateTime dateTime;

    /** Estimated duration of the appointment in minutes. */
    private int duration;

    /** Optional free-text notes or reason for the visit. */
    private String notes;

    /** Identifier of the patient requesting the appointment. */
    private Long patientId;

    /** Identifier of the medical specialist who will conduct the appointment. */
    private Long specialistId;
}