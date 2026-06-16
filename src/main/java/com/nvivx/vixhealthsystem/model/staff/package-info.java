/**
 * Shift and vacation data for hospital staff.
 *
 * These entities are backed by JSON files under
 * {@code src/main/resources/storage/} rather than the SQL database.
 * The rationale: shifts and vacations change constantly, have no foreign-key
 * constraints to protect, and would generate schema migrations for little
 * relational benefit.
 *
 * {@code Shift} represents a single work period (date + MORNING/AFTERNOON/NIGHT).
 * {@code VacationRequest} is submitted by any staff member and approved or denied
 * by the staff manager; once approved it should block shift assignments for the
 * covered date range.
 *
 * Main curator: Navjot Kaur
 *
 * Note: All team members may contribute to files in this package.
 */
package com.nvivx.vixhealthsystem.model.staff;