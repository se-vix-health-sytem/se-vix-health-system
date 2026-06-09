package com.nvivx.vixhealthsystem.repository;

import com.nvivx.vixhealthsystem.model.facility.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentRepository
        extends JpaRepository<Department, Long> {

    /**
     * Finds departments by name.
     *
     * @param name department name
     * @return matching departments
     */
    List<Department> findByName(String name);

    /**
     * Finds departments belonging to a specific medical facility.
     *
     * @param facilityId medical facility id
     * @return departments of the facility
     */
    List<Department> findByMedicalFacilityId(Long facilityId);
}