package com.nvivx.vixhealthsystem.exception;

/**
 * @brief Thrown when a user attempts an action they are not authorised to perform.
 *
 * Distinct from authentication failure : the user is known but lacks the required role
 * or ownership of the targeted resource.
 *
 * @see GlobalExceptionHandler
 * @see com.nvivx.vixhealthsystem.config.SecurityConfig
 */
public class UnauthorizedAccessException extends RuntimeException {

    /**
     * Constructs the exception explaining why access was denied.
     *
     * @param message description of the denied action and the required role or condition
     */
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}