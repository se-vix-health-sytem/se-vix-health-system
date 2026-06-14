/**
 * Custom exceptions and the global error handler.
 *
 * Every exception here maps to a specific business condition — they exist so
 * that service code can throw something meaningful rather than a generic
 * {@code RuntimeException}, and so that {@code GlobalExceptionHandler} can
 * map each one to the right HTTP response or error page.
 *
 * Domain exceptions:
 * <ul>
 *   <li>{@code AppointmentConflictException}  — time slot is already taken</li>
 *   <li>{@code SlotNotAvailableException}     — requested slot doesn't exist or is closed</li>
 *   <li>{@code PatientNotFoundException}      — patient ID not found in the database</li>
 *   <li>{@code ResourceNotFoundException}     — inventory resource not found</li>
 *   <li>{@code VacationNotFoundException}     — vacation request ID doesn't exist</li>
 *   <li>{@code UnauthorizedAccessException}   — caller lacks permission for the operation</li>
 * </ul>
 *
 * Main curator: Navjot Kaur
 *
 * Note: All team members may contribute to files in this package.
 */
package com.nvivx.vixhealthsystem.exception;
