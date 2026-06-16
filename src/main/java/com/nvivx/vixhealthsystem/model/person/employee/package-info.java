/**
 * The employee type hierarchy, implemented with JPA Single Table Inheritance.
 *
 * All subtypes share one database table (discriminator column: {@code type}).
 * {@link com.nvivx.vixhealthsystem.model.person.employee.Employee} is the abstract
 * base; the concrete types are {@code MedicalSpecialist}, {@code Secretary},
 * {@code Buyer}, {@code Technician}, and {@code StaffManager}.
 *
 * Domain methods such as {@code takeResource()} and {@code addResource()} live
 * on {@code Employee} and {@code Buyer} respectively. They navigate the object
 * graph to reach storage rather than accepting injected repositories, keeping
 * business rules inside the model where they belong.
 *
 * Main curator: Lorena Valentina Buitrón Zambrano
 *
 * Note: All team members may contribute to files in this package.
 */
package com.nvivx.vixhealthsystem.model.person.employee;