package com.nvivx.vixhealthsystem.repository;

import com.nvivx.vixhealthsystem.model.medical.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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