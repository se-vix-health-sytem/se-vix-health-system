package com.nvivx.vixhealthsystem.exception;

/**
 * @brief Thrown when a new appointment overlaps with an existing one for the same specialist.
 *
 * Signals a scheduling conflict at the business level, as opposed to
 * {@link SlotNotAvailableException} which indicates the slot was already booked by
 * a different patient.
 *
 * @see GlobalExceptionHandler
 * @see SlotNotAvailableException
 */
public class AppointmentConflictException extends RuntimeException {

    /**
     * Constructs the exception describing the conflicting appointments.
     *
     * @param message human-readable description of the overlap
     */
    public AppointmentConflictException(String message) {
        super(message);
    }
}