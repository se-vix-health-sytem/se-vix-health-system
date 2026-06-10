package com.nvivx.vixhealthsystem.service;

import com.nvivx.vixhealthsystem.infrastructure.ShiftService;
import com.nvivx.vixhealthsystem.model.staff.Shift;
import com.nvivx.vixhealthsystem.repository.JsonShiftRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShiftServiceTest {

    @Mock
    private JsonShiftRepository repository;

    @InjectMocks
    private ShiftService service;

    @Test
    void shouldCreateShift() {

        when(repository.findAll()).thenReturn(new ArrayList<>());

        Shift shift = service.assignShift(
                1L,
                LocalDate.of(2026, 6, 10),
                "Morning",
                "ER duty"
        );

        assertNotNull(shift);
        assertEquals(1L, shift.getId());
        assertEquals(1L, shift.getEmployeeId());
        assertEquals("Morning", shift.getShiftType());

        verify(repository, times(1)).saveAll(anyList());
    }

    @Test
    void shouldDeleteShift() {

        Shift shift = new Shift(
                1L,
                1L,
                LocalDate.now(),
                "Night",
                "Test"
        );

        when(repository.findAll()).thenReturn(new ArrayList<>(List.of(shift)));

        service.deleteShift(1L);

        verify(repository, times(1)).saveAll(anyList());
    }

    @Test
    void shouldFilterShiftsByEmployee() {

        Shift shift1 = new Shift(1L, 1L, LocalDate.now(), "Morning", "A");
        Shift shift2 = new Shift(2L, 2L, LocalDate.now(), "Night", "B");

        when(repository.findAll()).thenReturn(List.of(shift1, shift2));

        List<Shift> result = service.getEmployeeShifts(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getEmployeeId());
    }
}