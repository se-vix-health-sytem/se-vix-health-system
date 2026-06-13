package com.nvivx.vixhealthsystem.model.facility;

import com.nvivx.vixhealthsystem.model.person.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MedicalFacilityTest {
    private MedicalFacility facility;

    @BeforeEach
    void setUp() {
        Location location = new Location(45.4642, 9.1900);
        facility = new MedicalFacility("VIX Central Hospital", location, "info@vixhealth.it", "+39 0461 000001");
    }

    @Test
    void constructor_ShouldInitializeFacility() {
        assertEquals("VIX Central Hospital", facility.getName());
        assertEquals(45.4642, facility.getLocation().getLatitude());
        assertEquals(9.1900, facility.getLocation().getLongitude());
        assertEquals("info@vixhealth.it", facility.getEmail());
        assertEquals("+39 0461 000001", facility.getPhoneNumber());
    }

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

    @Test
    void rooms_ShouldBeManageable() {
        assertTrue(facility.getRooms().isEmpty());

        InternationRoom room1 = new InternationRoom("101", 2);
        room1.setMedicalFacility(facility);
        facility.getRooms().add(room1);

        assertEquals(1, facility.getRooms().size());
        assertEquals(room1, facility.getRooms().get(0));
    }

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

class HospitalTest {
    private Hospital hospital;
    private InternationRoom room1;
    private InternationRoom room2;
    private Patient patient;

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

    @Test
    void findPatientInRoom_ShouldFindCorrectRoom() throws Exception {
        room1.addPatient(patient);

        InternationRoom found = hospital.findPatientInRoom(patient);
        assertEquals(room1, found);
    }

    @Test
    void findPatientInRoom_ShouldThrowExceptionWhenPatientNotAdmitted() {
        Exception exception = assertThrows(Exception.class, () -> {
            hospital.findPatientInRoom(patient);
        });

        assertTrue(exception.getMessage().contains("No patient"));
    }

    @Test
    void internPatient_ShouldAdmitPatientToRoom() throws Exception {
        hospital.internPatient(patient, room2);

        assertTrue(room2.hasPatient(patient));
        assertEquals(0, room2.getNFreeBeds());
    }

    @Test
    void dismissPatient_ShouldRemovePatientFromRoom() throws Exception {
        room1.addPatient(patient);
        assertTrue(room1.hasPatient(patient));

        hospital.dismissPatient(patient);

        assertFalse(room1.hasPatient(patient));
    }

    @Test
    void dismissPatient_ShouldThrowExceptionWhenPatientNotAdmitted() {
        Exception exception = assertThrows(Exception.class, () -> {
            hospital.dismissPatient(patient);
        });

        assertTrue(exception.getMessage().contains("No patient"));
    }
}