/**
 * Patient-facing controllers: authentication, appointment management, and profile.
 *
 * The patient side uses a mock SPID/CIE identity-provider flow for login rather
 * than a username/password form. {@code MockSpidController} simulates that provider
 * locally and is not safe for production use.
 *
 * {@code PatientAuthController} handles the full session lifecycle (login, dashboard,
 * profile edits, account deletion), while {@code PatientAppointmentController} covers
 * booking, rescheduling, cancellation, and payment initiation.
 *
 * Base paths: {@code /patient}, {@code /mock-spid}, {@code /mock-cie}
 *
 * Main curator: Lorena Valentina Buitrón Zambrano
 *
 * Note: All team members may contribute to files in this package.
 */
package com.nvivx.vixhealthsystem.controllers.patient;