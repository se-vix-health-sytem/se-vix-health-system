package com.nvivx.vixhealthsystem.model.medical;

import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @brief Unit tests for Prescription.
 *
 * Verifies setter/getter correctness and the parameterized constructor that
 * creates a prescription with medication and timestamp but no associations.
 * Plain JUnit : no Spring context loaded.
 *
 * @see Prescription
 */
class PrescriptionTest {
    private Prescription prescription;
    private MedicalSpecialist specialist;
    private MedicalRecord medicalRecord;

    /** @brief Builds the fixture shared by all tests in this class. */
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

    /**
     * Verifies that all prescription fields including specialist and
     *        medical record associations round-trip without data loss.
     */
    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        assertEquals(1L, prescription.getId());
        assertEquals("Amlodipine 5mg", prescription.getMedication());
        assertEquals(LocalDateTime.of(2024, 6, 10, 10, 30), prescription.getDateTime());
        assertEquals(specialist, prescription.getMedicalSpecialist());
        assertEquals(medicalRecord, prescription.getMedicalRecord());
    }

    /**
     * Verifies that the two-argument constructor stores the timestamp
     *        and medication while leaving id and associations null, so the
     *        caller can attach them separately.
     */
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