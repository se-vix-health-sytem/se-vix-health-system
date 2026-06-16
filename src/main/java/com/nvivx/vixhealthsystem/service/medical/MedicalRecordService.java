package com.nvivx.vixhealthsystem.service.medical;

import com.nvivx.vixhealthsystem.model.medical.MedicalRecord;
import com.nvivx.vixhealthsystem.model.medical.MedicalCondition;
import com.nvivx.vixhealthsystem.model.medical.Prescription;
import com.nvivx.vixhealthsystem.model.medical.Surgery;
import com.nvivx.vixhealthsystem.model.person.Patient;
import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import com.nvivx.vixhealthsystem.repository.MedicalRecordRepository;
import com.nvivx.vixhealthsystem.repository.PatientRepository;
import com.nvivx.vixhealthsystem.repository.EmployeeRepository;
import com.nvivx.vixhealthsystem.service.AuditService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @brief Manages patient medical records : creation and appending of diagnoses,
 *        prescriptions, and exam results.
 *
 * Annotated {@code @Transactional(readOnly=true)} at the class level; write methods
 * override with {@code @Transactional}.
 *
 * All mutations are recorded via {@link AuditService} to satisfy NFR02 (traceability).
 * When a patient has no record yet, write operations auto-create one rather than failing.
 *
 * Prescription issuance delegates to {@link com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist#appPrescriptionForPatient}
 * so that domain invariants are enforced by the model layer.
 *
 * @see PatientService
 * @see com.nvivx.vixhealthsystem.model.medical.MedicalRecord
 * @see AuditService
 */
@Service
@Transactional(readOnly = true)
public class MedicalRecordService {

    // =========================================================
    // FIELDS
    // =========================================================

    private final MedicalRecordRepository medicalRecordRepository;
    private final PatientRepository patientRepository;
    /** Used to look up the prescribing {@link MedicalSpecialist} by ID. */
    private final EmployeeRepository employeeRepository;
    private final AuditService auditService;

    // =========================================================
    // CONSTRUCTORS
    // =========================================================

    /**
     * Constructs the service with all required repositories and collaborators.
     *
     * @param medicalRecordRepository  Persistence layer for {@link MedicalRecord} entities.
     * @param patientRepository        Used to load and save the owning {@link Patient}.
     * @param employeeRepository       Needed to resolve the prescribing specialist by ID.
     * @param auditService             Records every clinical write for traceability (NFR02).
     */
    public MedicalRecordService(MedicalRecordRepository medicalRecordRepository,
                                PatientRepository patientRepository,
                                EmployeeRepository employeeRepository,
                                AuditService auditService) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.patientRepository = patientRepository;
        this.employeeRepository = employeeRepository;
        this.auditService = auditService;
    }

    // =========================================================
    // READ OPERATIONS
    // =========================================================

    /**
     * Returns the medical record for the given patient, throwing when absent.
     *
     * @param patientId  ID of the patient.
     * @return           The patient's {@link MedicalRecord}; never {@code null}.
     * @throws RuntimeException When no medical record exists for the patient.
     */
    public MedicalRecord getMedicalRecordByPatientId(Long patientId) {
        return medicalRecordRepository.findByPatientId(patientId)
                .orElseThrow(() -> new RuntimeException("Medical record not found for patient: " + patientId));
    }

    /**
     * Loads a patient and touches their medical record proxy to force initialisation.
     *
     * JPA lazy-loading means the record may still be a proxy after {@code findById}; this
     * method ensures the proxy is initialised before the session closes.
     *
     * @param patientId  ID of the patient to load.
     * @return           The {@link Patient} with the {@code medicalRecord} association initialised.
     * @throws RuntimeException When no patient with {@code patientId} exists.
     */
    public Patient getPatientWithMedicalRecord(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found: " + patientId));

        // Force load medical record if needed (JPA will handle lazy loading)
        if (patient.getMedicalRecord() != null) {
            patient.getMedicalRecord().getId(); // Touch to initialize if proxy
        }

        return patient;
    }

    // =========================================================
    // WRITE OPERATIONS
    // =========================================================

    /**
     * Creates and links a new {@link MedicalRecord} for a patient.
     *
     * All three metric parameters are optional; pass {@code null} when not yet known.
     * Allergies and vaccines are initialised to empty strings to avoid null-checks downstream.
     *
     * @param patientId  ID of the patient who owns the record.
     * @param height     Patient height in centimetres; may be {@code null}.
     * @param weight     Patient weight in kilograms; may be {@code null}.
     * @param bloodType  ABO+Rh blood group string (e.g., {@code "A+"}); may be {@code null}.
     * @return           The persisted {@link MedicalRecord} with a generated ID.
     * @throws RuntimeException When no patient with {@code patientId} exists.
     */
    @Transactional
    public MedicalRecord createMedicalRecord(Long patientId, Float height, Float weight, String bloodType) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found: " + patientId));

        MedicalRecord record = new MedicalRecord(height, weight, bloodType);
        record.setPatient(patient);
        record.setAllergies("");
        record.setVaccines("");

        MedicalRecord saved = medicalRecordRepository.save(record);
        patient.setMedicalRecord(saved);
        patientRepository.save(patient);

        auditService.log("CREATE_MEDICAL_RECORD", "MedicalRecord", String.valueOf(saved.getId()),
                "Created medical record for patient: " + patientId);

        return saved;
    }

    /**
     * Appends a diagnosis to the patient's medical record, creating the record first
     *        if it does not exist.
     *
     * @param patientId      ID of the patient to diagnose.
     * @param diagnosisName  Short clinical label for the condition.
     * @param description    Detailed clinical notes.
     * @param severity       Severity tier (e.g., {@code "MILD"}, {@code "SEVERE"}).
     * @throws RuntimeException When no patient with {@code patientId} exists.
     */
    @Transactional
    public void addDiagnosis(Long patientId, String diagnosisName, String description, String severity) {
        Patient patient = getPatientWithMedicalRecord(patientId);
        MedicalRecord record = patient.getMedicalRecord();

        if (record == null) {
            record = createMedicalRecord(patientId, null, null, null);
        }

        MedicalCondition condition = new MedicalCondition();
        condition.setName(diagnosisName);
        condition.setDescription(description);
        condition.setDateOfDiagnosis(LocalDate.now());
        condition.setType(severity);

        record.addCondition(condition);
        medicalRecordRepository.save(record);

        auditService.log("ADD_DIAGNOSIS", "MedicalRecord", String.valueOf(record.getId()),
                "Added diagnosis: " + diagnosisName + " (severity: " + severity + ") for patient: " + patientId);
    }

    /**
     * Issues a prescription for the patient via the prescribing specialist's domain method.
     *
     * Delegates to {@link com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist#appPrescriptionForPatient}
     * so that the specialist back-reference on the {@link Prescription} is set correctly.
     * Auto-creates a medical record when the patient has none.
     *
     * @param patientId           ID of the patient receiving the prescription.
     * @param medicalSpecialistId ID of the issuing {@link MedicalSpecialist}.
     * @param medication          Name or description of the prescribed medication.
     * @throws RuntimeException When the patient or specialist cannot be found, or the
     *                          employee ID does not resolve to a {@link MedicalSpecialist}.
     */
    @Transactional
    public void addPrescription(Long patientId, Long medicalSpecialistId, String medication) {
        Patient patient = getPatientWithMedicalRecord(patientId);
        MedicalSpecialist specialist = (MedicalSpecialist) employeeRepository.findById(medicalSpecialistId)
                .orElseThrow(() -> new RuntimeException("Medical specialist not found: " + medicalSpecialistId));

        MedicalRecord record = patient.getMedicalRecord();

        if (record == null) {
            record = createMedicalRecord(patientId, null, null, null);
            // Reload patient so the new record is attached to the same in-memory object
            patient = patientRepository.findById(patientId)
                    .orElseThrow(() -> new RuntimeException("Patient not found: " + patientId));
        }

        Prescription prescription = new Prescription();
        prescription.setMedication(medication);
        prescription.setDateTime(LocalDateTime.now());

        // Domain: specialist issues prescription for patient via model method,
        // which sets the specialist back-reference and adds to the medical record
        specialist.appPrescriptionForPatient(patient, prescription);
        medicalRecordRepository.save(patient.getMedicalRecord());

        auditService.log("ADD_PRESCRIPTION", "MedicalRecord", String.valueOf(record.getId()),
                "Added prescription: " + medication + " for patient: " + patientId + " by specialist: " + medicalSpecialistId);
    }

    /**
     * Records an exam result in the patient's medical record.
     *
     * Exam results are modelled as {@link MedicalCondition} entries with
     * {@code type = "EXAM_RESULT"} because no separate exam-result entity exists yet.
     * Auto-creates a medical record when the patient has none.
     *
     * @param patientId  ID of the patient.
     * @param examType   Type of examination (e.g., {@code "Blood Test"}, {@code "MRI"}).
     * @param result     The outcome of the exam (e.g., {@code "Normal"}, numeric values).
     * @param notes      Optional additional clinical notes; may be {@code null} or blank.
     * @throws RuntimeException When no patient with {@code patientId} exists.
     */
    @Transactional
    public void addExamResult(Long patientId, String examType, String result, String notes) {
        Patient patient = getPatientWithMedicalRecord(patientId);
        MedicalRecord record = patient.getMedicalRecord();

        if (record == null) {
            record = createMedicalRecord(patientId, null, null, null);
        }

        String description = "Result: " + result;
        if (notes != null && !notes.isEmpty()) {
            description += "\nNotes: " + notes;
        }

        MedicalCondition examResult = new MedicalCondition();
        examResult.setName(examType);
        examResult.setDescription(description);
        examResult.setDateOfDiagnosis(LocalDate.now());
        examResult.setType("EXAM_RESULT");

        record.addCondition(examResult);
        medicalRecordRepository.save(record);

        auditService.log("ADD_EXAM_RESULT", "MedicalRecord", String.valueOf(record.getId()),
                "Added exam result: " + examType + " for patient: " + patientId);

    }

    // clinical data accessors

    /**
     * Returns all medical conditions on record for the given patient.
     *
     * @param patientId  ID of the patient.
     * @return           Non-null list of {@link MedicalCondition} entries.
     * @throws RuntimeException When no medical record exists for the patient.
     */
    public List<MedicalCondition> getConditions(Long patientId) {
        MedicalRecord record = getMedicalRecordByPatientId(patientId);
        return record.getConditions();
    }

    /**
     * Returns all prescriptions issued to the given patient.
     *
     * @param patientId  ID of the patient.
     * @return           Non-null list of {@link Prescription} entries.
     * @throws RuntimeException When no medical record exists for the patient.
     */
    public List<Prescription> getPrescriptions(Long patientId) {
        MedicalRecord record = getMedicalRecordByPatientId(patientId);
        return record.getPrescriptions();
    }

    /**
     * Returns all surgeries recorded for the given patient.
     *
     * @param patientId  ID of the patient.
     * @return           Non-null list of {@link Surgery} entries.
     * @throws RuntimeException When no medical record exists for the patient.
     */
    public List<Surgery> getSurgeries(Long patientId) {
        MedicalRecord record = getMedicalRecordByPatientId(patientId);
        return record.getSurgeries();
    }
}