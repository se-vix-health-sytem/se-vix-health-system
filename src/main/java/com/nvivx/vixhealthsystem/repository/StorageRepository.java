package com.nvivx.vixhealthsystem.repository;

import com.nvivx.vixhealthsystem.model.resource.Storage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @brief JPA repository for {@link Storage} entities.
 *
 * Each {@link com.nvivx.vixhealthsystem.model.facility.MedicalFacility} owns at most
 * one storage unit.  The native query {@link #findTotalQuantityPerResource()} bypasses
 * the {@code @ElementCollection} mapping to avoid Hibernate proxy issues when
 * aggregating stock across all storages.
 *
 * @see com.nvivx.vixhealthsystem.model.resource.Storage
 * @see ResourceRepository
 */
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