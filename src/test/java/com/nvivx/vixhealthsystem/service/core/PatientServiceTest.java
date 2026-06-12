package com.nvivx.vixhealthsystem.service.core;

import com.nvivx.vixhealthsystem.model.person.Patient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PatientServiceTest {

    @Autowired
    private PatientService patientService;

    @Test
    void shouldFindPatientById() {
        Patient patient = patientService.findById(1L);

        assertNotNull(patient);
        assertEquals(1L, patient.getId());
        assertEquals("Mario", patient.getName());
        assertEquals("Rossi", patient.getSurname());
        assertEquals("RSSMRA80A01F205X", patient.getFiscalCode());
    }

    @Test
    void shouldThrowExceptionWhenPatientIdDoesNotExist() {
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> patientService.findById(999L)
        );

        assertTrue(exception.getMessage().contains("Patient not found"));
    }

    @Test
    void shouldFindPatientByFiscalCode() {
        Optional<Patient> patient =
                patientService.findByFiscalCode("RSSMRA80A01F205X");

        assertTrue(patient.isPresent());
        assertEquals("Mario", patient.get().getName());
        assertEquals("Rossi", patient.get().getSurname());
    }

    @Test
    void shouldReturnEmptyOptionalWhenFiscalCodeDoesNotExist() {
        Optional<Patient> patient =
                patientService.findByFiscalCode("NOTEXIST12345678");

        assertTrue(patient.isEmpty());
    }

    @Test
    void shouldReturnAllPatientsFromDatabase() {
        List<Patient> patients = patientService.findAllPatients();

        assertNotNull(patients);
        assertEquals(5, patients.size());
    }

    @Test
    void shouldSearchPatientsByName() {
        List<Patient> patients = patientService.searchPatients("Mario");

        assertNotNull(patients);
        assertFalse(patients.isEmpty());

        assertTrue(patients.stream()
                .anyMatch(p -> p.getName().equals("Mario")));
    }

    @Test
    void shouldSearchPatientsBySurname() {
        List<Patient> patients = patientService.searchPatients("Bianchi");

        assertNotNull(patients);
        assertFalse(patients.isEmpty());

        assertTrue(patients.stream()
                .anyMatch(p -> p.getSurname().equals("Bianchi")));
    }

    @Test
    void shouldSearchPatientsByFiscalCode() {
        List<Patient> patients = patientService.searchPatients("RSSMRA80");

        assertNotNull(patients);
        assertFalse(patients.isEmpty());

        assertTrue(patients.stream()
                .anyMatch(p -> p.getFiscalCode().equals("RSSMRA80A01F205X")));
    }

    @Test
    void shouldReturnAllPatientsWhenSearchQueryIsNull() {
        List<Patient> patients = patientService.searchPatients(null);

        assertNotNull(patients);
        assertEquals(5, patients.size());
    }

    @Test
    void shouldReturnAllPatientsWhenSearchQueryIsBlank() {
        List<Patient> patients = patientService.searchPatients("   ");

        assertNotNull(patients);
        assertEquals(5, patients.size());
    }

    @Test
    void shouldReturnEmptyListWhenSearchDoesNotMatch() {
        List<Patient> patients = patientService.searchPatients("NoMatchingPatient");

        assertNotNull(patients);
        assertTrue(patients.isEmpty());
    }

    @Test
    void shouldUpdatePatient() {
        Patient updatedData = new Patient();

        updatedData.setName("UpdatedName");
        updatedData.setSurname("UpdatedSurname");
        updatedData.setEmail("updated.patient@vixhealth.com");
        updatedData.setPhoneNumber("+391234567890");

        Patient updated = patientService.updatePatient(1L, updatedData);

        assertNotNull(updated);
        assertEquals("UpdatedName", updated.getName());
        assertEquals("UpdatedSurname", updated.getSurname());
        assertEquals("updated.patient@vixhealth.com", updated.getEmail());
        assertEquals("+391234567890", updated.getPhoneNumber());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingPatientThatDoesNotExist() {
        Patient updatedData = new Patient();
        updatedData.setName("Nobody");

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> patientService.updatePatient(999L, updatedData)
        );

        assertTrue(exception.getMessage().contains("Patient not found"));
    }

    @Test
    void shouldAnonymizePatientWhenDeleted() {
        patientService.deletePatient(2L);

        Patient deleted = patientService.findById(2L);

        assertNotNull(deleted);
        assertEquals("ANONYMIZED", deleted.getName());
        assertEquals("ANONYMIZED", deleted.getSurname());
        assertNull(deleted.getEmail());
        assertNull(deleted.getPhoneNumber());
        assertTrue(deleted.getFiscalCode().startsWith("DELETED_"));
    }

    @Test
    void shouldThrowExceptionWhenDeletingPatientThatDoesNotExist() {
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> patientService.deletePatient(999L)
        );

        assertTrue(exception.getMessage().contains("Patient not found"));
    }

    @Test
    void shouldCreatePatient() {
        Patient patient = new Patient();

        long unique = System.currentTimeMillis();

        patient.setName("Test");
        patient.setSurname("Patient");
        patient.setFiscalCode("TEST" + unique);
        patient.setEmail("test.patient." + unique + "@vixhealth.com");
        patient.setPhoneNumber("+390000000000");
        patient.setBirthDate(LocalDate.of(2000, 1, 1));
        patient.setBirthPlace("Test City");
        patient.setGender('F');

        Patient saved = patientService.createPatient(patient);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("Test", saved.getName());
        assertEquals("Patient", saved.getSurname());
        assertEquals("TEST" + unique, saved.getFiscalCode());
    }

    @Test
    void shouldPermanentDeletePatient() {
        Patient patient = new Patient();

        long unique = System.currentTimeMillis();

        patient.setName("Permanent");
        patient.setSurname("Delete");
        patient.setFiscalCode("PERM" + unique);
        patient.setEmail("permanent.delete." + unique + "@vixhealth.com");
        patient.setPhoneNumber("+391111111111");
        patient.setBirthDate(LocalDate.of(2001, 2, 2));
        patient.setBirthPlace("Delete City");
        patient.setGender('M');

        Patient saved = patientService.createPatient(patient);

        assertNotNull(saved.getId());

        patientService.permanentDeletePatient(saved.getId());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> patientService.findById(saved.getId())
        );

        assertTrue(exception.getMessage().contains("Patient not found"));
    }
}