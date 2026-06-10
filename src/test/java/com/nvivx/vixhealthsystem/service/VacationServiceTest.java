package com.nvivx.vixhealthsystem.infrastructure;

import com.nvivx.vixhealthsystem.model.staff.Vacation;
import com.nvivx.vixhealthsystem.repository.JsonVacationRepository;
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
class VacationServiceTest {

    @Mock
    private JsonVacationRepository repository;

    @InjectMocks
    private VacationService service;

    @Test
    void shouldAddVacation() {

        when(repository.findAll()).thenReturn(new ArrayList<>());

        Vacation v = service.addVacation(
                1L,
                LocalDate.of(2026, 7, 10),
                LocalDate.of(2026, 7, 15),
                "Holiday"
        );

        assertNotNull(v);
        assertEquals(1L, v.getId());
        assertEquals(1L, v.getEmployeeId());

        verify(repository, times(1)).saveAll(anyList());
    }

    @Test
    void shouldDeleteVacation() {

        Vacation v = new Vacation(
                1L,
                1L,
                LocalDate.now(),
                LocalDate.now().plusDays(3),
                "Trip"
        );

        when(repository.findAll()).thenReturn(new ArrayList<>(List.of(v)));

        service.deleteVacation(1L);

        verify(repository, times(1)).saveAll(anyList());
    }

    @Test
    void shouldFilterVacationsByEmployee() {

        Vacation v1 = new Vacation(1L, 1L, LocalDate.now(), LocalDate.now().plusDays(2), "A");
        Vacation v2 = new Vacation(2L, 2L, LocalDate.now(), LocalDate.now().plusDays(3), "B");

        when(repository.findAll()).thenReturn(List.of(v1, v2));

        List<Vacation> result = service.getEmployeeVacations(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getEmployeeId());
    }
}