package com.nvivx.vixhealthsystem.exception;

/**
 * @brief Thrown when a hospital resource (supply, machinery, room) cannot be located.
 *
 * @see GlobalExceptionHandler
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs the exception identifying the missing resource.
     *
     * @param message description that includes the resource type and identifier
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}