package com.nvivx.vixhealthsystem.model.medical;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class MedicalConditionTest {
    private MedicalCondition condition;

    @BeforeEach
    void setUp() {
        condition = new MedicalCondition();
        condition.setId(1L);
        condition.setName("Hypertension");
        condition.setDescription("High blood pressure requiring medication");
        condition.setDateOfDiagnosis(LocalDate.of(2024, 1, 15));
        condition.setType("Chronic");
        condition.setTreatment("Lifestyle changes and medication");
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        assertEquals(1L, condition.getId());
        assertEquals("Hypertension", condition.getName());
        assertEquals("High blood pressure requiring medication", condition.getDescription());
        assertEquals(LocalDate.of(2024, 1, 15), condition.getDateOfDiagnosis());
        assertEquals("Chronic", condition.getType());
        assertEquals("Lifestyle changes and medication", condition.getTreatment());
    }

    @Test
    void parameterizedConstructor_ShouldInitializeCondition() {
        MedicalCondition newCondition = new MedicalCondition(
                "Diabetes",
                LocalDate.of(2024, 2, 10),
                "Metabolic",
                "Type 2 diabetes",
                "Metformin"
        );

        assertEquals("Diabetes", newCondition.getName());
        assertEquals(LocalDate.of(2024, 2, 10), newCondition.getDateOfDiagnosis());
        assertEquals("Metabolic", newCondition.getType());
        assertEquals("Type 2 diabetes", newCondition.getDescription());
        assertEquals("Metformin", newCondition.getTreatment());
    }

    @Test
    void setMedicalRecord_ShouldLinkToMedicalRecord() {
        MedicalRecord record = new MedicalRecord();
        condition.setMedicalRecord(record);
        assertEquals(record, condition.getMedicalRecord());
    }

    @Test
    void nullFields_ShouldBeAllowed() {
        MedicalCondition emptyCondition = new MedicalCondition();
        assertNull(emptyCondition.getId());
        assertNull(emptyCondition.getName());
        assertNull(emptyCondition.getDescription());
        assertNull(emptyCondition.getDateOfDiagnosis());
        assertNull(emptyCondition.getType());
        assertNull(emptyCondition.getTreatment());
    }
}