package com.nvivx.vixhealthsystem.repository;

import com.nvivx.vixhealthsystem.model.person.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository
        extends JpaRepository<Patient, Long> {

    /**
     * Finds a patient by fiscal code.
     *
     * @param fiscalCode patient's fiscal code
     * @return patient if found
     */
    Optional<Patient> findByFiscalCode(String fiscalCode);
}