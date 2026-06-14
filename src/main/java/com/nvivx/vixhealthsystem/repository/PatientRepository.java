package com.nvivx.vixhealthsystem.repository;

import com.nvivx.vixhealthsystem.model.person.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @brief JPA repository for {@link Patient} entities.
 *
 * Covers patient lookup by fiscal code, which is the primary identifier used
 * during SPID/CIE identity-verification login flows.
 *
 * @see com.nvivx.vixhealthsystem.model.person.Patient
 * @see MedicalRecordRepository
 */
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