/**
 * Domain model — the core of the application.
 *
 * All JPA entities live here, organised by area:
 * <ul>
 *   <li>{@code enums}           — shared status and type enumerations</li>
 *   <li>{@code facility}        — hospitals, departments, rooms, and storage</li>
 *   <li>{@code medical}         — appointments, prescriptions, surgeries, medical records</li>
 *   <li>{@code person}          — patients and the base {@code Person} class</li>
 *   <li>{@code person.employee} — all staff subtypes (specialist, secretary, buyer, etc.)</li>
 *   <li>{@code resource}        — consumable resources and machinery</li>
 *   <li>{@code staff}           — shifts and vacation requests</li>
 * </ul>
 *
 * The project follows a Rich Domain Model approach — business rules and
 * state-changing operations are methods on the entity classes, not in the
 * service layer. Keep that in mind before moving logic out of the model.
 *
 * {@link com.nvivx.vixhealthsystem.model.AuditLog} also lives here (root level)
 * since it is used system-wide.
 */
package com.nvivx.vixhealthsystem.model;
