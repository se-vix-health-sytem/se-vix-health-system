package com.nvivx.vixhealthsystem.service.core;

import com.nvivx.vixhealthsystem.model.person.Patient;
import com.nvivx.vixhealthsystem.repository.PatientRepository;
import com.nvivx.vixhealthsystem.service.AuditService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @brief Manages the patient lifecycle : registration, lookup, partial updates, and
 *        GDPR-compliant account deletion.
 *
 * Annotated {@code @Transactional(readOnly=true)} at the class level so that all read
 * operations benefit from connection-pool optimisations; write methods override with
 * {@code @Transactional}.
 *
 * Deletion follows a two-tier model: {@link #deletePatient(long)} anonymises personal
 * data while preserving clinical records (NFR04 + NFR09), whereas
 * {@link #permanentDeletePatient(long)} cascades all data and should only be used
 * for legal erasure requests.
 *
 * @see com.nvivx.vixhealthsystem.model.person.Patient
 * @see MedicalRecordService
 * @see AuditService
 */
@Service
@Transactional(readOnly = true)
public class PatientService {

    // =========================================================
    // FIELDS
    // =========================================================

    private final PatientRepository patientRepository;
    private final AuditService auditService;

    // =========================================================
    // CONSTRUCTORS
    // =========================================================

    /**
     * Constructs the service with its required collaborators.
     *
     * @param patientRepository  Persistence layer for {@link Patient} entities.
     * @param auditService       Records every mutating action (NFR02 : traceability).
     */
    public PatientService(PatientRepository patientRepository, AuditService auditService) {
        this.patientRepository = patientRepository;
        this.auditService = auditService;
    }

    // =========================================================
    // READ OPERATIONS
    // =========================================================

    /**
     * Looks up a patient by primary key, throwing when absent.
     *
     * @param id  Patient primary key.
     * @return    The matching {@link Patient}; never {@code null}.
     * @throws RuntimeException When no patient with the given ID exists.
     */
    public Patient findById(long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));
    }

    /**
     * Looks up a patient by their national fiscal code.
     *
     * @param fiscalCode  The patient's unique fiscal/tax code.
     * @return            {@link Optional} containing the patient, or empty if absent.
     */
    public Optional<Patient> findByFiscalCode(String fiscalCode) {
        return patientRepository.findByFiscalCode(fiscalCode);
    }

    /** @brief Returns all patients in the system. */
    public List<Patient> findAllPatients() {
        return patientRepository.findAll();
    }

    /**
     * Searches patients by name, surname, or fiscal code using case-insensitive
     *        substring matching.
     *
     * Filtering is performed in-memory because the repository does not yet expose a
     * compound search query.  A blank query returns all patients.
     *
     * @param query  Search term; {@code null} or blank returns all patients.
     * @return       Non-null list of matching patients.
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

    // =========================================================
    // WRITE OPERATIONS
    // =========================================================

    /**
     * Persists a new patient and writes an audit entry.
     *
     * @param patient  Transient patient entity to persist.
     * @return         The saved {@link Patient} with a generated ID.
     */
    @Transactional
    public Patient createPatient(Patient patient) {
        Patient saved = patientRepository.save(patient);
        auditService.log("CREATE_PATIENT", "Patient", String.valueOf(saved.getId()),
                "Created patient: " + patient.getName() + " " + patient.getSurname());
        return saved;
    }

    /**
     * Applies a partial update to an existing patient, ignoring {@code null} fields.
     *
     * @param id           ID of the patient to update.
     * @param updatedData  Carrier object with the fields to overwrite; {@code null} fields are skipped.
     * @return             The updated and re-persisted {@link Patient}.
     * @throws RuntimeException When no patient with {@code id} exists.
     */
    @Transactional
    public Patient updatePatient(long id, Patient updatedData) {
        Patient patient = findById(id);
        patient.updateProfile(
                updatedData.getName(),
                updatedData.getSurname(),
                updatedData.getEmail(),
                updatedData.getPhoneNumber(),
                updatedData.getBirthPlace(),
                updatedData.getBirthDate(),
                updatedData.getGender()
        );
        Patient saved = patientRepository.save(patient);
        auditService.log("UPDATE_PATIENT", "Patient", String.valueOf(id), "Updated patient details");
        return saved;
    }

    /**
     * Anonymises a patient account while preserving all clinical records (GDPR soft-delete).
     *
     * Personal identifiers (name, surname, email, phone) are overwritten or nulled.
     * The fiscal code is replaced with a timestamped tombstone token so the row remains
     * uniquely identifiable internally.  Medical conditions, prescriptions, and surgeries
     * are intentionally kept to satisfy the 5-year clinical retention requirement (NFR09).
     *
     * Prefer this method over {@link #permanentDeletePatient(long)} for all normal deletion
     * requests.
     *
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

        // Domain: patient clears their own transient state (appointments list, medical record ref)
        patient.deleteAccount();

        // Anonymize persistent personal data (GDPR compliance)
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
     * Permanently deletes a patient and ALL associated medical records from the database.
     *
     * This is a destructive, non-recoverable operation intended only for testing environments
     * or court-ordered erasure when anonymisation is legally insufficient.  In all other
     * cases, use {@link #deletePatient(long)} instead.
     *
     * @param id  ID of the patient to permanently erase.
     * @throws RuntimeException When no patient with the given ID exists.
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