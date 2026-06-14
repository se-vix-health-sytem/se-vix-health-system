/**
 * Clinical services — appointments and medical records.
 *
 * <ul>
 *   <li>{@code AppointmentService}    — reads and writes the JSON appointment store;
 *                                       checks slot availability and handles cancellations</li>
 *   <li>{@code MedicalRecordService}  — creates patient records and appends diagnoses,
 *                                       prescriptions, and exam results; all writes are
 *                                       audit-logged for traceability (NFR02)</li>
 * </ul>
 *
 * Main curator: Lorena Valentina Buitrón Zambrano
 *
 * Note: All team members may contribute to files in this package.
 */
package com.nvivx.vixhealthsystem.service.medical;
