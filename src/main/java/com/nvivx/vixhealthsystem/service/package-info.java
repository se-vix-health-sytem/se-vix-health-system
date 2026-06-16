/**
 * Business logic layer, grouped by domain area.
 *
 * Services are the only layer that should write to repositories directly.
 * Controllers call services; services call repositories and each other where needed.
 * Most service classes are annotated {@code @Transactional(readOnly=true)} at the
 * class level, with {@code @Transactional} overrides on individual write methods.
 *
 * Sub-packages:
 * <ul>
 *   <li>{@code core}        - employee, patient, and department management</li>
 *   <li>{@code medical}     - appointments and medical records</li>
 *   <li>{@code resources}   - inventory, machinery, room availability</li>
 *   <li>{@code scheduling}  - shifts and vacation requests</li>
 *   <li>{@code integration} - Firebase, payment simulation, notifications</li>
 * </ul>
 *
 * Root-level utilities:
 * {@code AuditService} writes immutable log entries (NFR02);
 * {@code DevCredentialStore} is a DEV-ONLY in-memory credential cache.
 *
 * Main curator: Lorena Valentina Buitrón Zambrano
 *
 * Note: All team members may contribute to files in this package.
 */
package com.nvivx.vixhealthsystem.service;