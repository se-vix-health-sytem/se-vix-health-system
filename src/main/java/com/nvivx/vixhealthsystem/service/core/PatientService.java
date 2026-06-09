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

    public Patient findById(Long id) {
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
    public Patient updatePatient(Long id, Patient updatedData) {
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
    public void deletePatient(Long id) {
        Patient patient = findById(id);

        // Anonymize personal data before deletion (GDPR compliance)
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

        patientRepository.save(patient);
        // Soft delete - we keep the record for legal requirements but anonymized
        auditService.log("DELETE_PATIENT", "Patient", String.valueOf(id), "Patient account anonymized and deleted");
    }
}