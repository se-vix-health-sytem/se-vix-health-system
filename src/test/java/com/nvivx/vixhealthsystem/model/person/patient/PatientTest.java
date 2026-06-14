package com.nvivx.vixhealthsystem.model.person.patient;

import com.nvivx.vixhealthsystem.model.enums.Role;
import com.nvivx.vixhealthsystem.model.medical.Appointment;
import com.nvivx.vixhealthsystem.model.medical.MedicalRecord;
import com.nvivx.vixhealthsystem.model.person.Patient;
import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @brief Unit tests for Patient using plain JUnit (no Spring context).
 * Covers field getters/setters, makeAppointment, rescheduleAppointment, cancelAppointment,
 * deleteAccount, getSystemRole, and the modifiability of the appointments list.
 */
class PatientTest {
    private Patient patient;
    private MedicalSpecialist specialist;
    private MedicalRecord medicalRecord;

    @BeforeEach
    void setUp() {
        patient = new Patient();
        patient.setId(1L);
        patient.setName("Mario");
        patient.setSurname("Rossi");
        patient.setFiscalCode("RSSMRA80A01F205X");

        medicalRecord = new MedicalRecord();
        medicalRecord.setId(100L);
        patient.setMedicalRecord(medicalRecord);

        specialist = new MedicalSpecialist();
        specialist.setId(10L);
        specialist.setName("Giovanni");
        specialist.setSurname("Bianchi");
        specialist.setSpecialty("Cardiology");
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        assertEquals(1L, patient.getId());
        assertEquals("Mario", patient.getName());
        assertEquals("Rossi", patient.getSurname());
        assertEquals("RSSMRA80A01F205X", patient.getFiscalCode());
        assertEquals(medicalRecord, patient.getMedicalRecord());
    }

    @Test
    void makeAppointment_ShouldCreateValidAppointment() {
        LocalDateTime appointmentTime = LocalDateTime.now().plusDays(3);
        Appointment appointment = patient.makeAppointment(specialist, appointmentTime);

        assertNotNull(appointment);
        assertEquals(patient, appointment.getPatient());
        assertEquals(specialist, appointment.getMedicalSpecialist());
        assertTrue(patient.getAppointments().contains(appointment));
    }

    @Test
    void rescheduleAppointment_ShouldUpdateDateTime() {
        LocalDateTime originalTime = LocalDateTime.now().plusDays(3);
        LocalDateTime newTime = originalTime.plusDays(1);

        patient.makeAppointment(specialist, originalTime);
        patient.rescheduleAppointment(originalTime, newTime);

        Appointment updated = patient.getAppointments().get(0);
        assertEquals(newTime, updated.getDateTime());
    }

    @Test
    void rescheduleAppointment_ShouldOnlyUpdateFirstMatching() {
        LocalDateTime time1 = LocalDateTime.now().plusDays(3);
        LocalDateTime time2 = LocalDateTime.now().plusDays(4);
        LocalDateTime newTime = LocalDateTime.now().plusDays(10);

        patient.makeAppointment(specialist, time1);
        patient.makeAppointment(specialist, time2);
        patient.rescheduleAppointment(time1, newTime);

        List<Appointment> appointments = patient.getAppointments();
        assertEquals(newTime, appointments.get(0).getDateTime());
        assertEquals(time2, appointments.get(1).getDateTime());
    }

    @Test
    void cancelAppointment_ShouldRemoveAppointment() {
        LocalDateTime time = LocalDateTime.now().plusDays(3);
        patient.makeAppointment(specialist, time);
        assertEquals(1, patient.getAppointments().size());

        patient.cancelAppointment(time);
        assertTrue(patient.getAppointments().isEmpty());
    }

    @Test
    void cancelAppointment_ShouldOnlyRemoveMatchingTime() {
        LocalDateTime time1 = LocalDateTime.now().plusDays(3);
        LocalDateTime time2 = LocalDateTime.now().plusDays(4);

        patient.makeAppointment(specialist, time1);
        patient.makeAppointment(specialist, time2);

        patient.cancelAppointment(time1);

        assertEquals(1, patient.getAppointments().size());
        assertEquals(time2, patient.getAppointments().get(0).getDateTime());
    }

    @Test
    void deleteAccount_ShouldClearAppointmentsAndNullifyMedicalRecord() {
        patient.makeAppointment(specialist, LocalDateTime.now().plusDays(1));
        patient.makeAppointment(specialist, LocalDateTime.now().plusDays(2));
        assertEquals(2, patient.getAppointments().size());
        assertNotNull(patient.getMedicalRecord());

        patient.deleteAccount();

        assertTrue(patient.getAppointments().isEmpty());
        assertNull(patient.getMedicalRecord());
    }

    @Test
    void getSystemRole_ShouldReturnPatientRole() {
        assertEquals(Role.ROLE_PATIENT, patient.getSystemRole());
    }

    @Test
    void appointments_ShouldBeModifiable() {
        assertTrue(patient.getAppointments().isEmpty());

        Appointment apt1 = new Appointment();
        Appointment apt2 = new Appointment();
        patient.getAppointments().add(apt1);
        patient.getAppointments().add(apt2);

        assertEquals(2, patient.getAppointments().size());
    }
}