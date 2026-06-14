/**
 * Shared enumerations used across the domain model.
 *
 * Using enums instead of raw strings gives us compile-time safety and makes
 * it impossible to accidentally store an invalid status value in the database.
 *
 * <ul>
 *   <li>{@code AppointmentStatus} — PENDING, CONFIRMED, CANCELLED, RESCHEDULED</li>
 *   <li>{@code BedStatus}         — FREE / OCCUPIED (computed, not stored)</li>
 *   <li>{@code EmployeeType}      — discriminator values for the employee hierarchy</li>
 *   <li>{@code MachineStatus}     — operational state of medical machinery</li>
 *   <li>{@code PaymentStatus}     — PAID / UNPAID for appointment billing</li>
 *   <li>{@code Role}              — Spring Security authority names</li>
 *   <li>{@code ShiftType}         — MORNING, AFTERNOON, NIGHT</li>
 *   <li>{@code VacationStatus}    — PENDING, APPROVED, DENIED</li>
 * </ul>
 *
 * Backward-compatible getters (returning {@code String}) are provided on the
 * entities that use these enums so that Thymeleaf templates and Jackson
 * serialisation don't break on existing data.
 *
 * Main curator: Alexandrina Harti
 *
 * Note: All team members may contribute to files in this package.
 */
package com.nvivx.vixhealthsystem.model.enums;
