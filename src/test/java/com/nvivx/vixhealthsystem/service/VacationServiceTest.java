package com.nvivx.vixhealthsystem.service.scheduling;

import com.nvivx.vixhealthsystem.model.staff.VacationRequest;
import com.nvivx.vixhealthsystem.repository.JsonVacationRepository;
import com.nvivx.vixhealthsystem.service.AuditService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VacationServiceTest {

    @Mock
    private JsonVacationRepository repository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private VacationService service;

    @Test
    void shouldAddVacation() {
        when(repository.findAll()).thenReturn(List.of());

        VacationRequest result = service.addVacationRequest(
                101,
                "John",
                LocalDate.now(),
                LocalDate.now().plusDays(3),
                "Holiday"
        );

        assertNotNull(result);
        assertEquals(101, result.getEmployeeId());
        assertEquals("PENDING", result.getStatus());

        verify(repository).save(any());
        verify(auditService).log(any(), any(), any(), any());
    }

    @Test
    void shouldApproveVacation() {
        VacationRequest request = new VacationRequest(
                1, 101, "John",
                LocalDate.now(),
                LocalDate.now().plusDays(3),
                "Holiday",
                "PENDING"
        );

        when(repository.findById(1)).thenReturn(Optional.of(request));
        when(repository.save(any())).thenReturn(request);

        VacationRequest result = service.approveVacation(1);

        assertEquals("APPROVED", result.getStatus());

        verify(repository).save(any());
        verify(auditService).log(any(), any(), any(), any());
    }

    @Test
    void shouldDenyVacation() {
        VacationRequest request = new VacationRequest(
                1, 101, "John",
                LocalDate.now(),
                LocalDate.now().plusDays(3),
                "Holiday",
                "PENDING"
        );

        when(repository.findById(1)).thenReturn(Optional.of(request));
        when(repository.save(any())).thenReturn(request);

        VacationRequest result = service.denyVacation(1);

        assertEquals("DENIED", result.getStatus());
    }

    @Test
    void shouldDetectOverlappingVacation() {
        VacationRequest existing = new VacationRequest(
                1, 101, "John",
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 10),
                "Holiday",
                "APPROVED"
        );

        when(repository.findAll()).thenReturn(List.of(existing));

        boolean overlap = service.hasOverlappingVacation(
                101,
                LocalDate.of(2026, 7, 5),
                LocalDate.of(2026, 7, 8)
        );

        assertTrue(overlap);
    }

    @Test
    void shouldReturnEmployeeRequests() {
        VacationRequest v1 = new VacationRequest(
                1, 101, "John",
                LocalDate.now(),
                LocalDate.now().plusDays(3),
                "Holiday",
                "PENDING"
        );

        when(repository.findAll()).thenReturn(List.of(v1));

        List<VacationRequest> result = service.getRequestsForEmployee(101);

        assertEquals(1, result.size());
    }
}