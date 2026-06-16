/**
 * Shared enumerations across the domain model.
 *
 * Using enums instead of raw strings prevents invalid status values from
 * ever reaching the database, and gives us clean switch sites in service code.
 * Backward-compatible {@code String} getters are provided on the entities that
 * use these enums so Thymeleaf templates and Jackson serialisation don't break.
 *
 * <ul>
 *   <li>{@code AppointmentStatus} - PENDING, CONFIRMED, CANCELLED, RESCHEDULED</li>
 *   <li>{@code BedStatus}         - FREE / OCCUPIED (computed at runtime, not stored)</li>
 *   <li>{@code EmployeeType}      - discriminator values for the employee hierarchy</li>
 *   <li>{@code MachineStatus}     - operational state of medical machinery</li>
 *   <li>{@code PaymentStatus}     - PAID / UNPAID for appointment billing</li>
 *   <li>{@code Role}              - Spring Security authority names</li>
 *   <li>{@code ShiftType}         - MORNING, AFTERNOON, NIGHT</li>
 *   <li>{@code VacationStatus}    - PENDING, APPROVED, DENIED</li>
 * </ul>
 *
 * Main curator: Alexandrina Harti
 *
 * Note: All team members may contribute to files in this package.
 */
package com.nvivx.vixhealthsystem.model.enums;