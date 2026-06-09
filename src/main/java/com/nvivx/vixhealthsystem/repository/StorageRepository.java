package com.nvivx.vixhealthsystem.repository;

import com.nvivx.vixhealthsystem.model.resource.Storage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StorageRepository
        extends JpaRepository<Storage, Long> {

    /**
     * Finds the storage belonging to a medical facility.
     *
     * @param medicalFacilityId facility id
     * @return storage if present
     */
    Optional<Storage> findByMedicalFacilityId(Long medicalFacilityId);
}