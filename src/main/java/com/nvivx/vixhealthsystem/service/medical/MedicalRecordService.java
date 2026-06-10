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

@Service
@Transactional(readOnly = true)
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PatientRepository patientRepository;
    private final EmployeeRepository employeeRepository;
    private final AuditService auditService;

    public MedicalRecordService(MedicalRecordRepository medicalRecordRepository,
                                PatientRepository patientRepository,
                                EmployeeRepository employeeRepository,
                                AuditService auditService) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.patientRepository = patientRepository;
        this.employeeRepository = employeeRepository;
        this.auditService = auditService;
    }

    /**
     * Get medical record by patient ID
     */
    public MedicalRecord getMedicalRecordByPatientId(Long patientId) {
        return medicalRecordRepository.findByPatientId(patientId)
                .orElseThrow(() -> new RuntimeException("Medical record not found for patient: " + patientId));
    }

    /**
     * Get patient with their medical record (uses standard repository methods)
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

    /**
     * Create a new medical record for a patient
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
     * Add a diagnosis to a patient's medical record
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
     * Add a prescription to a patient's medical record
     */
    @Transactional
    public void addPrescription(Long patientId, Long medicalSpecialistId, String medication) {
        Patient patient = getPatientWithMedicalRecord(patientId);
        MedicalSpecialist specialist = (MedicalSpecialist) employeeRepository.findById(medicalSpecialistId)
                .orElseThrow(() -> new RuntimeException("Medical specialist not found: " + medicalSpecialistId));

        MedicalRecord record = patient.getMedicalRecord();

        if (record == null) {
            record = createMedicalRecord(patientId, null, null, null);
        }

        Prescription prescription = new Prescription();
        prescription.setMedication(medication);
        prescription.setDateTime(LocalDateTime.now());
        prescription.setMedicalSpecialist(specialist);

        record.addPrescription(prescription);
        medicalRecordRepository.save(record);

        auditService.log("ADD_PRESCRIPTION", "MedicalRecord", String.valueOf(record.getId()),
                "Added prescription: " + medication + " for patient: " + patientId + " by specialist: " + medicalSpecialistId);
    }

    /**
     * Add an exam result to a patient's medical record
     * Exam results are stored as MedicalCondition with type "EXAM_RESULT"
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

        // TODO: Trigger notification via NotificationService when implemented
    }

    /**
     * Get all conditions for a patient
     */
    public List<MedicalCondition> getConditions(Long patientId) {
        MedicalRecord record = getMedicalRecordByPatientId(patientId);
        return record.getConditions();
    }

    /**
     * Get all prescriptions for a patient
     */
    public List<Prescription> getPrescriptions(Long patientId) {
        MedicalRecord record = getMedicalRecordByPatientId(patientId);
        return record.getPrescriptions();
    }

    /**
     * Get all surgeries for a patient
     */
    public List<Surgery> getSurgeries(Long patientId) {
        MedicalRecord record = getMedicalRecordByPatientId(patientId);
        return record.getSurgeries();
    }
}