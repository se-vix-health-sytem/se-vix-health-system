/**
 * Staff scheduling data — shifts and vacation requests.
 *
 * These entities are stored as JSON (not SQL) because they change frequently
 * and don't require relational joins with the rest of the database. The JSON
 * files live under {@code src/main/resources/storage/}.
 *
 * <ul>
 *   <li>{@code Shift}           — a single work shift assigned to an employee
 *                                 (date, type: MORNING/AFTERNOON/NIGHT, optional notes)</li>
 *   <li>{@code VacationRequest} — a leave request submitted by an employee;
 *                                 approved or denied by the staff manager</li>
 * </ul>
 *
 * Main curator: Navjot Kaur
 *
 * Note: All team members may contribute to files in this package.
 */
package com.nvivx.vixhealthsystem.model.staff;
