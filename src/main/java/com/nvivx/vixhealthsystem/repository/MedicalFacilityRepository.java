package com.nvivx.vixhealthsystem.repository;

import com.nvivx.vixhealthsystem.model.facility.MedicalFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

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