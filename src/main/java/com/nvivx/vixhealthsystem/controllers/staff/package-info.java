/**
 * Controllers for hospital staff — one controller per role, plus shared utilities.
 *
 * Each staff role has its own controller and dashboard template. The
 * {@code AuthController} handles the shared staff login/logout flow (Firebase-backed).
 * {@code EmployeeResourceController} is shared across all roles — any employee
 * can take consumable resources from storage.
 *
 * <ul>
 *   <li>{@code AuthController}              — staff login/logout ({@code /login})</li>
 *   <li>{@code MedicalSpecialistController} — calendar, schedule, appointments, surgeries,
 *                                              prescriptions ({@code /medical-specialist})</li>
 *   <li>{@code SecretaryController}         — patient search, appointment management,
 *                                              room overview ({@code /secretary})</li>
 *   <li>{@code BuyerController}             — inventory management, purchasing ({@code /buyer})</li>
 *   <li>{@code TechnicianController}        — machinery monitoring and maintenance ({@code /technician})</li>
 *   <li>{@code StaffManagerController}      — employee management, shifts, vacations,
 *                                              audit logs ({@code /staff-manager})</li>
 *   <li>{@code EmployeeResourceController}  — resource-take flow shared by all staff
 *                                              ({@code /employee/resources})</li>
 * </ul>
 *
 * Main curator: Lorena Valentina Buitrón Zambrano
 *
 * Note: All team members may contribute to files in this package.
 */
package com.nvivx.vixhealthsystem.controllers.staff;
