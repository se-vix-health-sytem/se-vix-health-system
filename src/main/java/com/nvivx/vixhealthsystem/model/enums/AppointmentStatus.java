package com.nvivx.vixhealthsystem.model.enums;

/**
 * @brief Lifecycle state of an {@link com.nvivx.vixhealthsystem.model.medical.Appointment}.
 *
 * Transitions flow roughly as: {@code PENDING} → {@code CONFIRMED} → {@code COMPLETED},
 * with {@code CANCELLED} or {@code RESCHEDULED} as possible side exits.
 * Business rules for allowed transitions are enforced in {@link com.nvivx.vixhealthsystem.model.medical.Appointment}.
 */
public enum AppointmentStatus {
    /** Appointment has been submitted but not yet reviewed or confirmed. */
    PENDING,
    /** Appointment has been confirmed and is on the schedule. */
    CONFIRMED,
    /** Appointment was called off before it took place. */
    CANCELLED,
    /** Appointment has taken place and the visit is closed. */
    COMPLETED,
    /** Appointment was moved to a new date and time. */
    RESCHEDULED
}
