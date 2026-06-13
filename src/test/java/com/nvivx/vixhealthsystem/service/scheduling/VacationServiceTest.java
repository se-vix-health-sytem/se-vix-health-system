/* package com.nvivx.vixhealthsystem.service.scheduling;

import com.nvivx.vixhealthsystem.exception.VacationNotFoundException;
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
import static org.mockito.Mockito.*;

/**
 * Arrange = prepare fake data and mock behavior
 * Act = call the method being tested
 * Assert = check the result
 * Verify = check that mocks were called correctly
 */

/*
@ExtendWith(MockitoExtension.class)
class VacationServiceTest {

    @Mock
    private JsonVacationRepository repository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private VacationService service;

    @Test
    void shouldAddVacationRequest() {
        // Arrange
        VacationRequest savedRequest = new VacationRequest(
                1,
                101,
                "John Smith",
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 5),
                "Holiday",
                "PENDING"
        );

        when(repository.save(any(VacationRequest.class)))
                .thenReturn(savedRequest);

        // Act
        VacationRequest result = service.addVacationRequest(
                101,
                "John Smith",
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 5),
                "Holiday"
        );

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(101, result.getEmployeeId());
        assertEquals("John Smith", result.getEmployeeName());
        assertEquals("PENDING", result.getStatus());

        // Verify
        verify(repository).save(any(VacationRequest.class));
        verify(auditService).log(
                eq("CREATE_VACATION_REQUEST"),
                eq("VacationRequest"),
                eq("1"),
                contains("Created vacation request")
        );
    }

    @Test
    void shouldApproveVacation() {
        // Arrange
        VacationRequest request = new VacationRequest(
                1,
                101,
                "John Smith",
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 5),
                "Holiday",
                "PENDING"
        );

        when(repository.findById(1))
                .thenReturn(Optional.of(request));

        when(repository.save(request))
                .thenReturn(request);

        // Act
        VacationRequest result = service.approveVacation(1);

        // Assert
        assertEquals("APPROVED", result.getStatus());

        // Verify
        verify(repository).findById(1);
        verify(repository).save(request);
        verify(auditService).log(
                eq("APPROVE_VACATION"),
                eq("VacationRequest"),
                eq("1"),
                contains("Approved vacation")
        );
    }

    @Test
    void shouldThrowWhenApprovingVacationThatDoesNotExist() {
        // Arrange
        when(repository.findById(99))
                .thenReturn(Optional.empty());

        // Act + Assert
        VacationNotFoundException exception = assertThrows(
                VacationNotFoundException.class,
                () -> service.approveVacation(99)
        );

        assertTrue(exception.getMessage().contains("Vacation request not found"));

        // Verify
        verify(repository).findById(99);
        verify(repository, never()).save(any());
    }

    @Test
    void shouldDenyVacation() {
        // Arrange
        VacationRequest request = new VacationRequest(
                2,
                102,
                "Anna Brown",
                LocalDate.of(2026, 8, 10),
                LocalDate.of(2026, 8, 12),
                "Trip",
                "PENDING"
        );

        when(repository.findById(2))
                .thenReturn(Optional.of(request));

        when(repository.save(request))
                .thenReturn(request);

        // Act
        VacationRequest result = service.denyVacation(2);

        // Assert
        assertEquals("DENIED", result.getStatus());

        // Verify
        verify(repository).findById(2);
        verify(repository).save(request);
        verify(auditService).log(
                eq("DENY_VACATION"),
                eq("VacationRequest"),
                eq("2"),
                contains("Denied vacation")
        );
    }

    @Test
    void shouldThrowWhenDenyingVacationThatDoesNotExist() {
        // Arrange
        when(repository.findById(99))
                .thenReturn(Optional.empty());

        // Act + Assert
        VacationNotFoundException exception = assertThrows(
                VacationNotFoundException.class,
                () -> service.denyVacation(99)
        );

        assertTrue(exception.getMessage().contains("Vacation request not found"));

        // Verify
        verify(repository).findById(99);
        verify(repository, never()).save(any());
    }

    @Test
    void shouldReturnAllRequests() {
        // Arrange
        VacationRequest v1 = new VacationRequest();
        VacationRequest v2 = new VacationRequest();

        when(repository.findAll())
                .thenReturn(List.of(v1, v2));

        // Act
        List<VacationRequest> result = service.getAllRequests();

        // Assert
        assertEquals(2, result.size());

        // Verify
        verify(repository).findAll();
    }

    @Test
    void shouldReturnOnlyPendingRequests() {
        // Arrange
        VacationRequest pending = new VacationRequest(
                1,
                101,
                "John Smith",
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 5),
                "Holiday",
                "PENDING"
        );

        VacationRequest approved = new VacationRequest(
                2,
                102,
                "Anna Brown",
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 3),
                "Trip",
                "APPROVED"
        );

        when(repository.findAll())
                .thenReturn(List.of(pending, approved));

        // Act
        List<VacationRequest> result = service.getPendingRequests();

        // Assert
        assertEquals(1, result.size());
        assertEquals("PENDING", result.get(0).getStatus());

        // Verify
        verify(repository).findAll();
    }

    @Test
    void shouldReturnRequestsForEmployee() {
        // Arrange
        VacationRequest v1 = new VacationRequest(
                1,
                101,
                "John Smith",
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 5),
                "Holiday",
                "PENDING"
        );

        VacationRequest v2 = new VacationRequest(
                2,
                102,
                "Anna Brown",
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 3),
                "Trip",
                "APPROVED"
        );

        when(repository.findAll())
                .thenReturn(List.of(v1, v2));

        // Act
        List<VacationRequest> result = service.getRequestsForEmployee(101);

        // Assert
        assertEquals(1, result.size());
        assertEquals(101, result.get(0).getEmployeeId());

        // Verify
        verify(repository).findAll();
    }

    @Test
    void shouldReturnApprovedRequestsForEmployee() {
        // Arrange
        VacationRequest approved = new VacationRequest(
                1,
                101,
                "John Smith",
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 5),
                "Holiday",
                "APPROVED"
        );

        VacationRequest pending = new VacationRequest(
                2,
                101,
                "John Smith",
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 3),
                "Trip",
                "PENDING"
        );

        when(repository.findAll())
                .thenReturn(List.of(approved, pending));

        // Act
        List<VacationRequest> result =
                service.getApprovedRequestsForEmployee(101);

        // Assert
        assertEquals(1, result.size());
        assertEquals("APPROVED", result.get(0).getStatus());

        // Verify
        verify(repository).findAll();
    }

    @Test
    void shouldDeleteExistingRequest() {
        // Arrange
        VacationRequest request = new VacationRequest(
                1,
                101,
                "John Smith",
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 5),
                "Holiday",
                "PENDING"
        );

        when(repository.findById(1))
                .thenReturn(Optional.of(request));

        // Act
        service.deleteRequest(1);

        // Assert
        verify(repository).findById(1);
        verify(repository).deleteById(1);
        verify(auditService).log(
                eq("DELETE_VACATION_REQUEST"),
                eq("VacationRequest"),
                eq("1"),
                contains("Deleted vacation request")
        );
    }

    @Test
    void shouldDeleteRequestWithoutAuditWhenRequestDoesNotExist() {
        // Arrange
        when(repository.findById(99))
                .thenReturn(Optional.empty());

        // Act
        service.deleteRequest(99);

        // Assert / Verify
        verify(repository).findById(99);
        verify(repository).deleteById(99);
        verifyNoInteractions(auditService);
    }

    @Test
    void shouldDetectOverlappingVacation() {
        // Arrange
        VacationRequest existing = new VacationRequest(
                1,
                101,
                "John Smith",
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 10),
                "Holiday",
                "APPROVED"
        );

        when(repository.findAll())
                .thenReturn(List.of(existing));

        // Act
        boolean result = service.hasOverlappingVacation(
                101,
                LocalDate.of(2026, 7, 5),
                LocalDate.of(2026, 7, 8)
        );

        // Assert
        assertTrue(result);

        // Verify
        verify(repository).findAll();
    }

    @Test
    void shouldReturnFalseWhenVacationDoesNotOverlap() {
        // Arrange
        VacationRequest existing = new VacationRequest(
                1,
                101,
                "John Smith",
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 5),
                "Holiday",
                "APPROVED"
        );

        when(repository.findAll())
                .thenReturn(List.of(existing));

        // Act
        boolean result = service.hasOverlappingVacation(
                101,
                LocalDate.of(2026, 7, 10),
                LocalDate.of(2026, 7, 12)
        );

        // Assert
        assertFalse(result);

        // Verify
        verify(repository).findAll();
    }

    @Test
    void shouldIgnoreDeniedRequestsWhenCheckingOverlap() {
        // Arrange
        VacationRequest denied = new VacationRequest(
                1,
                101,
                "John Smith",
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 10),
                "Holiday",
                "DENIED"
        );

        when(repository.findAll())
                .thenReturn(List.of(denied));

        // Act
        boolean result = service.hasOverlappingVacation(
                101,
                LocalDate.of(2026, 7, 5),
                LocalDate.of(2026, 7, 8)
        );

        // Assert
        assertFalse(result);

        // Verify
        verify(repository).findAll();
    }

    @Test
    void shouldReturnTotalVacationDaysInYear() {
        // Arrange
        VacationRequest approved = new VacationRequest(
                1,
                101,
                "John Smith",
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 5),
                "Holiday",
                "APPROVED"
        );

        VacationRequest pending = new VacationRequest(
                2,
                101,
                "John Smith",
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 3),
                "Trip",
                "PENDING"
        );

        when(repository.findAll())
                .thenReturn(List.of(approved, pending));

        // Act
        long result = service.getTotalVacationDaysInYear(101, 2026);

        // Assert
        assertEquals(5, result);

        // Verify
        verify(repository).findAll();
    }
}
 */