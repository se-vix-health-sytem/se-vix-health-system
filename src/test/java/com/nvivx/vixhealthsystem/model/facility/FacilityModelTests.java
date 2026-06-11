package com.nvivx.vixhealthsystem.model.facility;

import com.nvivx.vixhealthsystem.model.person.Patient;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FacilityModelTests {

    // ========== Location Tests ==========
    @Test
    void testLocation() {
        Location loc = new Location(46.066422, 11.119928);
        assertEquals(46.066422, loc.getLatitude());
        assertEquals(11.119928, loc.getLongitude());

        loc.setLatitude(45.0);
        loc.setLongitude(10.0);
        assertEquals(45.0, loc.getLatitude());
        assertEquals(10.0, loc.getLongitude());
    }

    // ========== MedicalFacility Tests ==========
    @Test
    void testMedicalFacility() {
        MedicalFacility facility = new MedicalFacility();
        facility.setId(1L);
        facility.setName("Test Hospital");
        facility.setEmail("test@hospital.com");

        assertEquals(1L, facility.getId());
        assertEquals("Test Hospital", facility.getName());
        assertEquals("test@hospital.com", facility.getEmail());
        assertNotNull(facility.getRooms());
        assertNotNull(facility.getDepartments());
    }

    @Test
    void testMedicalFacilityParameterized() {
        Location loc = new Location(46.0, 11.0);
        MedicalFacility facility = new MedicalFacility("Central Hospital", loc, "info@vix.com", "+39 0461 000001");

        assertEquals("Central Hospital", facility.getName());
        assertEquals(loc, facility.getLocation());
        assertEquals("info@vix.com", facility.getEmail());
        assertEquals("+39 0461 000001", facility.getPhoneNumber());
    }

    // ========== Hospital Tests ==========
    @Test
    void testHospitalRoomManagement() throws Exception {
        Hospital hospital = new Hospital();
        hospital.setName("Test Hospital");

        InternationRoom room1 = new InternationRoom("101", 1);
        InternationRoom room2 = new InternationRoom("102", 1);
        room1.setMedicalFacility(hospital);
        room2.setMedicalFacility(hospital);
        hospital.getRooms().add(room1);
        hospital.getRooms().add(room2);

        Patient patient = new Patient();
        patient.setId(1L);

        assertEquals(2, hospital.getRoomsForPatients().size());
        assertEquals(2, hospital.getFreeRoomsForPatients().size());

        hospital.internPatient(patient, room1);
        assertEquals(1, hospital.getFreeRoomsForPatients().size());

        InternationRoom found = hospital.findPatientInRoom(patient);
        assertEquals(room1, found);

        hospital.dismissPatient(patient);
        assertThrows(Exception.class, () -> hospital.findPatientInRoom(patient));
    }

    // ========== InternationRoom Tests ==========
    @Test
    void testInternationRoom() throws Exception {
        InternationRoom room = new InternationRoom("201", 2);
        assertEquals("201", room.getNumber());
        assertEquals(2, room.getTotalNBeds());
        assertEquals(2, room.getNFreeBeds());

        Patient p1 = new Patient();
        Patient p2 = new Patient();

        room.addPatient(p1);
        assertEquals(1, room.getNFreeBeds());
        assertTrue(room.hasPatient(p1));

        room.addPatient(p2);

        Patient p3 = new Patient();
        assertThrows(Exception.class, () -> room.addPatient(p3));

        room.removePatient(p1);
        assertFalse(room.hasPatient(p1));
        assertThrows(Exception.class, () -> room.removePatient(p1));
    }

    // ========== Office Tests ==========
    @Test
    void testOffice() {
        Office office = new Office();
        office.setNumber("A101");
        assertEquals("A101", office.getNumber());

        com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist emp =
                new com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist();
        office.assignEmployee(emp);
        assertEquals(emp, office.getEmployee());
    }

    // ========== Room Tests ==========
    @Test
    void testRoomAbstract() {
        // Test concrete implementation via InternationRoom
        InternationRoom room = new InternationRoom("T001", 1);
        room.setId(100L);
        assertEquals(100L, room.getId());
        assertEquals("T001", room.getNumber());
    }
}