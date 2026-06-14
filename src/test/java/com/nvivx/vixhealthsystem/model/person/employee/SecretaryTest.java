package com.nvivx.vixhealthsystem.model.person.employee;

import com.nvivx.vixhealthsystem.model.enums.EmployeeType;
import com.nvivx.vixhealthsystem.model.enums.Role;
import com.nvivx.vixhealthsystem.model.facility.InternationRoom;
import com.nvivx.vixhealthsystem.model.medical.Appointment;
import com.nvivx.vixhealthsystem.model.person.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @brief Unit tests for Secretary.
 *
 * Verifies system role, employee type, front-office role field, and the
 * appointment-management and room-admission domain operations. Also guards the
 * over-capacity and absent-patient error paths. Plain JUnit — no Spring
 * context loaded.
 *
 * @see Secretary
 */
class SecretaryTest {
    private Secretary secretary;
    private Patient patient;
    private MedicalSpecialist specialist;
    private InternationRoom room;

    @BeforeEach
    void setUp() {
        secretary = new Secretary();
        secretary.setId(1L);
        secretary.setName("Sara");
        secretary.setSurname("Conti");
        secretary.setRole("Front Office");

        patient = new Patient();
        patient.setId(100L);
        patient.setName("Test");
        patient.setSurname("Patient");

        specialist = new MedicalSpecialist();
        specialist.setId(200L);
        specialist.setName("Dr");
        specialist.setSurname("Specialist");

        room = new InternationRoom("101", 2);
    }

    @Test
    void getSystemRole_ShouldReturnSecretaryRole() {
        assertEquals(Role.ROLE_SECRETARY, secretary.getSystemRole());
    }

    @Test
    void getEmployeeType_ShouldReturnSecretaryType() {
        assertEquals(EmployeeType.SECRETARY, secretary.getEmployeeType());
    }

    @Test
    void getRole_ShouldReturnSecretarySpecialization() {
        assertEquals("Front Office", secretary.getRole());
    }

    @Test
    void setRole_ShouldUpdateSpecialization() {
        secretary.setRole("Admissions");
        assertEquals("Admissions", secretary.getRole());
    }

    @Test
    void makeAppointmentForPatient_ShouldCreateAppointmentForPatient() {
        LocalDateTime appointmentTime = LocalDateTime.now().plusDays(3);

        Appointment appointment = secretary.makeAppointmentForPatient(patient, specialist, appointmentTime);

        assertNotNull(appointment);
        assertEquals(patient, appointment.getPatient());
        assertEquals(specialist, appointment.getMedicalSpecialist());
        assertTrue(patient.getAppointments().contains(appointment));
    }

    @Test
    void rescheduleAppointmentForPatient_ShouldUpdatePatientAppointment() {
        LocalDateTime originalTime = LocalDateTime.now().plusDays(3);
        LocalDateTime newTime = originalTime.plusDays(1);

        patient.makeAppointment(specialist, originalTime);
        secretary.rescheduleAppointmentForPatient(patient, originalTime, newTime);

        assertEquals(newTime, patient.getAppointments().get(0).getDateTime());
    }

    @Test
    void cancelAppointmentForPatient_ShouldRemovePatientAppointment() {
        LocalDateTime time = LocalDateTime.now().plusDays(3);
        patient.makeAppointment(specialist, time);
        assertEquals(1, patient.getAppointments().size());

        secretary.cancelAppointmentForPatient(patient, time);

        assertTrue(patient.getAppointments().isEmpty());
    }

    @Test
    void setPatientInRoom_ShouldAdmitPatientToRoom() throws Exception {
        secretary.setPatientInRoom(room, patient);

        assertTrue(room.hasPatient(patient));
        assertEquals(1, room.getPatients().size());
    }

    @Test
    void setPatientInRoom_ShouldThrowExceptionWhenRoomFull() throws Exception {
        Patient patient2 = new Patient();
        Patient patient3 = new Patient();

        secretary.setPatientInRoom(room, patient);
        secretary.setPatientInRoom(room, patient2);

        Exception exception = assertThrows(Exception.class, () -> {
            secretary.setPatientInRoom(room, patient3);
        });

        assertTrue(exception.getMessage().contains("limit reached"));
    }

    @Test
    void dismissPatient_ShouldRemovePatientFromRoom() throws Exception {
        secretary.setPatientInRoom(room, patient);
        assertTrue(room.hasPatient(patient));

        secretary.dismissPatient(room, patient);

        assertFalse(room.hasPatient(patient));
        assertTrue(room.getPatients().isEmpty());
    }

    @Test
    void dismissPatient_ShouldThrowExceptionWhenPatientNotInRoom() {
        Exception exception = assertThrows(Exception.class, () -> {
            secretary.dismissPatient(room, patient);
        });

        assertTrue(exception.getMessage().contains("No patient"));
    }
}