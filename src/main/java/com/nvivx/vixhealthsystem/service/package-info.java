/**
 * Business logic layer — all services, grouped by domain area.
 *
 * Sub-packages:
 * <ul>
 *   <li>{@code core}        — employee, patient, and department management</li>
 *   <li>{@code medical}     — appointments and medical records</li>
 *   <li>{@code resources}   — inventory, machinery, room availability</li>
 *   <li>{@code scheduling}  — shifts and vacation requests</li>
 *   <li>{@code integration} — Firebase authentication, payment simulation,
 *                             notifications, questionnaire</li>
 * </ul>
 *
 * Root-level classes:
 * <ul>
 *   <li>{@code AuditService}      — writes immutable audit log entries (NFR02)</li>
 *   <li>{@code DevCredentialStore}— DEV-ONLY in-memory store for demo login credentials</li>
 * </ul>
 *
 * Most services are annotated {@code @Transactional(readOnly=true)} at the class level
 * and override with {@code @Transactional} on individual write methods.
 *
 * Main curator: Lorena Valentina Buitrón Zambrano
 *
 * Note: All team members may contribute to files in this package.
 */
package com.nvivx.vixhealthsystem.service;
