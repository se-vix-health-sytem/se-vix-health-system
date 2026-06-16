/**
 * Typed business exceptions for the VIX domain.
 *
 * Each exception maps to a concrete failure condition so that service code can
 * signal exactly what went wrong, and {@code GlobalExceptionHandler} can turn it
 * into the right HTTP response without inspecting message strings.
 *
 * <ul>
 *   <li>{@code AppointmentConflictException}  - time slot is already taken</li>
 *   <li>{@code SlotNotAvailableException}     - requested slot doesn't exist or is closed</li>
 *   <li>{@code PatientNotFoundException}      - patient ID not found in the database</li>
 *   <li>{@code ResourceNotFoundException}     - inventory resource not found</li>
 *   <li>{@code VacationNotFoundException}     - vacation request ID doesn't exist</li>
 *   <li>{@code UnauthorizedAccessException}   - caller lacks the required role</li>
 * </ul>
 *
 * Main curator: Navjot Kaur
 *
 * Note: All team members may contribute to files in this package.
 */
package com.nvivx.vixhealthsystem.exception;