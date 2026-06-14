/**
 * Scheduling services — shifts and vacation requests.
 *
 * Both services back their data with JSON files under
 * {@code src/main/resources/storage/} rather than the SQL database.
 * This avoids schema migrations for data that changes constantly and
 * has no foreign-key dependencies on other tables.
 *
 * <ul>
 *   <li>{@code ShiftService}    — creates, updates, and deletes work shifts;
 *                                 queries shifts by employee or date range</li>
 *   <li>{@code VacationService} — handles vacation request submission, approval,
 *                                 and denial by the staff manager</li>
 * </ul>
 *
 * Main curator: Lorena Valentina Buitrón Zambrano
 *
 * Note: All team members may contribute to files in this package.
 */
package com.nvivx.vixhealthsystem.service.scheduling;
