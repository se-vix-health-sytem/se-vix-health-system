package com.nvivx.vixhealthsystem.model.person.employee;

import com.nvivx.vixhealthsystem.model.enums.EmployeeType;
import com.nvivx.vixhealthsystem.model.enums.Role;
import com.nvivx.vixhealthsystem.model.medical.Appointment;
import com.nvivx.vixhealthsystem.model.medical.MedicalRecord;
import com.nvivx.vixhealthsystem.model.medical.Prescription;
import com.nvivx.vixhealthsystem.model.medical.Surgery;
import com.nvivx.vixhealthsystem.model.person.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @brief Unit tests for MedicalSpecialist.
 *
 * Verifies system role, employee type, specialty/license fields, and the two
 * domain operations that attach a prescription or a surgery to a patient's
 * medical record. Plain JUnit : no Spring context loaded.
 *
 * @see MedicalSpecialist
 */
class MedicalSpecialistTest {
    private MedicalSpecialist specialist;
    private Patient patient;
    private MedicalRecord medicalRecord;

    @BeforeEach
    void setUp() {
        specialist = new MedicalSpecialist();
        specialist.setId(1L);
        specialist.setName("Marco");
        specialist.setSurname("Rossi");
        specialist.setSpecialty("Cardiology");
        specialist.setLicenseNumber("LIC-CARD-001");

        patient = new Patient();
        patient.setId(100L);
        patient.setName("Test");
        patient.setSurname("Patient");

        medicalRecord = new MedicalRecord();
        medicalRecord.setId(1000L);
        patient.setMedicalRecord(medicalRecord);
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        assertEquals(1L, specialist.getId());
        assertEquals("Marco", specialist.getName());
        assertEquals("Rossi", specialist.getSurname());
        assertEquals("Cardiology", specialist.getSpecialty());
        assertEquals("LIC-CARD-001", specialist.getLicenseNumber());
    }

    @Test
    void getSystemRole_ShouldReturnMedicalSpecialistRole() {
        assertEquals(Role.ROLE_MEDICAL_SPECIALIST, specialist.getSystemRole());
    }

    @Test
    void getEmployeeType_ShouldReturnMedicalSpecialistType() {
        assertEquals(EmployeeType.MEDICAL_SPECIALIST, specialist.getEmployeeType());
    }

    @Test
    void appPrescriptionForPatient_ShouldAddPrescriptionToMedicalRecord() {
        Prescription prescription = new Prescription();
        prescription.setMedication("Amlodipine 5mg");
        prescription.setDateTime(LocalDateTime.now());

        specialist.appPrescriptionForPatient(patient, prescription);

        assertTrue(medicalRecord.getPrescriptions().contains(prescription));
        assertEquals(specialist, prescription.getMedicalSpecialist());
        assertEquals(medicalRecord, prescription.getMedicalRecord());
    }

    @Test
    void scheduleSurgeryForPatient_ShouldLinkSurgeryToPatientAndSpecialist() {
        Surgery surgery = new Surgery();
        surgery.setName("Appendectomy");
        surgery.setDateTime(LocalDateTime.now().plusDays(7));

        specialist.scheduleSurgeryForPatient(patient, surgery);

        assertTrue(medicalRecord.getSurgeries().contains(surgery));
        assertEquals(specialist, surgery.getMedicalSpecialist());
        assertEquals(medicalRecord, surgery.getMedicalRecord());
    }

    @Test
    void appointments_ShouldBeManageable() {
        assertTrue(specialist.getAppointments().isEmpty());

        // Appointments are transient, tested in AppointmentTest
        specialist.getAppointments().add(new Appointment());
        assertEquals(1, specialist.getAppointments().size());
    }
}