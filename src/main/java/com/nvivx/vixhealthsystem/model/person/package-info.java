/**
 * People in the system — the base class and the patient type.
 *
 * {@link com.nvivx.vixhealthsystem.model.person.Person} is an abstract JPA
 * mapped superclass that holds common fields (name, surname, date of birth,
 * contact info) shared by both patients and employees.
 *
 * {@link com.nvivx.vixhealthsystem.model.person.Patient} extends it with
 * patient-specific data: fiscal code, medical record, and Firebase UID for
 * authentication via the SPID/CIE mock flow.
 *
 * Employee subtypes live in the {@code person.employee} sub-package.
 *
 * Main curator: Alexandrina Harti
 *
 * Note: All team members may contribute to files in this package.
 */
package com.nvivx.vixhealthsystem.model.person;
