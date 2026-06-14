/**
 * Controllers for authenticated patients — base paths {@code /patient}, {@code /mock-spid}.
 *
 * <ul>
 *   <li>{@code PatientAuthController} — login, registration, and logout for patients
 *                                       authenticated via Firebase (SPID/CIE flow)</li>
 *   <li>{@code MockSpidController}    — simulates the Italian SPID identity provider
 *                                       for demo purposes (not production-safe)</li>
 *   <li>{@code PatientAppointmentController} — appointment booking, cancellation,
 *                                              payment initiation, and history view</li>
 * </ul>
 *
 * Main curator: Lorena Valentina Buitrón Zambrano
 *
 * Note: All team members may contribute to files in this package.
 */
package com.nvivx.vixhealthsystem.controllers.patient;
