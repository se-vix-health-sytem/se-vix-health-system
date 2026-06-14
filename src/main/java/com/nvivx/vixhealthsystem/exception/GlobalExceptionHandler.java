package com.nvivx.vixhealthsystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @brief Centralizes exception-to-HTTP-response mapping for all REST controllers.
 *
 * Each {@code @ExceptionHandler} method translates a domain exception into an
 * appropriate HTTP status code so that controllers remain free of error-handling
 * boilerplate.  Add new handlers here when introducing new domain exceptions.
 *
 * @see SlotNotAvailableException
 * @see AppointmentConflictException
 * @see PatientNotFoundException
 * @see ResourceNotFoundException
 * @see UnauthorizedAccessException
 * @see VacationNotFoundException
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // =========================================================
    // APPOINTMENT HANDLERS
    // =========================================================

    /**
     * Maps {@link SlotNotAvailableException} to HTTP 409 Conflict.
     *
     * Triggered when the requested appointment slot is already occupied by
     * another appointment.
     *
     * @param ex the exception carrying the conflict description
     * @return 409 response with the exception message as body
     */
    @ExceptionHandler(SlotNotAvailableException.class)
    public ResponseEntity<String> handleSlot(SlotNotAvailableException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }
}