package com.nvivx.vixhealthsystem.service;

import com.nvivx.vixhealthsystem.mock.MockDatabase;
import com.nvivx.vixhealthsystem.model.medical.MedicalCondition;
import com.nvivx.vixhealthsystem.model.medical.MedicalRecord;
import com.nvivx.vixhealthsystem.model.medical.Prescription;
import com.nvivx.vixhealthsystem.model.person.Patient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MedicalSpecialistService {

    private final MockDatabase mockDatabase;

    // MOCK_ONLY: In-memory storage for patients
    private final List<Patient> patients = new ArrayList<>();
    private final AtomicInteger patientIdCounter = new AtomicInteger(1);

    public MedicalSpecialistService(MockDatabase mockDatabase) {
        this.mockDatabase = mockDatabase;
        loadSamplePatients();
    }

    private void loadSamplePatients() {
        // Sample Patient 1 - Using CORRECT constructor for MedicalRecord
        Patient patient1 = new Patient();
        patient1.setId(patientIdCounter.getAndIncrement());
        patient1.setName("Maria");
        patient1.setSurname("Rossi");
        patient1.setEmail("maria.rossi@example.com");
        patient1.setFiscalCode("RSSMRA85M01F205X");

        // MedicalRecord constructor: (height, weight, bloodType, allergies, vaccines)
        MedicalRecord record1 = new MedicalRecord(165f, 60f, "A+", new String[]{}, new String[]{});
        patient1.setMedicalRecord(record1);
        patients.add(patient1);

        // Sample Patient 2
        Patient patient2 = new Patient();
        patient2.setId(patientIdCounter.getAndIncrement());
        patient2.setName("Marco");
        patient2.setSurname("Bianchi");
        patient2.setEmail("marco.bianchi@example.com");
        patient2.setFiscalCode("BNCMRC90E15F205X");

        MedicalRecord record2 = new MedicalRecord(175f, 75f, "O-", new String[]{}, new String[]{});
        patient2.setMedicalRecord(record2);
        patients.add(patient2);

        // Sample Patient 3
        Patient patient3 = new Patient();
        patient3.setId(patientIdCounter.getAndIncrement());
        patient3.setName("Giulia");
        patient3.setSurname("Verdi");
        patient3.setEmail("giulia.verdi@example.com");
        patient3.setFiscalCode("VRDGLI95L41F205X");

        // Empty medical record (no data yet)
        MedicalRecord record3 = new MedicalRecord(0f, 0f, "Unknown", new String[]{}, new String[]{});
        patient3.setMedicalRecord(record3);
        patients.add(patient3);
    }

    // ========== UC19: VIEW PATIENT MEDICAL RECORD ==========
    public Patient getPatientWithMedicalRecord(int patientId) {
        return patients.stream()
                .filter(p -> p.getId() == patientId)
                .findFirst()
                .orElse(null);
    }

    // Search patients by name or fiscal code
    public List<Patient> searchPatients(String query) {
        String lowerQuery = query.toLowerCase();
        return patients.stream()
                .filter(p -> p.getName().toLowerCase().contains(lowerQuery) ||
                        p.getSurname().toLowerCase().contains(lowerQuery) ||
                        (p.getFiscalCode() != null && p.getFiscalCode().toLowerCase().contains(lowerQuery)))
                .toList();
    }

    // Get all patients
    public List<Patient> getAllPatients() {
        return new ArrayList<>(patients);
    }

    // ========== UC20: ADD DIAGNOSIS ==========
    public void addDiagnosis(int patientId, String diagnosisName, String description, String severity) {
        Patient patient = getPatientWithMedicalRecord(patientId);
        if (patient == null) {
            throw new IllegalArgumentException("Patient not found with id: " + patientId);
        }

        MedicalRecord record = patient.getMedicalRecord();
        if (record == null) {
            // Create a new record if it doesn't exist
            record = new MedicalRecord(0f, 0f, "Unknown", new String[]{}, new String[]{});
            patient.setMedicalRecord(record);
        }

        // Use the 5-parameter constructor, then set severity separately
        MedicalCondition condition = new MedicalCondition(
                diagnosisName,
                LocalDate.now(),
                "DIAGNOSIS",  // type/category of the condition
                description,
                ""  // treatment - can be updated later
        );
        condition.setSeverity(severity);  // Set severity after construction

        // Add to record
        if (record.getConditions() == null) {
            record.setConditions(new ArrayList<>());
        }
        record.getConditions().add(condition);
    }

    // ========== UC20: ADD PRESCRIPTION ==========
    public void addPrescription(int patientId, String medication, String dosage) {
        Patient patient = getPatientWithMedicalRecord(patientId);
        if (patient == null) {
            throw new IllegalArgumentException("Patient not found with id: " + patientId);
        }

        MedicalRecord record = patient.getMedicalRecord();
        if (record == null) {
            record = new MedicalRecord(0f, 0f, "Unknown", new String[]{}, new String[]{});
            patient.setMedicalRecord(record);
        }

        // Prescription constructor: (dateTime, medication)
        Prescription prescription = new Prescription(
                LocalDateTime.now(),
                medication + " - " + dosage
        );

        // TODO: You need to add prescriptions list to MedicalRecord class
    }

    // ========== UC20: ADD EXAM RESULT ==========
    public void addExamResult(int patientId, String examType, String result, String notes) {
        // For now, just validate patient exists
        Patient patient = getPatientWithMedicalRecord(patientId);
        if (patient == null) {
            throw new IllegalArgumentException("Patient not found with id: " + patientId);
        }

        // TODO: Store exam results as medical conditions or a separate entity
    }
}