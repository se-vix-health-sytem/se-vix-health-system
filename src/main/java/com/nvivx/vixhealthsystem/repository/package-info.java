/**
 * Data access layer — Spring Data JPA repositories and JSON-backed stores.
 *
 * Most repositories here are standard Spring Data interfaces that extend
 * {@code JpaRepository}. The JSON-backed ones (prefixed {@code Json…}) manage
 * entities that are stored in flat files rather than the SQL database — this
 * was a design choice for data that changes frequently and doesn't need
 * relational integrity (appointments, shifts, vacation requests, audit logs).
 *
 * JPA repositories:
 * {@code DepartmentRepository}, {@code EmployeeRepository},
 * {@code MachineryRepository}, {@code MedicalConditionRepository},
 * {@code MedicalFacilityRepository}, {@code MedicalRecordRepository},
 * {@code PatientRepository}, {@code PrescriptionRepository},
 * {@code ResourceRepository}, {@code RoomRepository},
 * {@code StorageRepository}, {@code SurgeryRepository}
 *
 * JSON-backed stores:
 * {@code JsonAppointmentRepository}, {@code JsonAuditLogRepository},
 * {@code JsonShiftRepository}, {@code JsonVacationRepository}
 *
 * Main curator: Viviana Fraccarolli
 *
 * Note: All team members may contribute to files in this package.
 */
package com.nvivx.vixhealthsystem.repository;
