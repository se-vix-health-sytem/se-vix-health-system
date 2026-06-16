/**
 * Shift and vacation services.
 *
 * Both services store data in JSON files ({@code src/main/resources/storage/})
 * rather than the SQL database, to avoid schema migrations for data that
 * changes often and has no foreign-key constraints to protect.
 *
 * {@code ShiftService} creates, updates, and deletes work shifts, and can
 * query by employee or date range. Shift IDs come from an atomic counter so
 * they keep incrementing across server restarts and are never reused.
 *
 * {@code VacationService} manages the request lifecycle: submission by any
 * staff member, then approval or denial exclusively by the staff manager.
 * The service enforces the actor restriction rather than relying on the controller.
 *
 * Main curator: Lorena Valentina Buitrón Zambrano
 *
 * Note: All team members may contribute to files in this package.
 */
package com.nvivx.vixhealthsystem.service.scheduling;