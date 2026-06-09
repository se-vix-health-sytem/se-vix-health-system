package com.nvivx.vixhealthsystem.repository;

import com.nvivx.vixhealthsystem.model.medical.MedicalCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

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