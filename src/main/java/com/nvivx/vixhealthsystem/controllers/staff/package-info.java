/**
 * Staff-side controllers, one per role plus two shared classes.
 *
 * All routes here sit behind role-specific Spring Security rules.
 * {@code AuthController} is the entry point for staff login (Firebase-backed)
 * and is the only controller in this package that doesn't require an
 * already-authenticated session. {@code EmployeeResourceController} is shared
 * across all roles since any employee can take consumables from storage.
 *
 * <ul>
 *   <li>{@code AuthController}              - staff login/logout ({@code /login})</li>
 *   <li>{@code MedicalSpecialistController} - calendar, schedule, appointments,
 *                                             surgeries, prescriptions</li>
 *   <li>{@code SecretaryController}         - patient search, appointment management,
 *                                             room overview</li>
 *   <li>{@code BuyerController}             - inventory management and purchasing</li>
 *   <li>{@code TechnicianController}        - machinery monitoring and maintenance</li>
 *   <li>{@code StaffManagerController}      - employee management, shifts, vacations,
 *                                             and audit logs</li>
 *   <li>{@code EmployeeResourceController}  - resource-take flow, shared by all staff</li>
 * </ul>
 *
 * Main curator: Lorena Valentina Buitrón Zambrano
 *
 * Note: All team members may contribute to files in this package.
 */
package com.nvivx.vixhealthsystem.controllers.staff;