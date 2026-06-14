/**
 * Staff hierarchy — all employee types in the hospital.
 *
 * Uses JPA Single Table Inheritance (discriminator column: {@code type}).
 * {@link com.nvivx.vixhealthsystem.model.person.employee.Employee} is the
 * abstract parent; concrete subtypes are:
 *
 * <ul>
 *   <li>{@code MedicalSpecialist} — doctors and surgeons; can issue prescriptions
 *                                    and schedule surgeries</li>
 *   <li>{@code Secretary}         — manages appointments and patient search</li>
 *   <li>{@code Buyer}             — handles inventory purchasing</li>
 *   <li>{@code Technician}        — monitors and maintains medical machinery</li>
 *   <li>{@code StaffManager}      — oversees employees, shifts, and vacation requests</li>
 * </ul>
 *
 * Domain methods like {@code takeResource()} and {@code addResource()} are on
 * {@code Employee} and {@code Buyer} respectively — they navigate their own
 * associations to reach storage, rather than accepting injected objects. This
 * keeps domain logic inside the model where it belongs.
 *
 * Main curator: Lorena Valentina Buitrón Zambrano
 *
 * Note: All team members may contribute to files in this package.
 */
package com.nvivx.vixhealthsystem.model.person.employee;
