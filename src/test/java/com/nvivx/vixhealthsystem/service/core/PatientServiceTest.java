package com.nvivx.vixhealthsystem.service.core;

import com.nvivx.vixhealthsystem.model.person.Patient;
import com.nvivx.vixhealthsystem.repository.PatientRepository;
import com.nvivx.vixhealthsystem.service.AuditService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @brief Unit tests for PatientService using Mockito mocks for PatientRepository and AuditService.
 * Covers CRUD operations (find by id/fiscal code, create, update, soft-delete anonymisation,
 * permanent delete) and the search-patients filtering logic.
 */
@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private PatientService service;

    /**
     * Tests that findById() returns the patient when the ID exists.
     */
    @Test
    void shouldFindPatientById() {
        // Arrange
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setName("Mario");
        patient.setSurname("Rossi");

        when(patientRepository.findById(1L))
                .thenReturn(Optional.of(patient));

        // Act
        Patient result = service.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Mario", result.getName());

        verify(patientRepository).findById(1L);
    }

    /**
     * Tests that findById() throws an exception when the patient does not exist.
     */
    @Test
    void shouldThrowExceptionWhenPatientNotFound() {
        // Arrange
        when(patientRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act + Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.findById(99L)
        );

        assertTrue(exception.getMessage().contains("Patient not found"));

        verify(patientRepository).findById(99L);
    }

    /**
     * Tests that findByFiscalCode() returns the patient when the fiscal code exists.
     */
    @Test
    void shouldFindPatientByFiscalCode() {
        // Arrange
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setFiscalCode("RSSMRA80A01F205X");

        when(patientRepository.findByFiscalCode("RSSMRA80A01F205X"))
                .thenReturn(Optional.of(patient));

        // Act
        Optional<Patient> result =
                service.findByFiscalCode("RSSMRA80A01F205X");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("RSSMRA80A01F205X", result.get().getFiscalCode());

        verify(patientRepository).findByFiscalCode("RSSMRA80A01F205X");
    }

    /**
     * Tests that findByFiscalCode() returns an empty Optional when no patient matches.
     */
    @Test
    void shouldReturnEmptyOptionalWhenFiscalCodeDoesNotExist() {
        // Arrange
        when(patientRepository.findByFiscalCode("UNKNOWN"))
                .thenReturn(Optional.empty());

        // Act
        Optional<Patient> result =
                service.findByFiscalCode("UNKNOWN");

        // Assert
        assertTrue(result.isEmpty());

        verify(patientRepository).findByFiscalCode("UNKNOWN");
    }

    /**
     * Tests that findAllPatients() returns all patients from the repository.
     */
    @Test
    void shouldReturnAllPatients() {
        // Arrange
        Patient p1 = new Patient();
        p1.setName("Mario");

        Patient p2 = new Patient();
        p2.setName("Giulia");

        when(patientRepository.findAll())
                .thenReturn(List.of(p1, p2));

        // Act
        List<Patient> result = service.findAllPatients();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Mario", result.get(0).getName());

        verify(patientRepository).findAll();
    }

    /**
     * Tests that searchPatients() returns all patients when the query is null.
     */
    @Test
    void shouldReturnAllPatientsWhenSearchQueryIsNull() {
        // Arrange
        Patient p1 = new Patient();
        Patient p2 = new Patient();

        when(patientRepository.findAll())
                .thenReturn(List.of(p1, p2));

        // Act
        List<Patient> result = service.searchPatients(null);

        // Assert
        assertEquals(2, result.size());

        verify(patientRepository).findAll();
    }

    /**
     * Tests that searchPatients() returns all patients when the query is blank.
     */
    @Test
    void shouldReturnAllPatientsWhenSearchQueryIsBlank() {
        // Arrange
        Patient p1 = new Patient();

        when(patientRepository.findAll())
                .thenReturn(List.of(p1));

        // Act
        List<Patient> result = service.searchPatients("   ");

        // Assert
        assertEquals(1, result.size());

        verify(patientRepository).findAll();
    }

    /**
     * Tests searching patients by name.
     */
    @Test
    void shouldSearchPatientsByName() {
        // Arrange
        Patient p1 = new Patient();
        p1.setName("Mario");
        p1.setSurname("Rossi");
        p1.setFiscalCode("RSSMRA80A01F205X");

        Patient p2 = new Patient();
        p2.setName("Giulia");
        p2.setSurname("Bianchi");
        p2.setFiscalCode("BNCGLI90B41F205Y");

        when(patientRepository.findAll())
                .thenReturn(List.of(p1, p2));

        // Act
        List<Patient> result = service.searchPatients("mario");

        // Assert
        assertEquals(1, result.size());
        assertEquals("Mario", result.get(0).getName());

        verify(patientRepository).findAll();
    }

    /**
     * Tests searching patients by surname.
     */
    @Test
    void shouldSearchPatientsBySurname() {
        // Arrange
        Patient p1 = new Patient();
        p1.setName("Mario");
        p1.setSurname("Rossi");

        Patient p2 = new Patient();
        p2.setName("Giulia");
        p2.setSurname("Bianchi");

        when(patientRepository.findAll())
                .thenReturn(List.of(p1, p2));

        // Act
        List<Patient> result = service.searchPatients("bianchi");

        // Assert
        assertEquals(1, result.size());
        assertEquals("Bianchi", result.get(0).getSurname());

        verify(patientRepository).findAll();
    }

    /**
     * Tests searching patients by fiscal code.
     */
    @Test
    void shouldSearchPatientsByFiscalCode() {
        // Arrange
        Patient p1 = new Patient();
        p1.setName("Mario");
        p1.setFiscalCode("RSSMRA80A01F205X");

        Patient p2 = new Patient();
        p2.setName("Giulia");
        p2.setFiscalCode("BNCGLI90B41F205Y");

        when(patientRepository.findAll())
                .thenReturn(List.of(p1, p2));

        // Act
        List<Patient> result = service.searchPatients("BNCGLI");

        // Assert
        assertEquals(1, result.size());
        assertEquals("Giulia", result.get(0).getName());

        verify(patientRepository).findAll();
    }

    /**
     * Tests that searchPatients() returns an empty list when no patient matches.
     */
    @Test
    void shouldReturnEmptyListWhenSearchDoesNotMatch() {
        // Arrange
        Patient p1 = new Patient();
        p1.setName("Mario");
        p1.setSurname("Rossi");
        p1.setFiscalCode("RSSMRA80A01F205X");

        when(patientRepository.findAll())
                .thenReturn(List.of(p1));

        // Act
        List<Patient> result = service.searchPatients("notfound");

        // Assert
        assertTrue(result.isEmpty());

        verify(patientRepository).findAll();
    }

    /**
     * Tests that createPatient() saves the patient and logs the creation.
     */
    @Test
    void shouldCreatePatient() {
        // Arrange
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setName("Mario");
        patient.setSurname("Rossi");
        patient.setFiscalCode("RSSMRA80A01F205X");

        when(patientRepository.save(patient))
                .thenReturn(patient);

        // Act
        Patient result = service.createPatient(patient);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Mario", result.getName());

        verify(patientRepository).save(patient);
        verify(auditService).log(
                eq("CREATE_PATIENT"),
                eq("Patient"),
                eq("1"),
                contains("Created patient")
        );
    }

    /**
     * Tests that updatePatient() updates only the non-null fields.
     */
    @Test
    void shouldUpdatePatient() {
        // Arrange
        Patient existing = new Patient();
        existing.setId(1L);
        existing.setName("OldName");
        existing.setSurname("OldSurname");
        existing.setEmail("old@test.com");
        existing.setFiscalCode("OLD123");

        Patient updatedData = new Patient();
        updatedData.setName("NewName");
        updatedData.setSurname("NewSurname");
        updatedData.setEmail("new@test.com");
        updatedData.setPhoneNumber("+39123456789");
        updatedData.setBirthDate(LocalDate.of(2000, 1, 1));
        updatedData.setBirthPlace("Trento");
        updatedData.setGender('F');

        when(patientRepository.findById(1L))
                .thenReturn(Optional.of(existing));

        when(patientRepository.save(existing))
                .thenReturn(existing);

        // Act
        Patient result = service.updatePatient(1L, updatedData);

        // Assert
        assertEquals("NewName", result.getName());
        assertEquals("NewSurname", result.getSurname());
        assertEquals("new@test.com", result.getEmail());
        assertEquals("+39123456789", result.getPhoneNumber());
        assertEquals("OLD123", result.getFiscalCode()); // fiscal code is immutable via profile update
        assertEquals(LocalDate.of(2000, 1, 1), result.getBirthDate());
        assertEquals("Trento", result.getBirthPlace());
        assertEquals('F', result.getGender());

        verify(patientRepository).findById(1L);
        verify(patientRepository).save(existing);
        verify(auditService).log(
                eq("UPDATE_PATIENT"),
                eq("Patient"),
                eq("1"),
                eq("Updated patient details")
        );
    }

    /**
     * Tests that updatePatient() throws an exception when the patient does not exist.
     */
    @Test
    void shouldThrowExceptionWhenUpdatingPatientThatDoesNotExist() {
        // Arrange
        Patient updatedData = new Patient();
        updatedData.setName("NewName");

        when(patientRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act + Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.updatePatient(99L, updatedData)
        );

        assertTrue(exception.getMessage().contains("Patient not found"));

        verify(patientRepository).findById(99L);
        verify(patientRepository, never()).save(any());
    }

    /**
     * Tests that deletePatient() anonymizes the patient instead of removing it.
     */
    @Test
    void shouldAnonymizePatientWhenDeleted() {
        // Arrange
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setName("Mario");
        patient.setSurname("Rossi");
        patient.setEmail("mario@test.com");
        patient.setPhoneNumber("+39123456789");
        patient.setFiscalCode("RSSMRA80A01F205X");

        when(patientRepository.findById(1L))
                .thenReturn(Optional.of(patient));

        when(patientRepository.save(patient))
                .thenReturn(patient);

        // Act
        service.deletePatient(1L);

        // Assert
        assertEquals("ANONYMIZED", patient.getName());
        assertEquals("ANONYMIZED", patient.getSurname());
        assertNull(patient.getEmail());
        assertNull(patient.getPhoneNumber());
        assertTrue(patient.getFiscalCode().startsWith("DELETED_"));

        verify(patientRepository).findById(1L);
        verify(patientRepository).save(patient);
        verify(auditService).log(
                eq("DELETE_PATIENT"),
                eq("Patient"),
                eq("1"),
                contains("Patient account anonymized")
        );
    }

    /**
     * Tests that deletePatient() throws an exception when the patient does not exist.
     */
    @Test
    void shouldThrowExceptionWhenDeletingPatientThatDoesNotExist() {
        // Arrange
        when(patientRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act + Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.deletePatient(99L)
        );

        assertTrue(exception.getMessage().contains("Patient not found"));

        verify(patientRepository).findById(99L);
        verify(patientRepository, never()).save(any());
    }

    /**
     * Tests that permanentDeletePatient() removes the patient and logs the deletion.
     */
    @Test
    void shouldPermanentDeletePatient() {
        // Arrange
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setName("Mario");
        patient.setSurname("Rossi");

        when(patientRepository.findById(1L))
                .thenReturn(Optional.of(patient));

        // Act
        service.permanentDeletePatient(1L);

        // Assert
        verify(patientRepository).findById(1L);
        verify(patientRepository).delete(patient);
        verify(auditService).log(
                eq("PERMANENT_DELETE_PATIENT"),
                eq("Patient"),
                eq("1"),
                contains("Permanently deleted patient")
        );
    }

    /**
     * Tests that permanentDeletePatient() throws an exception when the patient does not exist.
     */
    @Test
    void shouldThrowExceptionWhenPermanentDeletingPatientThatDoesNotExist() {
        // Arrange
        when(patientRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act + Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.permanentDeletePatient(99L)
        );

        assertTrue(exception.getMessage().contains("Patient not found"));

        verify(patientRepository).findById(99L);
        verify(patientRepository, never()).delete(any());
    }
}