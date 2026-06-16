package com.nvivx.vixhealthsystem.model.medical;

import com.nvivx.vixhealthsystem.model.person.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @brief Unit tests for MedicalRecord.
 *
 * Verifies the parameterized constructor, all field setters/getters, and the
 * addCondition/addPrescription/addSurgery helpers that maintain bidirectional
 * back-references. Plain JUnit : no Spring context loaded.
 *
 * @see MedicalRecord
 */
class MedicalRecordTest {
    private MedicalRecord medicalRecord;
    private Patient patient;

    /** @brief Builds the fixture shared by all tests in this class. */
    @BeforeEach
    void setUp() {
        medicalRecord = new MedicalRecord(175.0f, 70.5f, "A+");
        medicalRecord.setId(100L);
        medicalRecord.setAllergies("Penicillin, Dust");
        medicalRecord.setVaccines("COVID-19, Tetanus");

        patient = new Patient();
        patient.setId(1L);
        patient.setMedicalRecord(medicalRecord);
        medicalRecord.setPatient(patient);
    }

    /**
     * Verifies that the three-argument constructor stores height, weight,
     *        and blood type in the appropriate fields.
     */
    @Test
    void constructor_ShouldInitializeHealthData() {
        MedicalRecord record = new MedicalRecord(180.0f, 75.0f, "O-");
        assertEquals(180.0f, record.getHeight());
        assertEquals(75.0f, record.getWeight());
        assertEquals("O-", record.getBloodType());
    }

    /**
     * Verifies that all health and identity fields round-trip through
     *        their setters and getters including the patient back-reference.
     */
    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        assertEquals(100L, medicalRecord.getId());
        assertEquals(175.0f, medicalRecord.getHeight());
        assertEquals(70.5f, medicalRecord.getWeight());
        assertEquals("A+", medicalRecord.getBloodType());
        assertEquals("Penicillin, Dust", medicalRecord.getAllergies());
        assertEquals("COVID-19, Tetanus", medicalRecord.getVaccines());
        assertEquals(patient, medicalRecord.getPatient());
    }

    /**
     * Verifies that adding a condition to the record also sets the
     *        condition's medicalRecord back-reference, maintaining consistency.
     */
    @Test
    void addCondition_ShouldAddConditionAndSetBackReference() {
        MedicalCondition condition = new MedicalCondition();
        condition.setName("Hypertension");
        condition.setDescription("High blood pressure");
        condition.setDateOfDiagnosis(LocalDate.now());

        medicalRecord.addCondition(condition);

        assertTrue(medicalRecord.getConditions().contains(condition));
        assertEquals(medicalRecord, condition.getMedicalRecord());
    }

    /**
     * Verifies that adding a prescription to the record also sets the
     *        prescription's medicalRecord back-reference.
     */
    @Test
    void addPrescription_ShouldAddPrescriptionAndSetBackReference() {
        Prescription prescription = new Prescription();
        prescription.setMedication("Amlodipine 5mg");
        prescription.setDateTime(LocalDateTime.now());

        medicalRecord.addPrescription(prescription);

        assertTrue(medicalRecord.getPrescriptions().contains(prescription));
        assertEquals(medicalRecord, prescription.getMedicalRecord());
    }

    /**
     * Verifies that scheduling a surgery via the record also sets the
     *        surgery's medicalRecord back-reference.
     */
    @Test
    void addSurgery_ShouldAddSurgeryAndSetBackReference() {
        Surgery surgery = new Surgery();
        surgery.setName("Appendectomy");
        surgery.setDateTime(LocalDateTime.now().plusDays(7));

        medicalRecord.addSurgery(surgery);

        assertTrue(medicalRecord.getSurgeries().contains(surgery));
        assertEquals(medicalRecord, surgery.getMedicalRecord());
    }

    /**
     * Verifies that conditions can be added directly to the mutable list
     *        returned by getConditions().
     */
    @Test
    void conditions_ShouldBeModifiable() {
        assertTrue(medicalRecord.getConditions().isEmpty());

        MedicalCondition c1 = new MedicalCondition();
        MedicalCondition c2 = new MedicalCondition();
        medicalRecord.getConditions().add(c1);
        medicalRecord.getConditions().add(c2);

        assertEquals(2, medicalRecord.getConditions().size());
    }

    /**
     * Verifies that the conditions list can be replaced wholesale,
     *        supporting JPA hydration from the database.
     */
    @Test
    void setConditions_ShouldReplaceList() {
        MedicalCondition condition = new MedicalCondition();
        medicalRecord.setConditions(java.util.List.of(condition));

        assertEquals(1, medicalRecord.getConditions().size());
        assertEquals(condition, medicalRecord.getConditions().get(0));
    }
}