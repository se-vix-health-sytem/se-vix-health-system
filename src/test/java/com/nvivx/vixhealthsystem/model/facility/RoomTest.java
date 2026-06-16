package com.nvivx.vixhealthsystem.model.facility;

import com.nvivx.vixhealthsystem.model.enums.BedStatus;
import com.nvivx.vixhealthsystem.model.person.Patient;
import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @brief Marker base class for Room hierarchy tests.
 *
 * Room is abstract; concrete subclasses InternationRoomTest, OfficeTest, and
 * SpecializedRoomTest cover its concrete implementations. Plain JUnit : no
 * Spring context loaded.
 *
 * @see Room
 */
abstract class RoomTest {
    // Room is abstract, testing through concrete subclasses
}

/**
 * @brief Unit tests for InternationRoom.
 *
 * Covers bed counting, patient admission and removal, capacity enforcement,
 * bed-status transitions, and list replacement. Plain JUnit : no Spring
 * context loaded.
 *
 * @see InternationRoom
 */
class InternationRoomTest {
    private InternationRoom room;
    private Patient patient1;
    private Patient patient2;

    /** @brief Builds the fixture shared by all tests in this class. */
    @BeforeEach
    void setUp() {
        room = new InternationRoom("A101", 3);
        patient1 = new Patient();
        patient1.setId(1L);
        patient1.setName("Mario");
        patient1.setSurname("Rossi");

        patient2 = new Patient();
        patient2.setId(2L);
        patient2.setName("Laura");
        patient2.setSurname("Bianchi");
    }

    /**
     * Verifies that a new InternationRoom starts with the given number
     *        and total bed count, with an empty patient list.
     */
    @Test
    void constructor_ShouldInitializeWithNumberAndBeds() {
        InternationRoom newRoom = new InternationRoom("B202", 2);
        assertEquals("B202", newRoom.getNumber());
        assertEquals(2, newRoom.getTotalNBeds());
        assertTrue(newRoom.getPatients().isEmpty());
    }

    /**
     * Verifies that the total bed count reflects the value given at
     *        construction and is never affected by occupancy.
     */
    @Test
    void getTotalNBeds_ShouldReturnCorrectCount() {
        assertEquals(3, room.getTotalNBeds());
    }

    /**
     * Verifies that free bed count decreases by one for every patient
     *        admitted, giving an accurate real-time availability figure.
     */
    @Test
    void getNFreeBeds_ShouldReturnCorrectFreeBeds() throws Exception {
        assertEquals(3, room.getNFreeBeds());

        room.addPatient(patient1);
        assertEquals(2, room.getNFreeBeds());

        room.addPatient(patient2);
        assertEquals(1, room.getNFreeBeds());
    }

    /**
     * Verifies that adding a patient within capacity succeeds and the
     *        patient appears in the room's list.
     */
    @Test
    void addPatient_ShouldAddPatientSuccessfully() throws Exception {
        room.addPatient(patient1);
        assertTrue(room.hasPatient(patient1));
        assertEquals(1, room.getPatients().size());
    }

    /**
     * Verifies that adding a fourth patient to a three-bed room throws
     *        an exception, preventing overbooking.
     */
    @Test
    void addPatient_ShouldThrowExceptionWhenRoomFull() throws Exception {
        room.addPatient(patient1);
        room.addPatient(patient2);
        room.addPatient(new Patient()); // third patient

        Patient extraPatient = new Patient();
        Exception exception = assertThrows(Exception.class, () -> {
            room.addPatient(extraPatient);
        });

        assertTrue(exception.getMessage().contains("Patient limit reached"));
    }

    /**
     * Verifies that removing an admitted patient clears them from the
     *        list and leaves the room empty.
     */
    @Test
    void removePatient_ShouldRemovePatientSuccessfully() throws Exception {
        room.addPatient(patient1);
        assertTrue(room.hasPatient(patient1));

        room.removePatient(patient1);
        assertFalse(room.hasPatient(patient1));
        assertTrue(room.getPatients().isEmpty());
    }

    /**
     * Verifies that removing a patient who is not in the room throws an
     *        exception, preventing silent no-ops that could mask workflow bugs.
     */
    @Test
    void removePatient_ShouldThrowExceptionWhenPatientNotPresent() {
        Exception exception = assertThrows(Exception.class, () -> {
            room.removePatient(patient1);
        });

        assertTrue(exception.getMessage().contains("No patient"));
    }

    /**
     * Verifies that hasPatient distinguishes between admitted and
     *        non-admitted patients, returning true only for those currently in
     *        the room.
     */
    @Test
    void hasPatient_ShouldReturnTrueOnlyForAdmittedPatients() throws Exception {
        assertFalse(room.hasPatient(patient1));

        room.addPatient(patient1);
        assertTrue(room.hasPatient(patient1));
        assertFalse(room.hasPatient(patient2));
    }

    /**
     * Verifies that BedStatus transitions from FREE to OCCUPIED only
     *        when every bed is taken, so the summary flag matches actual capacity.
     */
    @Test
    void getBedStatus_ShouldReturnFreeWhenBedsAvailable() throws Exception {
        assertEquals(BedStatus.FREE, room.getBedStatus());

        room.addPatient(patient1);
        room.addPatient(patient2);
        assertEquals(BedStatus.FREE, room.getBedStatus());

        room.addPatient(new Patient());
        assertEquals(BedStatus.OCCUPIED, room.getBedStatus());
    }

    /**
     * Verifies that the patient list can be replaced wholesale, which
     *        is used when hydrating the entity from the database.
     */
    @Test
    void setPatients_ShouldReplacePatientList() {
        ArrayList<Patient> newList = new ArrayList<>();
        newList.add(patient1);
        room.setPatients(newList);

        assertEquals(1, room.getPatients().size());
        assertTrue(room.getPatients().contains(patient1));
    }
}

/**
 * @brief Unit tests for Office.
 *
 * Verifies that an office can be constructed with an assigned specialist, that
 * the employee can be set via the assignEmployee helper, and that the setter
 * replaces the previous occupant. Plain JUnit : no Spring context loaded.
 *
 * @see Office
 */
class OfficeTest {
    private Office office;
    private MedicalSpecialist specialist;

    /** @brief Builds the fixture shared by all tests in this class. */
    @BeforeEach
    void setUp() {
        specialist = new MedicalSpecialist();
        specialist.setId(1L);
        specialist.setName("Marco");
        specialist.setSurname("Rossi");

        office = new Office("O101", specialist);
    }

    /**
     * Verifies that office number and initial occupant are both stored
     *        when using the two-argument constructor.
     */
    @Test
    void constructor_ShouldInitializeWithNumberAndEmployee() {
        assertEquals("O101", office.getNumber());
        assertEquals(specialist, office.getEmployee());
    }

    /**
     * Verifies that assignEmployee sets the occupant on an empty office,
     *        confirming the helper behaves identically to the setter.
     */
    @Test
    void assignEmployee_ShouldAssignEmployeeToOffice() {
        Office newOffice = new Office();
        newOffice.setNumber("O102");

        assertNull(newOffice.getEmployee());
        newOffice.assignEmployee(specialist);
        assertEquals(specialist, newOffice.getEmployee());
    }

    /**
     * Verifies that the employee setter replaces the current occupant,
     *        supporting office reassignment scenarios.
     */
    @Test
    void setEmployee_ShouldUpdateEmployee() {
        MedicalSpecialist anotherSpecialist = new MedicalSpecialist();
        anotherSpecialist.setId(2L);

        office.setEmployee(anotherSpecialist);
        assertEquals(anotherSpecialist, office.getEmployee());
    }

    /**
     * Verifies that getEmployee returns the specialist set during
     *        construction without modification.
     */
    @Test
    void getEmployee_ShouldReturnCorrectEmployee() {
        assertEquals(specialist, office.getEmployee());
    }
}

/**
 * @brief Unit tests for SpecializedRoom.
 *
 * Verifies that the room number and specialization are stored at construction
 * and that the specialization can be updated to reflect room repurposing.
 * Plain JUnit : no Spring context loaded.
 *
 * @see SpecializedRoom
 */
class SpecializedRoomTest {
    private SpecializedRoom room;

    /** @brief Builds the fixture shared by all tests in this class. */
    @BeforeEach
    void setUp() {
        room = new SpecializedRoom("S201", "Radiology");
    }

    /**
     * Verifies that the room number and specialization supplied at
     *        construction are immediately accessible via getters.
     */
    @Test
    void constructor_ShouldInitializeWithNumberAndSpecialization() {
        assertEquals("S201", room.getNumber());
        assertEquals("Radiology", room.getSpecialization());
    }

    /**
     * Verifies that the specialization can be updated after construction,
     *        supporting room repurposing without recreating the entity.
     */
    @Test
    void setSpecialization_ShouldUpdateSpecialization() {
        room.setSpecialization("MRI");
        assertEquals("MRI", room.getSpecialization());
    }

    /**
     * Verifies that getSpecialization returns the value set during
     *        construction without any transformation.
     */
    @Test
    void getSpecialization_ShouldReturnCorrectValue() {
        assertEquals("Radiology", room.getSpecialization());
    }
}