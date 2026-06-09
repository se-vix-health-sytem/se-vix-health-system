package com.nvivx.vixhealthsystem.repository;

import com.nvivx.vixhealthsystem.model.medical.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescriptionRepository
        extends JpaRepository<Prescription, Long> {

    /**
     * Returns all prescriptions belonging to a medical record.
     *
     * @param medicalRecordId medical record id
     * @return prescriptions of the medical record
     */
    List<Prescription> findByMedicalRecordId(Long medicalRecordId);

    /**
     * Returns all prescriptions issued by a medical specialist.
     *
     * @param medicalSpecialistId specialist id
     * @return prescriptions issued by the specialist
     */
    List<Prescription> findByMedicalSpecialistId(Long medicalSpecialistId);

    /**
     * Returns all prescriptions for a specific medication.
     *
     * @param medication medication name
     * @return matching prescriptions
     */
    List<Prescription> findByMedication(String medication);
}