package com.nvivx.vixhealthsystem.model.facility;

import com.nvivx.vixhealthsystem.model.enums.BedStatus;
import com.nvivx.vixhealthsystem.model.person.Patient;
import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

abstract class RoomTest {
    // Room is abstract, testing through concrete subclasses
}

class InternationRoomTest {
    private InternationRoom room;
    private Patient patient1;
    private Patient patient2;

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

    @Test
    void constructor_ShouldInitializeWithNumberAndBeds() {
        InternationRoom newRoom = new InternationRoom("B202", 2);
        assertEquals("B202", newRoom.getNumber());
        assertEquals(2, newRoom.getTotalNBeds());
        assertTrue(newRoom.getPatients().isEmpty());
    }

    @Test
    void getTotalNBeds_ShouldReturnCorrectCount() {
        assertEquals(3, room.getTotalNBeds());
    }

    @Test
    void getNFreeBeds_ShouldReturnCorrectFreeBeds() throws Exception {
        assertEquals(3, room.getNFreeBeds());

        room.addPatient(patient1);
        assertEquals(2, room.getNFreeBeds());

        room.addPatient(patient2);
        assertEquals(1, room.getNFreeBeds());
    }

    @Test
    void addPatient_ShouldAddPatientSuccessfully() throws Exception {
        room.addPatient(patient1);
        assertTrue(room.hasPatient(patient1));
        assertEquals(1, room.getPatients().size());
    }

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

    @Test
    void removePatient_ShouldRemovePatientSuccessfully() throws Exception {
        room.addPatient(patient1);
        assertTrue(room.hasPatient(patient1));

        room.removePatient(patient1);
        assertFalse(room.hasPatient(patient1));
        assertTrue(room.getPatients().isEmpty());
    }

    @Test
    void removePatient_ShouldThrowExceptionWhenPatientNotPresent() {
        Exception exception = assertThrows(Exception.class, () -> {
            room.removePatient(patient1);
        });

        assertTrue(exception.getMessage().contains("No patient"));
    }

    @Test
    void hasPatient_ShouldReturnTrueOnlyForAdmittedPatients() throws Exception {
        assertFalse(room.hasPatient(patient1));

        room.addPatient(patient1);
        assertTrue(room.hasPatient(patient1));
        assertFalse(room.hasPatient(patient2));
    }

    @Test
    void getBedStatus_ShouldReturnFreeWhenBedsAvailable() throws Exception {
        assertEquals(BedStatus.FREE, room.getBedStatus());

        room.addPatient(patient1);
        room.addPatient(patient2);
        assertEquals(BedStatus.FREE, room.getBedStatus());

        room.addPatient(new Patient());
        assertEquals(BedStatus.OCCUPIED, room.getBedStatus());
    }

    @Test
    void setPatients_ShouldReplacePatientList() {
        ArrayList<Patient> newList = new ArrayList<>();
        newList.add(patient1);
        room.setPatients(newList);

        assertEquals(1, room.getPatients().size());
        assertTrue(room.getPatients().contains(patient1));
    }
}

class OfficeTest {
    private Office office;
    private MedicalSpecialist specialist;

    @BeforeEach
    void setUp() {
        specialist = new MedicalSpecialist();
        specialist.setId(1L);
        specialist.setName("Marco");
        specialist.setSurname("Rossi");

        office = new Office("O101", specialist);
    }

    @Test
    void constructor_ShouldInitializeWithNumberAndEmployee() {
        assertEquals("O101", office.getNumber());
        assertEquals(specialist, office.getEmployee());
    }

    @Test
    void assignEmployee_ShouldAssignEmployeeToOffice() {
        Office newOffice = new Office();
        newOffice.setNumber("O102");

        assertNull(newOffice.getEmployee());
        newOffice.assignEmployee(specialist);
        assertEquals(specialist, newOffice.getEmployee());
    }

    @Test
    void setEmployee_ShouldUpdateEmployee() {
        MedicalSpecialist anotherSpecialist = new MedicalSpecialist();
        anotherSpecialist.setId(2L);

        office.setEmployee(anotherSpecialist);
        assertEquals(anotherSpecialist, office.getEmployee());
    }

    @Test
    void getEmployee_ShouldReturnCorrectEmployee() {
        assertEquals(specialist, office.getEmployee());
    }
}

class SpecializedRoomTest {
    private SpecializedRoom room;

    @BeforeEach
    void setUp() {
        room = new SpecializedRoom("S201", "Radiology");
    }

    @Test
    void constructor_ShouldInitializeWithNumberAndSpecialization() {
        assertEquals("S201", room.getNumber());
        assertEquals("Radiology", room.getSpecialization());
    }

    @Test
    void setSpecialization_ShouldUpdateSpecialization() {
        room.setSpecialization("MRI");
        assertEquals("MRI", room.getSpecialization());
    }

    @Test
    void getSpecialization_ShouldReturnCorrectValue() {
        assertEquals("Radiology", room.getSpecialization());
    }
}