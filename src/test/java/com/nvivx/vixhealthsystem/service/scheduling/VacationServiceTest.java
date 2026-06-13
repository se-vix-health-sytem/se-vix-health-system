package com.nvivx.vixhealthsystem.service.scheduling;

import com.nvivx.vixhealthsystem.exception.VacationNotFoundException;
import com.nvivx.vixhealthsystem.model.enums.VacationStatus;
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

@ExtendWith(MockitoExtension.class)
class VacationServiceTest {

    @Mock
    private JsonVacationRepository repository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private VacationService service;

    /**
     * Tests that addVacationRequest() creates a new vacation request
     * with PENDING status and saves it through the repository.
     */
    @Test
    void shouldAddVacationRequestSuccessfully() {
        VacationRequest savedRequest = new VacationRequest(
                1,
                10,
                "Marco Rossi",
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 5),
                "Summer holiday",
                VacationStatus.PENDING
        );

        when(repository.save(any(VacationRequest.class)))
                .thenReturn(savedRequest);

        VacationRequest result = service.addVacationRequest(
                10,
                "Marco Rossi",
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 5),
                "Summer holiday"
        );

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(10, result.getEmployeeId());
        assertEquals("Marco Rossi", result.getEmployeeName());
        assertEquals(VacationStatus.PENDING, result.getStatus());

        verify(repository).save(any(VacationRequest.class));
        verify(auditService).log(
                eq("CREATE_VACATION_REQUEST"),
                eq("VacationRequest"),
                eq("1"),
                contains("Created vacation request")
        );
    }

    /**
     * Tests that approveVacation() changes a pending request
     * to APPROVED and saves the updated request.
     */
    @Test
    void shouldApproveVacationSuccessfully() {
        VacationRequest request = new VacationRequest(
                1,
                10,
                "Marco Rossi",
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 5),
                "Summer holiday",
                VacationStatus.PENDING
        );

        VacationRequest approvedRequest = new VacationRequest(
                1,
                10,
                "Marco Rossi",
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 5),
                "Summer holiday",
                VacationStatus.APPROVED
        );

        when(repository.findById(1)).thenReturn(Optional.of(request));
        when(repository.save(request)).thenReturn(approvedRequest);

        VacationRequest result = service.approveVacation(1);

        assertEquals(VacationStatus.APPROVED, request.getStatus());
        assertEquals(VacationStatus.APPROVED, result.getStatus());

        verify(repository).findById(1);
        verify(repository).save(request);
        verify(auditService).log(
                eq("APPROVE_VACATION"),
                eq("VacationRequest"),
                eq("1"),
                contains("Approved vacation")
        );
    }

    /**
     * Tests that approveVacation() throws VacationNotFoundException
     * when the requested vacation ID does not exist.
     */
    @Test
    void shouldThrowWhenApprovingNonExistingVacation() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        VacationNotFoundException exception = assertThrows(
                VacationNotFoundException.class,
                () -> service.approveVacation(99)
        );

        assertEquals("Vacation request not found: 99", exception.getMessage());

        verify(repository).findById(99);
        verify(repository, never()).save(any());
        verifyNoInteractions(auditService);
    }

    /**
     * Tests that denyVacation() changes a pending request
     * to DENIED and saves the updated request.
     */
    @Test
    void shouldDenyVacationSuccessfully() {
        VacationRequest request = new VacationRequest(
                2,
                20,
                "Elena Bianchi",
                LocalDate.of(2026, 8, 10),
                LocalDate.of(2026, 8, 12),
                "Family event",
                VacationStatus.PENDING
        );

        VacationRequest deniedRequest = new VacationRequest(
                2,
                20,
                "Elena Bianchi",
                LocalDate.of(2026, 8, 10),
                LocalDate.of(2026, 8, 12),
                "Family event",
                VacationStatus.DENIED
        );

        when(repository.findById(2)).thenReturn(Optional.of(request));
        when(repository.save(request)).thenReturn(deniedRequest);

        VacationRequest result = service.denyVacation(2);

        assertEquals(VacationStatus.DENIED, request.getStatus());
        assertEquals(VacationStatus.DENIED, result.getStatus());

        verify(repository).findById(2);
        verify(repository).save(request);
        verify(auditService).log(
                eq("DENY_VACATION"),
                eq("VacationRequest"),
                eq("2"),
                contains("Denied vacation")
        );
    }

    /**
     * Tests that denyVacation() throws VacationNotFoundException
     * when the requested vacation ID does not exist.
     */
    @Test
    void shouldThrowWhenDenyingNonExistingVacation() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        VacationNotFoundException exception = assertThrows(
                VacationNotFoundException.class,
                () -> service.denyVacation(99)
        );

        assertEquals("Vacation request not found: 99", exception.getMessage());

        verify(repository).findById(99);
        verify(repository, never()).save(any());
        verifyNoInteractions(auditService);
    }

    /**
     * Tests that getAllRequests() returns every vacation request
     * provided by the repository.
     */
    @Test
    void shouldReturnAllRequests() {
        VacationRequest r1 = createRequest(1, 10, "Marco", VacationStatus.PENDING);
        VacationRequest r2 = createRequest(2, 20, "Elena", VacationStatus.APPROVED);

        when(repository.findAll()).thenReturn(List.of(r1, r2));

        List<VacationRequest> result = service.getAllRequests();

        assertEquals(2, result.size());
        assertEquals("Marco", result.get(0).getEmployeeName());
        assertEquals("Elena", result.get(1).getEmployeeName());

        verify(repository).findAll();
    }

    /**
     * Tests that getPendingRequests() returns only requests
     * with PENDING status.
     */
    @Test
    void shouldReturnOnlyPendingRequests() {
        VacationRequest pending = createRequest(1, 10, "Marco", VacationStatus.PENDING);
        VacationRequest approved = createRequest(2, 20, "Elena", VacationStatus.APPROVED);
        VacationRequest denied = createRequest(3, 30, "Luca", VacationStatus.DENIED);

        when(repository.findAll()).thenReturn(List.of(pending, approved, denied));

        List<VacationRequest> result = service.getPendingRequests();

        assertEquals(1, result.size());
        assertEquals(VacationStatus.PENDING, result.get(0).getStatus());

        verify(repository).findAll();
    }

    /**
     * Tests that getRequestsForEmployee() returns only
     * vacation requests belonging to the selected employee.
     */
    @Test
    void shouldReturnRequestsForEmployee() {
        VacationRequest r1 = createRequest(1, 10, "Marco", VacationStatus.PENDING);
        VacationRequest r2 = createRequest(2, 20, "Elena", VacationStatus.APPROVED);
        VacationRequest r3 = createRequest(3, 10, "Marco", VacationStatus.DENIED);

        when(repository.findAll()).thenReturn(List.of(r1, r2, r3));

        List<VacationRequest> result = service.getRequestsForEmployee(10);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(r -> r.getEmployeeId() == 10));

        verify(repository).findAll();
    }

    /**
     * Tests that getApprovedRequestsForEmployee() returns only
     * APPROVED requests for the selected employee.
     */
    @Test
    void shouldReturnApprovedRequestsForEmployee() {
        VacationRequest approved = createRequest(1, 10, "Marco", VacationStatus.APPROVED);
        VacationRequest pending = createRequest(2, 10, "Marco", VacationStatus.PENDING);
        VacationRequest otherEmployee = createRequest(3, 20, "Elena", VacationStatus.APPROVED);

        when(repository.findAll()).thenReturn(List.of(approved, pending, otherEmployee));

        List<VacationRequest> result =
                service.getApprovedRequestsForEmployee(10);

        assertEquals(1, result.size());
        assertEquals(VacationStatus.APPROVED, result.get(0).getStatus());
        assertEquals(10, result.get(0).getEmployeeId());

        verify(repository).findAll();
    }

    /**
     * Tests that deleteRequest() deletes an existing request
     * and creates an audit log entry.
     */
    @Test
    void shouldDeleteRequestSuccessfully() {
        VacationRequest request = createRequest(1, 10, "Marco", VacationStatus.PENDING);

        when(repository.findById(1)).thenReturn(Optional.of(request));

        service.deleteRequest(1);

        verify(repository).findById(1);
        verify(repository).deleteById(1);
        verify(auditService).log(
                eq("DELETE_VACATION_REQUEST"),
                eq("VacationRequest"),
                eq("1"),
                contains("Deleted vacation request")
        );
    }

    /**
     * Tests that deleteRequest() still calls deleteById()
     * but does not log anything when the request does not exist.
     */
    @Test
    void shouldDeleteNonExistingRequestWithoutAuditLog() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        service.deleteRequest(99);

        verify(repository).findById(99);
        verify(repository).deleteById(99);
        verifyNoInteractions(auditService);
    }

    /**
     * Tests that hasOverlappingVacation() returns true
     * when a pending or approved request overlaps the given dates.
     */
    @Test
    void shouldDetectOverlappingVacation() {
        VacationRequest request = new VacationRequest(
                1,
                10,
                "Marco",
                LocalDate.of(2026, 7, 10),
                LocalDate.of(2026, 7, 15),
                "Holiday",
                VacationStatus.APPROVED
        );

        when(repository.findAll()).thenReturn(List.of(request));

        boolean result = service.hasOverlappingVacation(
                10,
                LocalDate.of(2026, 7, 14),
                LocalDate.of(2026, 7, 20)
        );

        assertTrue(result);

        verify(repository).findAll();
    }

    /**
     * Tests that hasOverlappingVacation() ignores denied requests
     * even if their dates overlap.
     */
    @Test
    void shouldIgnoreDeniedVacationWhenCheckingOverlap() {
        VacationRequest request = new VacationRequest(
                1,
                10,
                "Marco",
                LocalDate.of(2026, 7, 10),
                LocalDate.of(2026, 7, 15),
                "Holiday",
                VacationStatus.DENIED
        );

        when(repository.findAll()).thenReturn(List.of(request));

        boolean result = service.hasOverlappingVacation(
                10,
                LocalDate.of(2026, 7, 14),
                LocalDate.of(2026, 7, 20)
        );

        assertFalse(result);

        verify(repository).findAll();
    }

    /**
     * Tests that getTotalVacationDaysInYear() sums only approved
     * vacation days for the requested employee and year.
     */
    @Test
    void shouldReturnTotalVacationDaysInYear() {
        VacationRequest r1 = new VacationRequest(
                1,
                10,
                "Marco",
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 5),
                "Holiday",
                VacationStatus.APPROVED
        );

        VacationRequest r2 = new VacationRequest(
                2,
                10,
                "Marco",
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 3),
                "Holiday",
                VacationStatus.APPROVED
        );

        VacationRequest pending = new VacationRequest(
                3,
                10,
                "Marco",
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 10),
                "Holiday",
                VacationStatus.PENDING
        );

        when(repository.findAll()).thenReturn(List.of(r1, r2, pending));

        long result = service.getTotalVacationDaysInYear(10, 2026);

        assertEquals(8, result);

        verify(repository).findAll();
    }

    private VacationRequest createRequest(
            int id,
            int employeeId,
            String employeeName,
            VacationStatus status
    ) {
        return new VacationRequest(
                id,
                employeeId,
                employeeName,
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 5),
                "Reason",
                status
        );
    }
}