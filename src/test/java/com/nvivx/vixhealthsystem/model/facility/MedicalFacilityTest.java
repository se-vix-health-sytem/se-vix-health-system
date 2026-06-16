package com.nvivx.vixhealthsystem.model.facility;

import com.nvivx.vixhealthsystem.model.person.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @brief Unit tests for MedicalFacility and Hospital.
 *
 * MedicalFacilityTest covers the base entity's constructor and collection
 * management. HospitalTest covers the domain logic for inpatient room queries,
 * patient admission, and dismissal. Plain JUnit : no Spring context loaded.
 *
 * @see MedicalFacility
 * @see Hospital
 */
class MedicalFacilityTest {
    private MedicalFacility facility;

    /** @brief Builds a MedicalFacility with a location, email, and phone number. */
    @BeforeEach
    void setUp() {
        Location location = new Location(45.4642, 9.1900);
        facility = new MedicalFacility("VIX Central Hospital", location, "info@vixhealth.it", "+39 0461 000001");
    }

    /**
     * Verifies that the four-argument constructor stores name, location,
     *        email, and phone number in the correct fields.
     */
    @Test
    void constructor_ShouldInitializeFacility() {
        assertEquals("VIX Central Hospital", facility.getName());
        assertEquals(45.4642, facility.getLocation().getLatitude());
        assertEquals(9.1900, facility.getLocation().getLongitude());
        assertEquals("info@vixhealth.it", facility.getEmail());
        assertEquals("+39 0461 000001", facility.getPhoneNumber());
    }

    /**
     * Verifies that id, name, email, phone, and location can all be
     *        overwritten after construction and read back correctly.
     */
    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        facility.setId(100L);
        facility.setName("Updated Name");
        facility.setEmail("new@email.com");
        facility.setPhoneNumber("+39 123456789");

        Location newLoc = new Location(46.0, 11.0);
        facility.setLocation(newLoc);

        assertEquals(100L, facility.getId());
        assertEquals("Updated Name", facility.getName());
        assertEquals("new@email.com", facility.getEmail());
        assertEquals("+39 123456789", facility.getPhoneNumber());
        assertEquals(newLoc, facility.getLocation());
    }

    /**
     * Verifies that rooms can be added to the facility's collection and
     *        retrieved in the expected order.
     */
    @Test
    void rooms_ShouldBeManageable() {
        assertTrue(facility.getRooms().isEmpty());

        InternationRoom room1 = new InternationRoom("101", 2);
        room1.setMedicalFacility(facility);
        facility.getRooms().add(room1);

        assertEquals(1, facility.getRooms().size());
        assertEquals(room1, facility.getRooms().get(0));
    }

    /**
     * Verifies that departments can be added to the facility and that
     *        each department carries the expected name.
     */
    @Test
    void departments_ShouldBeManageable() {
        assertTrue(facility.getDepartments().isEmpty());

        Department dept = new Department();
        dept.setName("Cardiology");
        dept.setMedicalFacility(facility);
        facility.getDepartments().add(dept);

        assertEquals(1, facility.getDepartments().size());
        assertEquals("Cardiology", facility.getDepartments().get(0).getName());
    }
}

/**
 * @brief Unit tests for Hospital domain logic.
 *
 * Focuses on inpatient room filtering, bed availability queries, patient
 * admission, and patient dismissal. Plain JUnit : no Spring context loaded.
 *
 * @see Hospital
 */
class HospitalTest {
    private Hospital hospital;
    private InternationRoom room1;
    private InternationRoom room2;
    private Patient patient;

    /** @brief Builds the fixture shared by all tests in this class. */
    @BeforeEach
    void setUp() {
        hospital = new Hospital();
        hospital.setName("Main Hospital");

        room1 = new InternationRoom("101", 2);  // 2 beds
        room1.setMedicalFacility(hospital);
        room2 = new InternationRoom("102", 1);  // 1 bed
        room2.setMedicalFacility(hospital);

        List<Room> rooms = new ArrayList<>();
        rooms.add(room1);
        rooms.add(room2);
        hospital.setRooms(rooms);

        patient = new Patient();
        patient.setId(1L);
        patient.setName("Test Patient");
    }

    /**
     * Verifies that only InternationRoom instances are returned when
     *        querying for patient rooms, excluding offices and other room types.
     */
    @Test
    void getRoomsForPatients_ShouldReturnOnlyInpatientRooms() {
        // Add a non-inpatient room
        Office office = new Office("O1", null);
        office.setMedicalFacility(hospital);
        hospital.getRooms().add(office);

        List<InternationRoom> patientRooms = hospital.getRoomsForPatients();

        assertEquals(2, patientRooms.size());
        assertTrue(patientRooms.contains(room1));
        assertTrue(patientRooms.contains(room2));
        assertFalse(patientRooms.contains(office));
    }

    /**
     * Verifies that only rooms with at least one free bed appear in the
     *        availability query, so secretaries are never directed to a full room.
     */
    @Test
    void getFreeRoomsForPatients_ShouldReturnOnlyRoomsWithFreeBeds() throws Exception {
        // Fill room1 completely (2 patients for 2 beds)
        Patient patient2 = new Patient();
        patient2.setId(2L);
        patient2.setName("Second");
        patient2.setSurname("Patient");

        room1.addPatient(patient);
        room1.addPatient(patient2);
        // room1 now has 0 free beds

        // room2 has 1 bed and is empty → 1 free bed

        List<InternationRoom> freeRooms = hospital.getFreeRoomsForPatients();

        assertEquals(1, freeRooms.size());
        assertEquals(room2, freeRooms.get(0));
    }

    /**
     * Verifies that an empty list is returned when every bed in the
     *        hospital is taken, making the overcapacity state detectable.
     */
    @Test
    void getFreeRoomsForPatients_WhenAllRoomsFull_ShouldReturnEmpty() throws Exception {
        // Fill room1 (2 beds)
        Patient patient2 = new Patient();
        patient2.setId(2L);
        Patient patient3 = new Patient();
        patient3.setId(3L);

        room1.addPatient(patient);
        room1.addPatient(patient2);

        // Fill room2 (1 bed)
        room2.addPatient(patient3);

        List<InternationRoom> freeRooms = hospital.getFreeRoomsForPatients();

        assertTrue(freeRooms.isEmpty());
    }

    /**
     * Verifies that the correct room is returned when a patient is
     *        currently admitted to it.
     */
    @Test
    void findPatientInRoom_ShouldFindCorrectRoom() throws Exception {
        room1.addPatient(patient);

        InternationRoom found = hospital.findPatientInRoom(patient);
        assertEquals(room1, found);
    }

    /**
     * Verifies that searching for a patient who has not been admitted
     *        raises an exception, guarding against silent null returns that
     *        could mask data errors.
     */
    @Test
    void findPatientInRoom_ShouldThrowExceptionWhenPatientNotAdmitted() {
        Exception exception = assertThrows(Exception.class, () -> {
            hospital.findPatientInRoom(patient);
        });

        assertTrue(exception.getMessage().contains("No patient"));
    }

    /**
     * Verifies that admitting a patient places them in the room and
     *        reduces the free bed count accordingly.
     */
    @Test
    void internPatient_ShouldAdmitPatientToRoom() throws Exception {
        hospital.internPatient(patient, room2);

        assertTrue(room2.hasPatient(patient));
        assertEquals(0, room2.getNFreeBeds());
    }

    /**
     * Verifies that dismissing an admitted patient removes them from
     *        the room and frees the bed.
     */
    @Test
    void dismissPatient_ShouldRemovePatientFromRoom() throws Exception {
        room1.addPatient(patient);
        assertTrue(room1.hasPatient(patient));

        hospital.dismissPatient(patient);

        assertFalse(room1.hasPatient(patient));
    }

    /**
     * Verifies that attempting to dismiss a patient who was never
     *        admitted raises an exception, preventing phantom discharge records.
     */
    @Test
    void dismissPatient_ShouldThrowExceptionWhenPatientNotAdmitted() {
        Exception exception = assertThrows(Exception.class, () -> {
            hospital.dismissPatient(patient);
        });

        assertTrue(exception.getMessage().contains("No patient"));
    }
}