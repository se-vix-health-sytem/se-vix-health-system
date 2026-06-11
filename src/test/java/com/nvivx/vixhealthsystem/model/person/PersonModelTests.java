package com.nvivx.vixhealthsystem.model.person;

import com.nvivx.vixhealthsystem.model.facility.InternationRoom;
import com.nvivx.vixhealthsystem.model.medical.Prescription;
import com.nvivx.vixhealthsystem.model.person.employee.*;
import com.nvivx.vixhealthsystem.model.medical.Appointment;
import com.nvivx.vixhealthsystem.model.medical.MedicalRecord;
import com.nvivx.vixhealthsystem.model.resource.Resource;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class PersonModelTests {

    // ========== Person Tests ==========
    @Test
    void testPerson() {
        TestPerson person = new TestPerson();
        person.setName("John");
        person.setSurname("Doe");
        person.setBirthDate(LocalDate.of(1990, 5, 15));
        person.setBirthPlace("New York");
        person.setGender('M');
        person.setEmail("john@example.com");
        person.setPhoneNumber("+1 555-1234");

        assertEquals("John", person.getName());
        assertEquals("Doe", person.getSurname());
        assertEquals(LocalDate.of(1990, 5, 15), person.getBirthDate());
        assertEquals("New York", person.getBirthPlace());
        assertEquals('M', person.getGender());
        assertEquals("john@example.com", person.getEmail());
        assertEquals("+1 555-1234", person.getPhoneNumber());

        int expectedAge = LocalDate.now().getYear() - 1990;
        assertEquals(expectedAge, person.getAge());
    }

    // ========== Patient Tests ==========
    @Test
    void testPatient() {
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setName("Mario");
        patient.setSurname("Rossi");
        patient.setFiscalCode("RSSMRA80A01F205X");

        assertEquals(1L, patient.getId());
        assertEquals("Mario", patient.getName());
        assertEquals("Rossi", patient.getSurname());
        assertEquals("RSSMRA80A01F205X", patient.getFiscalCode());

        MedicalSpecialist specialist = new MedicalSpecialist();
        LocalDateTime dt = LocalDateTime.of(2024, 12, 20, 14, 0);

        Appointment apt = patient.makeAppointment(specialist, dt);
        assertEquals(patient, apt.getPatient());
        assertEquals(specialist, apt.getMedicalSpecialist());
        assertEquals(1, patient.getAppointments().size());

        patient.rescheduleAppointment(dt, dt.plusDays(1));
        assertEquals(dt.plusDays(1), apt.getDateTime());

        patient.cancelAppointment(dt.plusDays(1));
        assertEquals(0, patient.getAppointments().size());

        MedicalRecord record = new MedicalRecord();
        patient.setMedicalRecord(record);
        patient.deleteAccount();
        assertNull(patient.getMedicalRecord());
    }

    // ========== Employee Tests ==========
    @Test
    void testEmployee() {
        TestEmployee emp = new TestEmployee();
        emp.setId(50L);
        emp.setHireDate(LocalDate.of(2020, 6, 1));

        assertEquals(50L, emp.getId());
        assertEquals(LocalDate.of(2020, 6, 1), emp.getHireDate());
    }

    // ========== MedicalSpecialist Tests ==========
    @Test
    void testMedicalSpecialist() {
        MedicalSpecialist specialist = new MedicalSpecialist();
        specialist.setSpecialty("Cardiology");
        specialist.setLicenseNumber("LIC-12345");

        assertEquals("Cardiology", specialist.getSpecialty());
        assertEquals("LIC-12345", specialist.getLicenseNumber());

        Patient patient = new Patient();
        Prescription presc = new Prescription();
        specialist.appPrescriptionForPatient(patient, presc); // Should not throw
    }

    // ========== Secretary Tests ==========
    @Test
    void testSecretary() {
        Secretary secretary = new Secretary();
        secretary.setRole("Front Office");
        assertEquals("Front Office", secretary.getRole());

        Patient patient = new Patient();
        MedicalSpecialist specialist = new MedicalSpecialist();
        InternationRoom room = new InternationRoom("101", 2);

        secretary.makeAppointmentForPatient(patient, specialist, LocalDateTime.now());
        secretary.rescheduleAppointmentForPatient(patient, LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        secretary.cancelAppointmentForPatient(patient, LocalDateTime.now());
        secretary.getRoomAvailability();
        secretary.setPatientInRoom(room, patient);
        secretary.dismissPatient(patient);
    }

    // ========== Technician Tests ==========
    @Test
    void testTechnician() {
        Technician tech = new Technician();
        assertNull(tech.getMachineList());
        assertNull(tech.getFaultyMachineList());

        Employee emp = new MedicalSpecialist();
        tech.credentialsRecovery(emp); // Should not throw
    }

    // ========== Buyer Tests ==========
    @Test
    void testBuyer() {
        Buyer buyer = new Buyer();
        Resource resource = new Resource("Syringes", "Disposable", BigDecimal.valueOf(0.50));
        buyer.addResource(resource); // Should not throw
    }

    // ========== StaffManager Tests ==========
    @Test
    void testStaffManager() {
        StaffManager manager = new StaffManager();
        Employee emp = new MedicalSpecialist();

        assertNull(manager.createAccountForEmployee());
        manager.deleteEmployeeAccount(emp);
        manager.credentialsRecovery(emp);
    }

    // Helper classes
    private static class TestPerson extends Person {}
    private static class TestEmployee extends Employee {}
}