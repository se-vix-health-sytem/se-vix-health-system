package com.nvivx.vixhealthsystem.service.core;

import com.nvivx.vixhealthsystem.model.person.Patient;
import com.nvivx.vixhealthsystem.repository.PatientRepository;
import com.nvivx.vixhealthsystem.service.AuditService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    /**
     * Search patients by name, surname, or fiscal code.
     * Uses Java streams to filter since repository doesn't have a custom query method.
     */
    public List<Patient> searchPatients(String query) {
        if (query == null || query.trim().isEmpty()) {
            return findAllPatients();
        }

        String searchLower = query.toLowerCase().trim();

        return patientRepository.findAll().stream()
                .filter(patient ->
                        (patient.getName() != null && patient.getName().toLowerCase().contains(searchLower)) ||
                                (patient.getSurname() != null && patient.getSurname().toLowerCase().contains(searchLower)) ||
                                (patient.getFiscalCode() != null && patient.getFiscalCode().toLowerCase().contains(searchLower))
                )
                .collect(Collectors.toList());
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
        if (updatedData.getBirthDate() != null) patient.setBirthDate(updatedData.getBirthDate());
        if (updatedData.getBirthPlace() != null) patient.setBirthPlace(updatedData.getBirthPlace());
        if (updatedData.getGender() != '\0') patient.setGender(updatedData.getGender());

        Patient saved = patientRepository.save(patient);
        auditService.log("UPDATE_PATIENT", "Patient", String.valueOf(id), "Updated patient details");
        return saved;
    }

    /**
     * Deletes (anonymizes) a patient account while preserving medical records.
     *
     * GDPR Compliance (NFR04):
     * - Personal data (name, surname, email, phone) is removed
     * - Fiscal code is replaced with a unique DELETED_ timestamp
     * - Medical records (conditions, prescriptions, surgeries) are preserved
     *   for clinical and legal requirements (NFR09 - 5-year retention)
     */
    @Transactional
    public void deletePatient(long id) {
        Patient patient = findById(id);

        // Store original name for audit log
        String originalName = patient.getName() + " " + patient.getSurname();

        // Anonymize personal data (GDPR compliance)
        patient.setName("ANONYMIZED");
        patient.setSurname("ANONYMIZED");
        patient.setEmail(null);
        patient.setPhoneNumber(null);
        patient.setFiscalCode("DELETED_" + System.currentTimeMillis());

        // Note: birthDate, birthPlace, gender are kept as they are
        // not considered direct identifiers when name is removed

        // Save the anonymized patient record
        Patient saved = patientRepository.save(patient);

        // Log the deletion (anonymization)
        auditService.log("DELETE_PATIENT", "Patient", String.valueOf(id),
                "Patient account anonymized. Original: " + originalName);

        // Note: Medical records remain in the database with patient_id = id
        // but the patient is no longer identifiable. This satisfies:
        // - NFR04 (GDPR) - personal data is removed
        // - NFR09 (Medical Data Norms) - clinical records retained for 5 years
    }

    /**
     * Permanently deletes a patient and ALL associated medical records.
     * Use with caution - this is for testing or when legally required.
     * Normally, deletePatient() should be used instead.
     */
    @Transactional
    public void permanentDeletePatient(long id) {
        Patient patient = findById(id);
        String patientInfo = patient.getName() + " " + patient.getSurname();

        patientRepository.delete(patient);

        auditService.log("PERMANENT_DELETE_PATIENT", "Patient", String.valueOf(id),
                "Permanently deleted patient: " + patientInfo + " and ALL medical records");
    }
}