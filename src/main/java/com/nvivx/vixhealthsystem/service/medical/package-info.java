/**
 * Clinical services: appointment scheduling and medical record management.
 *
 * {@code AppointmentService} owns the JSON appointment store, checks slot
 * availability before booking, and handles cancellations and reschedules.
 *
 * {@code MedicalRecordService} creates patient records and appends diagnoses,
 * prescriptions, and exam results via the domain model's aggregate methods.
 * Every write is audit-logged for traceability (NFR02), so don't bypass this
 * service to write to the record repository directly.
 *
 * Main curator: Lorena Valentina Buitrón Zambrano
 *
 * Note: All team members may contribute to files in this package.
 */
package com.nvivx.vixhealthsystem.service.medical;