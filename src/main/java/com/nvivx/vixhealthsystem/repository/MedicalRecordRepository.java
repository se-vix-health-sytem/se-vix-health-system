package com.nvivx.vixhealthsystem.repository;

import com.nvivx.vixhealthsystem.model.medical.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @brief JPA repository for {@link MedicalRecord} entities.
 *
 * Each patient has exactly one medical record; the one-to-one relationship is
 * enforced at the database level and queries here reflect that invariant.
 *
 * @see com.nvivx.vixhealthsystem.model.medical.MedicalRecord
 * @see PatientRepository
 */
@Repository
public interface MedicalRecordRepository
        extends JpaRepository<MedicalRecord, Long> {

    /**
     * Finds the medical record belonging to a patient.
     *
     * @param patientId patient id
     * @return medical record if present
     */
    Optional<MedicalRecord> findByPatientId(Long patientId);
}