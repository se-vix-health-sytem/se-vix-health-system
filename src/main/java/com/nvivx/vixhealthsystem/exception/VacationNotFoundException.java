package com.nvivx.vixhealthsystem.exception;

/**
 * @brief Thrown when a vacation request with a given identifier cannot be found.
 *
 * @see GlobalExceptionHandler
 */
public class VacationNotFoundException extends RuntimeException {

    /**
     * Constructs the exception identifying the missing vacation request.
     *
     * @param message description that includes the requested identifier
     */
    public VacationNotFoundException(String message) {
        super(message);
    }
}