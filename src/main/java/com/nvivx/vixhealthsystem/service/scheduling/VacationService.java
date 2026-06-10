package com.nvivx.vixhealthsystem.service.scheduling;

import com.nvivx.vixhealthsystem.exception.VacationNotFoundException;
import com.nvivx.vixhealthsystem.model.staff.VacationRequest;
import com.nvivx.vixhealthsystem.repository.JsonVacationRepository;
import com.nvivx.vixhealthsystem.service.AuditService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing employee vacation requests (UC29, FR6.2).
 * Requests are stored in vacations.json — the staff manager
 * adds and approves them manually through the dashboard.
 */
@Service
public class VacationService {

    private final JsonVacationRepository repository;
    private final AuditService auditService;

    public VacationService(JsonVacationRepository repository, AuditService auditService) {
        this.repository = repository;
        this.auditService = auditService;
    }

    /**
     * Creates a new vacation request with PENDING status.
     * UC29 — staff manager manually adds vacation requests on behalf of employees.
     */
    public VacationRequest addVacationRequest(int employeeId, String employeeName,
                                              LocalDate startDate, LocalDate endDate,
                                              String reason) {
        VacationRequest request = new VacationRequest(
                0, // ID assigned by repository
                employeeId,
                employeeName,
                startDate,
                endDate,
                reason,
                "PENDING"
        );
        VacationRequest saved = repository.save(request);

        auditService.log("CREATE_VACATION_REQUEST", "VacationRequest", String.valueOf(saved.getId()),
                "Created vacation request for employee: " + employeeName + " (" + startDate + " to " + endDate + ")");

        return saved;
    }

    /**
     * Approves a vacation request. UC29.
     */
    public VacationRequest approveVacation(int id) {
        VacationRequest request = repository.findById(id)
                .orElseThrow(() -> new VacationNotFoundException("Vacation request not found: " + id));
        request.setStatus("APPROVED");
        VacationRequest saved = repository.save(request);

        auditService.log("APPROVE_VACATION", "VacationRequest", String.valueOf(id),
                "Approved vacation for employee: " + request.getEmployeeName());

        return saved;
    }

    /**
     * Denies a vacation request. UC29.
     */
    public VacationRequest denyVacation(int id) {
        VacationRequest request = repository.findById(id)
                .orElseThrow(() -> new VacationNotFoundException("Vacation request not found: " + id));
        request.setStatus("DENIED");
        VacationRequest saved = repository.save(request);

        auditService.log("DENY_VACATION", "VacationRequest", String.valueOf(id),
                "Denied vacation for employee: " + request.getEmployeeName());

        return saved;
    }

    /**
     * Returns all vacation requests.
     */
    public List<VacationRequest> getAllRequests() {
        return repository.findAll();
    }

    /**
     * Returns only pending requests.
     */
    public List<VacationRequest> getPendingRequests() {
        return repository.findAll().stream()
                .filter(v -> "PENDING".equals(v.getStatus()))
                .collect(Collectors.toList());
    }

    /**
     * Returns all requests for a specific employee.
     */
    public List<VacationRequest> getRequestsForEmployee(int employeeId) {
        return repository.findAll().stream()
                .filter(v -> v.getEmployeeId() == employeeId)
                .collect(Collectors.toList());
    }

    /**
     * Returns approved requests for a specific employee.
     */
    public List<VacationRequest> getApprovedRequestsForEmployee(int employeeId) {
        return repository.findAll().stream()
                .filter(v -> v.getEmployeeId() == employeeId && "APPROVED".equals(v.getStatus()))
                .collect(Collectors.toList());
    }

    /**
     * Deletes a vacation request.
     */
    public void deleteRequest(int id) {
        VacationRequest request = repository.findById(id).orElse(null);
        repository.deleteById(id);

        if (request != null) {
            auditService.log("DELETE_VACATION_REQUEST", "VacationRequest", String.valueOf(id),
                    "Deleted vacation request for employee: " + request.getEmployeeName());
        }
    }

    /**
     * Check if an employee has overlapping vacation requests
     */
    public boolean hasOverlappingVacation(int employeeId, LocalDate startDate, LocalDate endDate) {
        return getRequestsForEmployee(employeeId).stream()
                .filter(v -> "APPROVED".equals(v.getStatus()) || "PENDING".equals(v.getStatus()))
                .anyMatch(v ->
                        !(v.getEndDate().isBefore(startDate) || v.getStartDate().isAfter(endDate))
                );
    }

    /**
     * Get total vacation days taken by an employee in a year (approved only)
     */
    public long getTotalVacationDaysInYear(int employeeId, int year) {
        return getRequestsForEmployee(employeeId).stream()
                .filter(v -> "APPROVED".equals(v.getStatus()))
                .filter(v -> v.getStartDate().getYear() == year)
                .mapToLong(VacationRequest::getDaysRequested)
                .sum();
    }
}