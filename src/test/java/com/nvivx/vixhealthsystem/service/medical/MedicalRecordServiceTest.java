package com.nvivx.vixhealthsystem.service.medical;

import com.nvivx.vixhealthsystem.model.medical.*;
import com.nvivx.vixhealthsystem.model.person.Patient;
import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import com.nvivx.vixhealthsystem.repository.EmployeeRepository;
import com.nvivx.vixhealthsystem.repository.MedicalRecordRepository;
import com.nvivx.vixhealthsystem.repository.PatientRepository;
import com.nvivx.vixhealthsystem.service.AuditService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicalRecordServiceTest {

    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private MedicalRecordService service;

    @Test
    void shouldGetMedicalRecordByPatientId() {

        // Arrange = prepare fake data and mock behavior
        MedicalRecord record = new MedicalRecord();
        record.setId(1L);

        when(medicalRecordRepository.findByPatientId(1L))
                .thenReturn(Optional.of(record));

        // Act = call the method being tested
        MedicalRecord result =
                service.getMedicalRecordByPatientId(1L);

        // Assert = check the result
        assertNotNull(result);
        assertEquals(1L, result.getId());

        // Verify = check that mocks were called correctly
        verify(medicalRecordRepository)
                .findByPatientId(1L);
    }

    @Test
    void shouldThrowWhenMedicalRecordNotFound() {

        // Arrange
        when(medicalRecordRepository.findByPatientId(99L))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                RuntimeException.class,
                () -> service.getMedicalRecordByPatientId(99L)
        );

        // Verify
        verify(medicalRecordRepository)
                .findByPatientId(99L);
    }

    @Test
    void shouldCreateMedicalRecord() {

        // Arrange
        Patient patient = new Patient();
        patient.setId(1L);

        MedicalRecord savedRecord = new MedicalRecord(
                180f,
                75f,
                "A+"
        );
        savedRecord.setId(10L);

        when(patientRepository.findById(1L))
                .thenReturn(Optional.of(patient));

        when(medicalRecordRepository.save(any(MedicalRecord.class)))
                .thenReturn(savedRecord);

        // Act
        MedicalRecord result =
                service.createMedicalRecord(
                        1L,
                        180f,
                        75f,
                        "A+"
                );

        // Assert
        assertNotNull(result);
        assertEquals(10L, result.getId());

        // Verify
        verify(patientRepository).findById(1L);
        verify(medicalRecordRepository).save(any(MedicalRecord.class));
        verify(patientRepository).save(patient);

        verify(auditService).log(
                eq("CREATE_MEDICAL_RECORD"),
                eq("MedicalRecord"),
                anyString(),
                anyString()
        );
    }

    @Test
    void shouldAddDiagnosis() {

        // Arrange
        Patient patient = new Patient();

        MedicalRecord record = new MedicalRecord();
        record.setId(5L);

        patient.setMedicalRecord(record);

        when(patientRepository.findById(1L))
                .thenReturn(Optional.of(patient));

        when(medicalRecordRepository.save(any(MedicalRecord.class)))
                .thenReturn(record);

        // Act
        service.addDiagnosis(
                1L,
                "Diabetes",
                "Type 2 Diabetes",
                "HIGH"
        );

        // Assert
        assertEquals(1, record.getConditions().size());

        MedicalCondition condition =
                record.getConditions().get(0);

        assertEquals("Diabetes", condition.getName());
        assertEquals("HIGH", condition.getType());

        // Verify
        verify(medicalRecordRepository)
                .save(record);

        verify(auditService).log(
                eq("ADD_DIAGNOSIS"),
                eq("MedicalRecord"),
                anyString(),
                anyString()
        );
    }

    @Test
    void shouldAddPrescription() {

        // Arrange
        Patient patient = new Patient();

        MedicalRecord record = new MedicalRecord();
        record.setId(3L);

        patient.setMedicalRecord(record);

        MedicalSpecialist specialist =
                new MedicalSpecialist();

        when(patientRepository.findById(1L))
                .thenReturn(Optional.of(patient));

        when(employeeRepository.findById(2L))
                .thenReturn(Optional.of(specialist));

        when(medicalRecordRepository.save(any(MedicalRecord.class)))
                .thenReturn(record);

        // Act
        service.addPrescription(
                1L,
                2L,
                "Ibuprofen"
        );

        // Assert
        assertEquals(1, record.getPrescriptions().size());

        Prescription prescription =
                record.getPrescriptions().get(0);

        assertEquals(
                "Ibuprofen",
                prescription.getMedication()
        );

        // Verify
        verify(employeeRepository)
                .findById(2L);

        verify(medicalRecordRepository)
                .save(record);

        verify(auditService).log(
                eq("ADD_PRESCRIPTION"),
                eq("MedicalRecord"),
                anyString(),
                anyString()
        );
    }

    @Test
    void shouldAddExamResult() {

        // Arrange
        Patient patient = new Patient();

        MedicalRecord record = new MedicalRecord();
        record.setId(7L);

        patient.setMedicalRecord(record);

        when(patientRepository.findById(1L))
                .thenReturn(Optional.of(patient));

        when(medicalRecordRepository.save(any(MedicalRecord.class)))
                .thenReturn(record);

        // Act
        service.addExamResult(
                1L,
                "Blood Test",
                "Normal",
                "Everything looks good"
        );

        // Assert
        assertEquals(1, record.getConditions().size());

        MedicalCondition exam =
                record.getConditions().get(0);

        assertEquals(
                "Blood Test",
                exam.getName()
        );

        assertEquals(
                "EXAM_RESULT",
                exam.getType()
        );

        // Verify
        verify(medicalRecordRepository)
                .save(record);

        verify(auditService).log(
                eq("ADD_EXAM_RESULT"),
                eq("MedicalRecord"),
                anyString(),
                anyString()
        );
    }

    @Test
    void shouldReturnConditions() {

        // Arrange
        MedicalRecord record = new MedicalRecord();

        MedicalCondition condition =
                new MedicalCondition();

        condition.setName("Asthma");

        record.setConditions(List.of(condition));

        when(medicalRecordRepository.findByPatientId(1L))
                .thenReturn(Optional.of(record));

        // Act
        List<MedicalCondition> result =
                service.getConditions(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals(
                "Asthma",
                result.get(0).getName()
        );

        // Verify
        verify(medicalRecordRepository)
                .findByPatientId(1L);
    }

    @Test
    void shouldReturnPrescriptions() {

        // Arrange
        MedicalRecord record = new MedicalRecord();

        Prescription prescription =
                new Prescription();

        prescription.setMedication("Paracetamol");

        record.setPrescriptions(List.of(prescription));

        when(medicalRecordRepository.findByPatientId(1L))
                .thenReturn(Optional.of(record));

        // Act
        List<Prescription> result =
                service.getPrescriptions(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals(
                "Paracetamol",
                result.get(0).getMedication()
        );

        // Verify
        verify(medicalRecordRepository)
                .findByPatientId(1L);
    }

    @Test
    void shouldReturnSurgeries() {

        // Arrange
        MedicalRecord record = new MedicalRecord();

        Surgery surgery = new Surgery();
        surgery.setName("Appendectomy");

        record.setSurgeries(List.of(surgery));

        when(medicalRecordRepository.findByPatientId(1L))
                .thenReturn(Optional.of(record));

        // Act
        List<Surgery> result =
                service.getSurgeries(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals(
                "Appendectomy",
                result.get(0).getName()
        );

        // Verify
        verify(medicalRecordRepository)
                .findByPatientId(1L);
    }
}