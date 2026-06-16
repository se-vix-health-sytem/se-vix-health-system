/**
 * Data access layer: JPA repositories and JSON-backed stores.
 *
 * The split here is intentional. Entities with relational structure and
 * foreign-key constraints use standard Spring Data {@code JpaRepository} interfaces.
 * Entities that change frequently and don't need relational integrity
 * (appointments, shifts, vacations, audit logs) are stored as flat JSON files
 * and accessed through custom {@code Json...} store classes.
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
 * Main curator: Viviana Fraccaroli
 *
 * Note: All team members may contribute to files in this package.
 */
package com.nvivx.vixhealthsystem.repository;