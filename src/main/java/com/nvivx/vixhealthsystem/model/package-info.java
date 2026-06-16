/**
 * Domain model: JPA entities and enums that form the core of VIX.
 *
 * The model follows a Rich Domain approach, meaning business rules live on
 * the entity classes themselves rather than in the service layer. Before moving
 * logic out of a model class, check whether it belongs there by design.
 *
 * Entities are organised into sub-packages by area:
 * <ul>
 *   <li>{@code enums}           - shared status and type enumerations</li>
 *   <li>{@code facility}        - hospitals, departments, rooms, and storage</li>
 *   <li>{@code medical}         - appointments, prescriptions, surgeries, records</li>
 *   <li>{@code person}          - patients and the base {@code Person} class</li>
 *   <li>{@code person.employee} - all staff subtypes</li>
 *   <li>{@code resource}        - consumable resources and machinery</li>
 *   <li>{@code staff}           - shifts and vacation requests</li>
 * </ul>
 *
 * {@link com.nvivx.vixhealthsystem.model.AuditLog} lives at the root level
 * since it is used system-wide rather than belonging to any one subdomain.
 */
package com.nvivx.vixhealthsystem.model;