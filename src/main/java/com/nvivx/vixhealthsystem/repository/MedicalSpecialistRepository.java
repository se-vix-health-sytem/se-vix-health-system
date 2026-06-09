package com.nvivx.vixhealthsystem.repository;

import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalSpecialistRepository extends JpaRepository<MedicalSpecialist, Long> {

    List<MedicalSpecialist> findBySpecialty(String specialty);

    MedicalSpecialist findByLicenseNumber(String licenseNumber);
}