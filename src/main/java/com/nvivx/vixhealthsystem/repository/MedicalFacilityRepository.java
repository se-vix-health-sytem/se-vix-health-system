package com.nvivx.vixhealthsystem.repository;

import com.nvivx.vixhealthsystem.model.facility.MedicalFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @brief JPA repository for {@link MedicalFacility} entities.
 *
 * Used by administration and public-facing pages that list or look up facilities
 * (clinics, hospitals) hosted within the VIX Health network.
 *
 * @see com.nvivx.vixhealthsystem.model.facility.MedicalFacility
 * @see DepartmentRepository
 * @see RoomRepository
 */
@Repository
public interface MedicalFacilityRepository
        extends JpaRepository<MedicalFacility, Long> {

    /**
     * Finds facilities by name.
     *
     * @param name facility name
     * @return matching facilities
     */
    List<MedicalFacility> findByName(String name);
}