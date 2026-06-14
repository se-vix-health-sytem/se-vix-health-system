package com.nvivx.vixhealthsystem.exception;

/**
 * @brief Thrown when a requested appointment time slot is already occupied.
 *
 * Caught by {@link GlobalExceptionHandler} and translated to HTTP 409 Conflict.
 */
public class SlotNotAvailableException extends RuntimeException {

    /**
     * Constructs the exception with a description of the unavailable slot.
     *
     * @param message human-readable explanation, included verbatim in the HTTP response body
     */
    public SlotNotAvailableException(String message) {
        super(message);
    }
}