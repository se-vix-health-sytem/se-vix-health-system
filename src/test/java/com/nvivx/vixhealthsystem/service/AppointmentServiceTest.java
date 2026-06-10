package com.nvivx.vixhealthsystem.service;

import com.nvivx.vixhealthsystem.dto.CreateAppointmentRequest;
import com.nvivx.vixhealthsystem.exception.SlotNotAvailableException;
import com.nvivx.vixhealthsystem.model.medical.Appointment;
import com.nvivx.vixhealthsystem.repository.JsonAppointmentRepository;
import com.nvivx.vixhealthsystem.service.medical.AppointmentService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentServiceTest {

    @Test
    void shouldCreateAppointmentSuccessfully() {

        JsonAppointmentRepository repo =
                Mockito.mock(JsonAppointmentRepository.class);

        Mockito.when(repo.findAll())
                .thenReturn(new ArrayList<>());

        AppointmentService service =
                new AppointmentService(repo);

        CreateAppointmentRequest request =
                new CreateAppointmentRequest();

        request.setDateTime(LocalDateTime.of(2026, 6, 10, 10, 0));
        request.setDuration(30);
        request.setNotes("General checkup");

        Appointment appointment =
                service.bookAppointment(request);

        assertNotNull(appointment);
        assertEquals(30, appointment.getDuration());
        assertEquals("General checkup", appointment.getNotes());

        Mockito.verify(repo).findAll();
        Mockito.verify(repo).saveAll(Mockito.anyList());
    }

    @Test
    void shouldThrowExceptionWhenSlotAlreadyBooked() {

        JsonAppointmentRepository repo =
                Mockito.mock(JsonAppointmentRepository.class);

        LocalDateTime date =
                LocalDateTime.of(2026, 6, 10, 10, 0);

        Appointment existing =
                new Appointment(1, date, 30, "Existing");

        Mockito.when(repo.findAll())
                .thenReturn(new ArrayList<>(List.of(existing)));

        AppointmentService service =
                new AppointmentService(repo);

        CreateAppointmentRequest request =
                new CreateAppointmentRequest();

        request.setDateTime(date);
        request.setDuration(30);
        request.setNotes("New booking");

        assertThrows(
                SlotNotAvailableException.class,
                () -> service.bookAppointment(request)
        );

        Mockito.verify(repo).findAll();
        Mockito.verify(repo, Mockito.never())
                .saveAll(Mockito.anyList());
    }

    @Test
    void shouldReturnAllAppointments() {

        JsonAppointmentRepository repo =
                Mockito.mock(JsonAppointmentRepository.class);

        Appointment a1 =
                new Appointment(1, LocalDateTime.now(), 30, "Visit");

        Mockito.when(repo.findAll())
                .thenReturn(List.of(a1));

        AppointmentService service =
                new AppointmentService(repo);

        assertEquals(1, service.getAllAppointments().size());

        Mockito.verify(repo).findAll();
    }

    @Test
    void shouldThrowExceptionWhenTimesOverlap() {

        JsonAppointmentRepository repo =
                Mockito.mock(JsonAppointmentRepository.class);

        LocalDateTime existingStart =
                LocalDateTime.of(2026, 6, 10, 10, 0);

        Appointment existing =
                new Appointment(1, existingStart, 30, "Existing");

        Mockito.when(repo.findAll())
                .thenReturn(new ArrayList<>(List.of(existing)));

        AppointmentService service =
                new AppointmentService(repo);

        CreateAppointmentRequest request =
                new CreateAppointmentRequest();

        request.setDateTime(
                LocalDateTime.of(2026, 6, 10, 10, 15)
        );
        request.setDuration(30);
        request.setNotes("Overlap test");

        assertThrows(
                SlotNotAvailableException.class,
                () -> service.bookAppointment(request)
        );

        Mockito.verify(repo).findAll();
        Mockito.verify(repo, Mockito.never()).saveAll(Mockito.anyList());
    }
}