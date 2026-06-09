package com.nvivx.vixhealthsystem.service.medical;

import com.nvivx.vixhealthsystem.model.medical.MedicalRecord;
import com.nvivx.vixhealthsystem.model.medical.MedicalCondition;
import com.nvivx.vixhealthsystem.model.medical.Prescription;
import com.nvivx.vixhealthsystem.model.person.Patient;
import com.nvivx.vixhealthsystem.repository.MedicalRecordRepository;
import com.nvivx.vixhealthsystem.repository.PatientRepository;
import com.nvivx.vixhealthsystem.service.AuditService;
import com.nvivx.vixhealthsystem.service.core.PatientService;
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
    private final AuditService auditService;

    public MedicalRecordService(MedicalRecordRepository medicalRecordRepository,
                                PatientRepository patientRepository,
                                AuditService auditService) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.patientRepository = patientRepository;
        this.auditService = auditService;
    }

    public MedicalRecord getMedicalRecordByPatientId(Long patientId) {
        return medicalRecordRepository.findByPatientId(patientId)
                .orElseThrow(() -> new RuntimeException("Medical record not found for patient: " + patientId));
    }

    public Patient getPatientWithMedicalRecord(Long patientId) {
        return patientRepository.findByIdWithMedicalRecord(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found: " + patientId));
    }

    @Transactional
    public void addDiagnosis(Long patientId, String name, String description, String severity) {
        Patient patient = getPatientWithMedicalRecord(patientId);
        MedicalRecord record = patient.getMedicalRecord();

        if (record == null) {
            record = new MedicalRecord();
            record.setPatient(patient);
            patient.setMedicalRecord(record);
        }

        MedicalCondition condition = new MedicalCondition();
        condition.setName(name);
        condition.setDescription(description);
        condition.setDateOfDiagnosis(LocalDate.now());
        condition.setType(severity);
        condition.setMedicalRecord(record);

        if (record.getConditions() == null) {
            record.setConditions(new ArrayList<>());
        }
        record.getConditions().add(condition);

        medicalRecordRepository.save(record);

        auditService.log("ADD_DIAGNOSIS", "MedicalRecord", String.valueOf(record.getId()),
                "Added diagnosis: " + name + " for patient: " + patientId);
    }

    @Transactional
    public void addPrescription(Long patientId, Long medicalSpecialistId, String medication) {
        Patient patient = getPatientWithMedicalRecord(patientId);
        MedicalRecord record = patient.getMedicalRecord();

        if (record == null) {
            record = new MedicalRecord();
            record.setPatient(patient);
            patient.setMedicalRecord(record);
        }

        Prescription prescription = new Prescription();
        prescription.setMedication(medication);
        prescription.setDateTime(LocalDateTime.now());
        prescription.setMedicalRecord(record);

        // Set medical specialist - need to fetch from database
        // prescription.setMedicalSpecialist(medicalSpecialist);

        if (record.getPrescriptions() == null) {
            record.setPrescriptions(new ArrayList<>());
        }
        record.getPrescriptions().add(prescription);

        medicalRecordRepository.save(record);

        auditService.log("ADD_PRESCRIPTION", "MedicalRecord", String.valueOf(record.getId()),
                "Added prescription: " + medication + " for patient: " + patientId);
    }

    @Transactional
    public void addExamResult(Long patientId, String examType, String result, String notes) {
        Patient patient = getPatientWithMedicalRecord(patientId);
        MedicalRecord record = patient.getMedicalRecord();

        if (record == null) {
            record = new MedicalRecord();
            record.setPatient(patient);
            patient.setMedicalRecord(record);
        }

        // Store exam result as a medical condition with type "EXAM_RESULT"
        MedicalCondition examResult = new MedicalCondition();
        examResult.setName(examType);
        examResult.setDescription("Result: " + result + (notes != null ? "\nNotes: " + notes : ""));
        examResult.setDateOfDiagnosis(LocalDate.now());
        examResult.setType("EXAM_RESULT");
        examResult.setMedicalRecord(record);

        if (record.getConditions() == null) {
            record.setConditions(new ArrayList<>());
        }
        record.getConditions().add(examResult);

        medicalRecordRepository.save(record);

        auditService.log("ADD_EXAM_RESULT", "MedicalRecord", String.valueOf(record.getId()),
                "Added exam result: " + examType + " for patient: " + patientId);

        // TODO: Trigger notification via NotificationService
    }
}