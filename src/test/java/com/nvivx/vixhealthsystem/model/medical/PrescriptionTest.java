package com.nvivx.vixhealthsystem.model.medical;

import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PrescriptionTest {
    private Prescription prescription;
    private MedicalSpecialist specialist;
    private MedicalRecord medicalRecord;

    @BeforeEach
    void setUp() {
        prescription = new Prescription();
        prescription.setId(1L);
        prescription.setMedication("Amlodipine 5mg");
        prescription.setDateTime(LocalDateTime.of(2024, 6, 10, 10, 30));

        specialist = new MedicalSpecialist();
        specialist.setId(10L);
        specialist.setName("Marco");
        specialist.setSurname("Rossi");

        medicalRecord = new MedicalRecord();
        medicalRecord.setId(100L);

        prescription.setMedicalSpecialist(specialist);
        prescription.setMedicalRecord(medicalRecord);
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        assertEquals(1L, prescription.getId());
        assertEquals("Amlodipine 5mg", prescription.getMedication());
        assertEquals(LocalDateTime.of(2024, 6, 10, 10, 30), prescription.getDateTime());
        assertEquals(specialist, prescription.getMedicalSpecialist());
        assertEquals(medicalRecord, prescription.getMedicalRecord());
    }

    @Test
    void parameterizedConstructor_ShouldInitializePrescription() {
        LocalDateTime now = LocalDateTime.now();
        Prescription newPrescription = new Prescription(now, "Ibuprofen 400mg");

        assertEquals(now, newPrescription.getDateTime());
        assertEquals("Ibuprofen 400mg", newPrescription.getMedication());
        assertNull(newPrescription.getId());
        assertNull(newPrescription.getMedicalSpecialist());
        assertNull(newPrescription.getMedicalRecord());
    }
}