/**
 * Clinical domain — everything that happens to a patient medically.
 *
 * <ul>
 *   <li>{@code Appointment}     — a scheduled visit between a patient and a specialist</li>
 *   <li>{@code MedicalRecord}   — the patient's full clinical history (owns prescriptions,
 *                                 surgeries, and conditions)</li>
 *   <li>{@code MedicalCondition}— a diagnosis or exam result attached to a record</li>
 *   <li>{@code Prescription}    — a medication order issued by a specialist</li>
 *   <li>{@code Surgery}         — a surgical procedure linked to both a patient and a specialist</li>
 * </ul>
 *
 * {@code MedicalRecord} is the aggregate root for clinical data. Adding a prescription
 * or surgery should always go through its domain methods ({@code addPrescription},
 * {@code addSurgery}) so that bidirectional associations stay consistent.
 *
 * Main curator: Navjot Kaur
 *
 * Note: All team members may contribute to files in this package.
 */
package com.nvivx.vixhealthsystem.model.medical;
