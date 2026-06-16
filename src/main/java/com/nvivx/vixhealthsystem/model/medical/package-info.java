/**
 * Clinical domain: everything that happens to a patient medically.
 *
 * {@code MedicalRecord} is the aggregate root here. Prescriptions, surgeries,
 * and conditions should always be added through its domain methods
 * ({@code addPrescription}, {@code addSurgery}) to keep bidirectional
 * associations consistent. Don't set the collections directly.
 *
 * The other classes in this package are owned by the record:
 * {@code Appointment} connects a patient to a specialist at a given time;
 * {@code MedicalCondition} stores diagnoses and exam results;
 * {@code Prescription} holds medication orders; {@code Surgery} ties a
 * procedure to both the patient and the operating specialist.
 *
 * Main curator: Navjot Kaur
 *
 * Note: All team members may contribute to files in this package.
 */
package com.nvivx.vixhealthsystem.model.medical;