package com.nvivx.vixhealthsystem.repository;

import com.nvivx.vixhealthsystem.model.resource.Storage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StorageRepository
        extends JpaRepository<Storage, Long> {

    /**
     * Finds the storage belonging to a medical facility.
     */
    Optional<Storage> findByMedicalFacilityId(Long medicalFacilityId);

    /**
     * Aggregates total quantity per resource across all storages.
     * Returns rows of [resource_id (Long), total_quantity (Long)].
     * Bypasses the @ElementCollection mapping to avoid Hibernate proxy issues.
     */
    @Query(value = "SELECT resource_id, SUM(quantity) FROM StorageResources GROUP BY resource_id",
           nativeQuery = true)
    List<Object[]> findTotalQuantityPerResource();
}