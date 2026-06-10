package com.nvivx.vixhealthsystem.service.core;

import com.nvivx.vixhealthsystem.model.person.Patient;
import com.nvivx.vixhealthsystem.repository.PatientRepository;
import com.nvivx.vixhealthsystem.service.AuditService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PatientService {

    private final PatientRepository patientRepository;
    private final AuditService auditService;

    public PatientService(PatientRepository patientRepository, AuditService auditService) {
        this.patientRepository = patientRepository;
        this.auditService = auditService;
    }

    public Patient findById(long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));
    }

    public Optional<Patient> findByFiscalCode(String fiscalCode) {
        return patientRepository.findByFiscalCode(fiscalCode);
    }

    public List<Patient> findAllPatients() {
        return patientRepository.findAll();
    }

    public List<Patient> searchPatients(String query) {
        String searchPattern = "%" + query.toLowerCase() + "%";
        return patientRepository.searchByNameOrSurnameOrFiscalCode(searchPattern);
    }

    @Transactional
    public Patient createPatient(Patient patient) {
        Patient saved = patientRepository.save(patient);
        auditService.log("CREATE_PATIENT", "Patient", String.valueOf(saved.getId()),
                "Created patient: " + patient.getName() + " " + patient.getSurname());
        return saved;
    }

    @Transactional
    public Patient updatePatient(long id, Patient updatedData) {
        Patient patient = findById(id);

        if (updatedData.getName() != null) patient.setName(updatedData.getName());
        if (updatedData.getSurname() != null) patient.setSurname(updatedData.getSurname());
        if (updatedData.getEmail() != null) patient.setEmail(updatedData.getEmail());
        if (updatedData.getPhoneNumber() != null) patient.setPhoneNumber(updatedData.getPhoneNumber());
        if (updatedData.getFiscalCode() != null) patient.setFiscalCode(updatedData.getFiscalCode());

        Patient saved = patientRepository.save(patient);
        auditService.log("UPDATE_PATIENT", "Patient", String.valueOf(id), "Updated patient details");
        return saved;
    }

    @Transactional
    public void deletePatient(long id) {
        Patient patient = findById(id);

        // Store original data for audit
        String originalName = patient.getName();
        String originalSurname = patient.getSurname();

        // Anonymize personal data before deletion (GDPR compliance - NFR04)
        patient.setName("ANONYMIZED");
        patient.setSurname("ANONYMIZED");
        patient.setEmail(null);
        patient.setPhoneNumber(null);
        patient.setFiscalCode("DELETED_" + System.currentTimeMillis());

        // Remove associations
        if (patient.getMedicalRecord() != null) {
            patient.getMedicalRecord().setPatient(null);
        }
        patient.getAppointments().clear();

        // Save the anonymized version
        Patient saved = patientRepository.save(patient);

        // Log the deletion (anonymization)
        auditService.log("DELETE_PATIENT", "Patient", String.valueOf(id),
                "Patient account anonymized and deleted. Original: " + originalName + " " + originalSurname);

        // Note: We keep the record for legal requirements (NFR09 - 5-year retention)
        // but it's anonymized so no personal data remains
    }
}