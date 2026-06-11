package com.nvivx.vixhealthsystem.model.medical;

import com.nvivx.vixhealthsystem.model.person.Patient;
import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import com.nvivx.vixhealthsystem.model.facility.SpecializedRoom;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class MedicalModelTests {

    // ========== Appointment Tests ==========
    @Test
    void testAppointment() {
        LocalDateTime dt = LocalDateTime.of(2024, 12, 15, 10, 30);
        Appointment apt = new Appointment(1, dt, 30, "Checkup");

        assertEquals(1, apt.getId());
        assertEquals(dt, apt.getDateTime());
        assertEquals(30, apt.getDuration());
        assertEquals("Checkup", apt.getNotes());
        assertEquals("CONFIRMED", apt.getStatus());

        apt.setStatus("CANCELLED");
        assertFalse(apt.isActive());
        assertFalse(apt.isCancellable());

        Patient patient = new Patient();
        MedicalSpecialist specialist = new MedicalSpecialist();
        Appointment apt2 = new Appointment(dt, 45, "Follow-up", patient, specialist);
        assertEquals(patient, apt2.getPatient());
        assertEquals(specialist, apt2.getMedicalSpecialist());
    }

    // ========== MedicalRecord Tests ==========
    @Test
    void testMedicalRecord() {
        MedicalRecord record = new MedicalRecord(175.5f, 70.2f, "O+");
        assertEquals(175.5f, record.getHeight());
        assertEquals(70.2f, record.getWeight());
        assertEquals("O+", record.getBloodType());

        record.setAllergies("Penicillin");
        record.setVaccines("COVID-19, Flu");
        assertEquals("Penicillin", record.getAllergies());
        assertEquals("COVID-19, Flu", record.getVaccines());

        MedicalCondition condition = new MedicalCondition();
        record.addCondition(condition);
        assertEquals(1, record.getConditions().size());
        assertEquals(record, condition.getMedicalRecord());

        Prescription prescription = new Prescription();
        record.addPrescription(prescription);
        assertEquals(1, record.getPrescriptions().size());

        Surgery surgery = new Surgery();
        record.addSurgery(surgery);
        assertEquals(1, record.getSurgeries().size());
    }

    // ========== MedicalCondition Tests ==========
    @Test
    void testMedicalCondition() {
        LocalDate diagnosisDate = LocalDate.of(2024, 1, 15);
        MedicalCondition condition = new MedicalCondition(
                "Hypertension", diagnosisDate, "Cardiovascular",
                "High blood pressure", "Lifestyle changes"
        );

        assertEquals("Hypertension", condition.getName());
        assertEquals(diagnosisDate, condition.getDateOfDiagnosis());
        assertEquals("Cardiovascular", condition.getType());
        assertEquals("High blood pressure", condition.getDescription());
        assertEquals("Lifestyle changes", condition.getTreatment());

        condition.setId(1L);
        assertEquals(1L, condition.getId());
    }

    // ========== Prescription Tests ==========
    @Test
    void testPrescription() {
        LocalDateTime dt = LocalDateTime.of(2024, 5, 15, 14, 30);
        Prescription presc = new Prescription(dt, "Amoxicillin 500mg");

        assertEquals(dt, presc.getDateTime());
        assertEquals("Amoxicillin 500mg", presc.getMedication());

        MedicalSpecialist specialist = new MedicalSpecialist();
        presc.setMedicalSpecialist(specialist);
        assertEquals(specialist, presc.getMedicalSpecialist());

        presc.setId(100L);
        assertEquals(100L, presc.getId());
    }

    // ========== Surgery Tests ==========
    @Test
    void testSurgery() {
        LocalDateTime dt = LocalDateTime.of(2024, 6, 10, 9, 0);
        SpecializedRoom room = new SpecializedRoom("OR1", "Surgery");
        Surgery surgery = new Surgery(dt, "Appendectomy", "Removal of appendix", room);

        assertEquals(dt, surgery.getDateTime());
        assertEquals("Appendectomy", surgery.getName());
        assertEquals("Removal of appendix", surgery.getDescription());
        assertEquals(room, surgery.getSpecializedRoom());

        surgery.setId(50L);
        assertEquals(50L, surgery.getId());
    }
}