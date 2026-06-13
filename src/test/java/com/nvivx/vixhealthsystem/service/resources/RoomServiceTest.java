package com.nvivx.vixhealthsystem.service.resources;

import com.nvivx.vixhealthsystem.model.facility.InternationRoom;
import com.nvivx.vixhealthsystem.model.facility.Office;
import com.nvivx.vixhealthsystem.model.facility.Room;
import com.nvivx.vixhealthsystem.model.person.Patient;
import com.nvivx.vixhealthsystem.repository.PatientRepository;
import com.nvivx.vixhealthsystem.repository.RoomRepository;
import com.nvivx.vixhealthsystem.service.AuditService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Arrange = prepare fake data and mock behavior
 * Act = call the method being tested
 * Assert = check the result
 * Verify = check that mocks were called correctly
 */
@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private RoomService service;

    @Test
    void shouldReturnAllRooms() {
        // Arrange
        InternationRoom room1 = new InternationRoom("101", 2);
        room1.setId(1L);

        InternationRoom room2 = new InternationRoom("102", 3);
        room2.setId(2L);

        when(roomRepository.findAll())
                .thenReturn(List.of(room1, room2));

        // Act
        List<Room> result = service.getAllRooms();

        // Assert
        assertEquals(2, result.size());
        assertEquals("101", result.get(0).getNumber());

        // Verify
        verify(roomRepository).findAll();
    }

    @Test
    void shouldFindRoomById() {
        // Arrange
        InternationRoom room = new InternationRoom("101", 2);
        room.setId(1L);

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        // Act
        Room result = service.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("101", result.getNumber());

        // Verify
        verify(roomRepository).findById(1L);
    }

    @Test
    void shouldThrowWhenRoomNotFound() {
        // Arrange
        when(roomRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act + Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.findById(99L)
        );

        assertTrue(exception.getMessage().contains("Room not found"));

        // Verify
        verify(roomRepository).findById(99L);
    }

    @Test
    void shouldReturnOnlyInpatientRooms() {
        // Arrange
        InternationRoom inpatientRoom = new InternationRoom("101", 2);
        Office office = new Office();
        office.setNumber("A101");

        when(roomRepository.findAll())
                .thenReturn(List.of(inpatientRoom, office));

        // Act
        List<InternationRoom> result = service.getAllInpatientRooms();

        // Assert
        assertEquals(1, result.size());
        assertEquals("101", result.get(0).getNumber());

        // Verify
        verify(roomRepository).findAll();
    }

    @Test
    void shouldReturnAvailableRooms() {
        // Arrange
        InternationRoom availableRoom = new InternationRoom("101", 2);

        InternationRoom fullRoom = new InternationRoom("102", 1);
        Patient patient = new Patient();

        try {
            fullRoom.addPatient(patient);
        } catch (Exception e) {
            fail("Setup should not throw exception");
        }

        when(roomRepository.findAll())
                .thenReturn(List.of(availableRoom, fullRoom));

        // Act
        List<InternationRoom> result = service.getAvailableRooms();

        // Assert
        assertEquals(1, result.size());
        assertEquals("101", result.get(0).getNumber());

        // Verify
        verify(roomRepository).findAll();
    }

    @Test
    void shouldReturnTotalAvailableBeds() {
        // Arrange
        InternationRoom room1 = new InternationRoom("101", 2);
        InternationRoom room2 = new InternationRoom("102", 3);

        Patient patient = new Patient();

        try {
            room2.addPatient(patient);
        } catch (Exception e) {
            fail("Setup should not throw exception");
        }

        when(roomRepository.findAll())
                .thenReturn(List.of(room1, room2));

        // Act
        int result = service.getTotalAvailableBeds();

        // Assert
        assertEquals(4, result);

        // Verify
        verify(roomRepository).findAll();
    }

    @Test
    void shouldReturnOccupiedBedsCount() {
        // Arrange
        InternationRoom room1 = new InternationRoom("101", 2);
        InternationRoom room2 = new InternationRoom("102", 3);

        Patient p1 = new Patient();
        Patient p2 = new Patient();

        try {
            room1.addPatient(p1);
            room2.addPatient(p2);
        } catch (Exception e) {
            fail("Setup should not throw exception");
        }

        when(roomRepository.findAll())
                .thenReturn(List.of(room1, room2));

        // Act
        int result = service.getOccupiedBedsCount();

        // Assert
        assertEquals(2, result);

        // Verify
        verify(roomRepository).findAll();
    }

    @Test
    void shouldReturnTotalBeds() {
        // Arrange
        InternationRoom room1 = new InternationRoom("101", 2);
        InternationRoom room2 = new InternationRoom("102", 3);

        when(roomRepository.findAll())
                .thenReturn(List.of(room1, room2));

        // Act
        int result = service.getTotalBeds();

        // Assert
        assertEquals(5, result);

        // Verify
        verify(roomRepository).findAll();
    }

    @Test
    void shouldAdmitPatientToRoom() {
        // Arrange
        Patient patient = new Patient();
        patient.setId(1L);

        InternationRoom room = new InternationRoom("101", 2);
        room.setId(10L);

        when(patientRepository.findById(1L))
                .thenReturn(Optional.of(patient));

        when(roomRepository.findById(10L))
                .thenReturn(Optional.of(room));

        // Act
        service.admitPatient(1L, 10L);

        // Assert
        assertTrue(room.hasPatient(patient));
        assertEquals(1, room.getPatients().size());

        // Verify
        verify(patientRepository).findById(1L);
        verify(roomRepository).findById(10L);
        verify(roomRepository).save(room);
        verify(auditService).log(
                eq("ADMIT_PATIENT"),
                eq("Patient"),
                eq("1"),
                contains("Patient admitted to room")
        );
    }

    @Test
    void shouldThrowWhenAdmittingPatientThatDoesNotExist() {
        // Arrange
        when(patientRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act + Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.admitPatient(99L, 10L)
        );

        assertTrue(exception.getMessage().contains("Patient not found"));

        // Verify
        verify(patientRepository).findById(99L);
        verifyNoInteractions(roomRepository);
    }

    @Test
    void shouldThrowWhenAdmittingToFullRoom() {
        // Arrange
        Patient existingPatient = new Patient();
        existingPatient.setId(1L);

        Patient newPatient = new Patient();
        newPatient.setId(2L);

        InternationRoom fullRoom = new InternationRoom("101", 1);
        fullRoom.setId(10L);

        try {
            fullRoom.addPatient(existingPatient);
        } catch (Exception e) {
            fail("Setup should not throw exception");
        }

        when(patientRepository.findById(2L))
                .thenReturn(Optional.of(newPatient));

        when(roomRepository.findById(10L))
                .thenReturn(Optional.of(fullRoom));

        // Act + Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.admitPatient(2L, 10L)
        );

        assertTrue(exception.getMessage().contains("No available beds"));

        // Verify
        verify(patientRepository).findById(2L);
        verify(roomRepository).findById(10L);
        verify(roomRepository, never()).save(any());
    }

    @Test
    void shouldDismissPatientFromRoom() {
        // Arrange
        Patient patient = new Patient();
        patient.setId(1L);

        InternationRoom room = new InternationRoom("101", 2);
        room.setId(10L);

        try {
            room.addPatient(patient);
        } catch (Exception e) {
            fail("Setup should not throw exception");
        }

        when(patientRepository.findById(1L))
                .thenReturn(Optional.of(patient));

        when(roomRepository.findById(10L))
                .thenReturn(Optional.of(room));

        // Act
        service.dismissPatient(1L, 10L);

        // Assert
        assertFalse(room.hasPatient(patient));
        assertTrue(room.getPatients().isEmpty());

        // Verify
        verify(patientRepository).findById(1L);
        verify(roomRepository).findById(10L);
        verify(roomRepository).save(room);
        verify(auditService).log(
                eq("DISMISS_PATIENT"),
                eq("Patient"),
                eq("1"),
                contains("Patient dismissed from room")
        );
    }

    @Test
    void shouldThrowWhenDismissingPatientNotInRoom() {
        // Arrange
        Patient patient = new Patient();
        patient.setId(1L);

        InternationRoom room = new InternationRoom("101", 2);
        room.setId(10L);

        when(patientRepository.findById(1L))
                .thenReturn(Optional.of(patient));

        when(roomRepository.findById(10L))
                .thenReturn(Optional.of(room));

        // Act + Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.dismissPatient(1L, 10L)
        );

        assertTrue(exception.getMessage().contains("is not in room"));

        // Verify
        verify(patientRepository).findById(1L);
        verify(roomRepository).findById(10L);
        verify(roomRepository, never()).save(any());
    }

    @Test
    void shouldFindPatientRoom() {
        // Arrange
        Patient patient = new Patient();
        patient.setId(1L);

        InternationRoom room = new InternationRoom("101", 2);
        room.setId(10L);

        try {
            room.addPatient(patient);
        } catch (Exception e) {
            fail("Setup should not throw exception");
        }

        when(patientRepository.findById(1L))
                .thenReturn(Optional.of(patient));

        when(roomRepository.findAll())
                .thenReturn(List.of(room));

        // Act
        InternationRoom result = service.findPatientRoom(1L);

        // Assert
        assertNotNull(result);
        assertEquals("101", result.getNumber());

        // Verify
        verify(patientRepository).findById(1L);
        verify(roomRepository).findAll();
    }

    @Test
    void shouldReturnNullWhenPatientIsNotAdmitted() {
        // Arrange
        Patient patient = new Patient();
        patient.setId(1L);

        InternationRoom room = new InternationRoom("101", 2);

        when(patientRepository.findById(1L))
                .thenReturn(Optional.of(patient));

        when(roomRepository.findAll())
                .thenReturn(List.of(room));

        // Act
        InternationRoom result = service.findPatientRoom(1L);

        // Assert
        assertNull(result);

        // Verify
        verify(patientRepository).findById(1L);
        verify(roomRepository).findAll();
    }

    @Test
    void shouldReturnTrueWhenPatientIsAdmitted() {
        // Arrange
        Patient patient = new Patient();
        patient.setId(1L);

        InternationRoom room = new InternationRoom("101", 2);

        try {
            room.addPatient(patient);
        } catch (Exception e) {
            fail("Setup should not throw exception");
        }

        when(patientRepository.findById(1L))
                .thenReturn(Optional.of(patient));

        when(roomRepository.findAll())
                .thenReturn(List.of(room));

        // Act
        boolean result = service.isPatientAdmitted(1L);

        // Assert
        assertTrue(result);

        // Verify
        verify(patientRepository).findById(1L);
        verify(roomRepository).findAll();
    }

    @Test
    void shouldReturnFalseWhenPatientIsNotAdmitted() {
        // Arrange
        Patient patient = new Patient();
        patient.setId(1L);

        when(patientRepository.findById(1L))
                .thenReturn(Optional.of(patient));

        when(roomRepository.findAll())
                .thenReturn(List.of(new InternationRoom("101", 2)));

        // Act
        boolean result = service.isPatientAdmitted(1L);

        // Assert
        assertFalse(result);

        // Verify
        verify(patientRepository).findById(1L);
        verify(roomRepository).findAll();
    }
}