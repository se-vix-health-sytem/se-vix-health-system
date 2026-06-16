package com.nvivx.vixhealthsystem.model.medical;

import com.nvivx.vixhealthsystem.model.facility.SpecializedRoom;
import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @brief Unit tests for Surgery.
 *
 * Verifies setter/getter correctness and the parameterized constructor that
 * initialises a surgery with a date-time, name, description, and room while
 * leaving medical record and specialist null. Plain JUnit : no Spring context
 * loaded.
 *
 * @see Surgery
 */
class SurgeryTest {
    private Surgery surgery;
    private SpecializedRoom room;
    private MedicalRecord medicalRecord;
    private MedicalSpecialist specialist;

    /** @brief Builds the fixture shared by all tests in this class. */
    @BeforeEach
    void setUp() {
        room = new SpecializedRoom("S101", "Surgery");
        medicalRecord = new MedicalRecord();
        specialist = new MedicalSpecialist();

        surgery = new Surgery();
        surgery.setId(1L);
        surgery.setName("Appendectomy");
        surgery.setDescription("Removal of appendix");
        surgery.setDateTime(LocalDateTime.of(2024, 6, 15, 9, 0));
        surgery.setSpecializedRoom(room);
        surgery.setMedicalRecord(medicalRecord);
        surgery.setMedicalSpecialist(specialist);
    }

    /**
     * Verifies that all surgery fields including room, medical record,
     *        and specialist associations round-trip without data loss.
     */
    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        assertEquals(1L, surgery.getId());
        assertEquals("Appendectomy", surgery.getName());
        assertEquals("Removal of appendix", surgery.getDescription());
        assertEquals(LocalDateTime.of(2024, 6, 15, 9, 0), surgery.getDateTime());
        assertEquals(room, surgery.getSpecializedRoom());
        assertEquals(medicalRecord, surgery.getMedicalRecord());
        assertEquals(specialist, surgery.getMedicalSpecialist());
    }

    /**
     * Verifies that the four-argument constructor stores the date-time,
     *        name, description, and room while leaving id and medical record null
     *        until the surgery is linked to a patient.
     */
    @Test
    void parameterizedConstructor_ShouldInitializeSurgery() {
        SpecializedRoom newRoom = new SpecializedRoom("S202", "Orthopedic");
        LocalDateTime time = LocalDateTime.now().plusDays(7);

        Surgery newSurgery = new Surgery(time, "Knee Arthroscopy", "Minimally invasive knee procedure", newRoom);

        assertEquals(time, newSurgery.getDateTime());
        assertEquals("Knee Arthroscopy", newSurgery.getName());
        assertEquals("Minimally invasive knee procedure", newSurgery.getDescription());
        assertEquals(newRoom, newSurgery.getSpecializedRoom());
        assertNull(newSurgery.getId());
        assertNull(newSurgery.getMedicalRecord());
    }
}