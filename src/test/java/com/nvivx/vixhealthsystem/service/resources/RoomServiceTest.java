package com.nvivx.vixhealthsystem.service.resources;

import com.nvivx.vixhealthsystem.model.facility.InternationRoom;
import com.nvivx.vixhealthsystem.model.facility.Room;
import com.nvivx.vixhealthsystem.model.person.Patient;
import com.nvivx.vixhealthsystem.model.person.employee.Secretary;
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
        InternationRoom room1 = new InternationRoom("101", 3);
        InternationRoom room2 = new InternationRoom("102", 2);

        when(roomRepository.findAll()).thenReturn(List.of(room1, room2));

        List<Room> result = service.getAllRooms();

        assertEquals(2, result.size());
        assertEquals("101", result.get(0).getNumber());
        assertEquals("102", result.get(1).getNumber());

        verify(roomRepository).findAll();
    }

    @Test
    void shouldFindRoomById() {
        InternationRoom room = new InternationRoom("101", 3);
        room.setId(1L);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        Room result = service.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("101", result.getNumber());

        verify(roomRepository).findById(1L);
    }

    @Test
    void shouldThrowWhenRoomDoesNotExist() {
        when(roomRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.findById(99L)
        );

        assertEquals("Room not found with id: 99", exception.getMessage());

        verify(roomRepository).findById(99L);
    }

    @Test
    void shouldReturnOnlyInpatientRooms() {
        InternationRoom inpatientRoom = new InternationRoom("101", 3);
        Room normalRoom = new Room("OFFICE-1") {};

        when(roomRepository.findAll()).thenReturn(List.of(inpatientRoom, normalRoom));

        List<InternationRoom> result = service.getAllInpatientRooms();

        assertEquals(1, result.size());
        assertEquals("101", result.get(0).getNumber());

        verify(roomRepository).findAll();
    }

    @Test
    void shouldReturnAvailableRooms() throws Exception {
        InternationRoom availableRoom = new InternationRoom("101", 2);
        InternationRoom fullRoom = new InternationRoom("102", 1);

        fullRoom.addPatient(mock(Patient.class));

        when(roomRepository.findAll()).thenReturn(List.of(availableRoom, fullRoom));

        List<InternationRoom> result = service.getAvailableRooms();

        assertEquals(1, result.size());
        assertEquals("101", result.get(0).getNumber());

        verify(roomRepository).findAll();
    }

    @Test
    void shouldReturnTotalAvailableBeds() throws Exception {
        InternationRoom room1 = new InternationRoom("101", 3);
        InternationRoom room2 = new InternationRoom("102", 2);

        room1.addPatient(mock(Patient.class));

        when(roomRepository.findAll()).thenReturn(List.of(room1, room2));

        int result = service.getTotalAvailableBeds();

        assertEquals(4, result);

        verify(roomRepository).findAll();
    }

    @Test
    void shouldReturnOccupiedBedsCount() throws Exception {
        InternationRoom room1 = new InternationRoom("101", 3);
        InternationRoom room2 = new InternationRoom("102", 2);

        room1.addPatient(mock(Patient.class));
        room2.addPatient(mock(Patient.class));
        room2.addPatient(mock(Patient.class));

        when(roomRepository.findAll()).thenReturn(List.of(room1, room2));

        int result = service.getOccupiedBedsCount();

        assertEquals(3, result);

        verify(roomRepository).findAll();
    }

    @Test
    void shouldReturnTotalBeds() {
        InternationRoom room1 = new InternationRoom("101", 3);
        InternationRoom room2 = new InternationRoom("102", 2);

        when(roomRepository.findAll()).thenReturn(List.of(room1, room2));

        int result = service.getTotalBeds();

        assertEquals(5, result);

        verify(roomRepository).findAll();
    }

    @Test
    void shouldAdmitPatientSuccessfully() {
        Patient patient = mock(Patient.class);
        Secretary secretary = new Secretary();

        InternationRoom room = new InternationRoom("101", 2);
        room.setId(1L);

        when(patientRepository.findById(10L)).thenReturn(Optional.of(patient));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(roomRepository.save(room)).thenReturn(room);

        service.admitPatient(secretary, 10L, 1L);

        assertTrue(room.hasPatient(patient));
        assertEquals(1, room.getPatients().size());

        verify(patientRepository).findById(10L);
        verify(roomRepository).findById(1L);
        verify(roomRepository).save(room);
        verify(auditService).log(
                eq("ADMIT_PATIENT"),
                eq("Patient"),
                eq("10"),
                contains("Patient admitted to room")
        );
    }

    @Test
    void shouldThrowWhenAdmittingPatientToFullRoom() throws Exception {
        Patient patient = mock(Patient.class);
        Secretary secretary = new Secretary();

        InternationRoom room = new InternationRoom("101", 1);
        room.addPatient(mock(Patient.class));

        when(patientRepository.findById(10L)).thenReturn(Optional.of(patient));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.admitPatient(secretary, 10L, 1L)
        );

        assertEquals("No available beds in room 101", exception.getMessage());

        verify(roomRepository, never()).save(any());
        verifyNoInteractions(auditService);
    }

    @Test
    void shouldThrowWhenAdmittingToNonInpatientRoom() {
        Patient patient = mock(Patient.class);
        Secretary secretary = new Secretary();

        Room normalRoom = new Room("OFFICE-1") {};

        when(patientRepository.findById(10L)).thenReturn(Optional.of(patient));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(normalRoom));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.admitPatient(secretary, 10L, 1L)
        );

        assertEquals("Room 1 is not an inpatient room", exception.getMessage());

        verify(roomRepository, never()).save(any());
        verifyNoInteractions(auditService);
    }

    @Test
    void shouldDismissPatientSuccessfully() {
        Patient patient = mock(Patient.class);
        Secretary secretary = new Secretary();

        InternationRoom room = new InternationRoom("101", 2);
        room.getPatients().add(patient);

        when(patientRepository.findById(10L)).thenReturn(Optional.of(patient));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(roomRepository.save(room)).thenReturn(room);

        service.dismissPatient(secretary, 10L, 1L);

        assertFalse(room.hasPatient(patient));
        assertTrue(room.getPatients().isEmpty());

        verify(roomRepository).save(room);
        verify(auditService).log(
                eq("DISMISS_PATIENT"),
                eq("Patient"),
                eq("10"),
                contains("Patient dismissed from room")
        );
    }

    @Test
    void shouldThrowWhenDismissingPatientNotInRoom() {
        Patient patient = mock(Patient.class);
        Secretary secretary = new Secretary();

        InternationRoom room = new InternationRoom("101", 2);

        when(patientRepository.findById(10L)).thenReturn(Optional.of(patient));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.dismissPatient(secretary, 10L, 1L)
        );

        assertEquals("Patient 10 is not in room 101", exception.getMessage());

        verify(roomRepository, never()).save(any());
        verifyNoInteractions(auditService);
    }

    @Test
    void shouldFindPatientRoom() {
        Patient patient = mock(Patient.class);

        InternationRoom room1 = new InternationRoom("101", 2);
        InternationRoom room2 = new InternationRoom("102", 2);
        room2.getPatients().add(patient);

        when(patientRepository.findById(10L)).thenReturn(Optional.of(patient));
        when(roomRepository.findAll()).thenReturn(List.of(room1, room2));

        InternationRoom result = service.findPatientRoom(10L);

        assertNotNull(result);
        assertEquals("102", result.getNumber());

        verify(patientRepository).findById(10L);
        verify(roomRepository).findAll();
    }

    @Test
    void shouldReturnTrueWhenPatientIsAdmitted() {
        Patient patient = mock(Patient.class);

        InternationRoom room = new InternationRoom("101", 2);
        room.getPatients().add(patient);

        when(patientRepository.findById(10L)).thenReturn(Optional.of(patient));
        when(roomRepository.findAll()).thenReturn(List.of(room));

        boolean result = service.isPatientAdmitted(10L);

        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenPatientIsNotAdmitted() {
        Patient patient = mock(Patient.class);

        InternationRoom room = new InternationRoom("101", 2);

        when(patientRepository.findById(10L)).thenReturn(Optional.of(patient));
        when(roomRepository.findAll()).thenReturn(List.of(room));

        boolean result = service.isPatientAdmitted(10L);

        assertFalse(result);
    }
}