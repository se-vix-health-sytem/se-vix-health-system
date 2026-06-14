package com.nvivx.vixhealthsystem.exception;

/**
 * @brief Thrown when a patient lookup by ID or fiscal code yields no result.
 *
 * @see GlobalExceptionHandler
 */
public class PatientNotFoundException extends RuntimeException {

    /**
     * Constructs the exception identifying the missing patient.
     *
     * @param message description that includes the patient identifier or fiscal code
     */
    public PatientNotFoundException(String message) {
        super(message);
    }
}