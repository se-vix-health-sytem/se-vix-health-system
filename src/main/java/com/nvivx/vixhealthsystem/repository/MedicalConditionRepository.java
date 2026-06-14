package com.nvivx.vixhealthsystem.repository;

import com.nvivx.vixhealthsystem.model.medical.MedicalCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @brief JPA repository for {@link MedicalCondition} entities.
 *
 * Used by the medical-record service to read and attach conditions
 * (diagnoses, chronic illnesses, allergies) to a patient's record.
 *
 * @see com.nvivx.vixhealthsystem.model.medical.MedicalCondition
 * @see MedicalRecordRepository
 */
@Repository
public interface MedicalConditionRepository
        extends JpaRepository<MedicalCondition, Long> {

    /**
     * Finds all conditions with the specified name.
     *
     * @param name condition name
     * @return matching conditions
     */
    List<MedicalCondition> findByName(String name);

    /**
     * Finds all conditions belonging to a medical record.
     *
     * @param medicalRecordId medical record id
     * @return conditions associated with the medical record
     */
    List<MedicalCondition> findByMedicalRecordId(Long medicalRecordId);

    /**
     * Finds all conditions of a specific type.
     *
     * @param type condition type
     * @return matching conditions
     */
    List<MedicalCondition> findByType(String type);
}