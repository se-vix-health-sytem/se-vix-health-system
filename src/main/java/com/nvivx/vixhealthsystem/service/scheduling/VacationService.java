package com.nvivx.vixhealthsystem.service.scheduling;

import com.nvivx.vixhealthsystem.exception.VacationNotFoundException;
import com.nvivx.vixhealthsystem.model.enums.VacationStatus;
import com.nvivx.vixhealthsystem.model.staff.VacationRequest;
import com.nvivx.vixhealthsystem.repository.JsonVacationRepository;
import com.nvivx.vixhealthsystem.service.AuditService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages employee vacation requests from creation through approval/denial (UC29, FR6.2).
 *
 * Requests are stored in {@code vacations.json} via {@link JsonVacationRepository} rather
 * than the relational database, keeping vacation data separate from the core HR schema.
 * The staff manager is the only actor who can add, approve, deny, or delete requests —
 * employees themselves cannot self-serve vacation through this system.
 *
 * @see ShiftService
 * @see com.nvivx.vixhealthsystem.model.staff.VacationRequest
 * @see AuditService
 */
@Service
public class VacationService {

    // =========================================================
    // FIELDS
    // =========================================================

    private final JsonVacationRepository repository;
    private final AuditService auditService;

    // =========================================================
    // CONSTRUCTORS
    // =========================================================

    /**
     * Constructs the service with the JSON repository and audit collaborator.
     *
     * @param repository    File-backed repository for {@link VacationRequest} entities.
     * @param auditService  Records every state change for traceability (NFR02).
     */
    public VacationService(JsonVacationRepository repository, AuditService auditService) {
        this.repository = repository;
        this.auditService = auditService;
    }

    // =========================================================
    // WRITE OPERATIONS
    // =========================================================

    /**
     * Creates a new vacation request with {@link VacationStatus#PENDING} status (UC29).
     *
     * The staff manager calls this on behalf of the employee; the request then waits for
     * approval or denial.
     *
     * @param employeeId    Database ID of the requesting employee.
     * @param employeeName  Display name used in audit log entries.
     * @param startDate     First day of the requested vacation period (inclusive).
     * @param endDate       Last day of the requested vacation period (inclusive).
     * @param reason        Free-text reason provided by the employee.
     * @return              The saved {@link VacationRequest} with a generated ID.
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
                VacationStatus.PENDING
        );
        VacationRequest saved = repository.save(request);

        auditService.log("CREATE_VACATION_REQUEST", "VacationRequest", String.valueOf(saved.getId()),
                "Created vacation request for employee: " + employeeName + " (" + startDate + " to " + endDate + ")");

        return saved;
    }

    /**
     * Transitions a vacation request to {@link VacationStatus#APPROVED} (UC29).
     *
     * @param id  ID of the request to approve.
     * @return    The updated {@link VacationRequest}.
     * @throws com.nvivx.vixhealthsystem.exception.VacationNotFoundException When the ID does not exist.
     */
    public VacationRequest approveVacation(int id) {
        VacationRequest request = repository.findById(id)
                .orElseThrow(() -> new VacationNotFoundException("Vacation request not found: " + id));
        request.setStatus(VacationStatus.APPROVED);
        VacationRequest saved = repository.save(request);

        auditService.log("APPROVE_VACATION", "VacationRequest", String.valueOf(id),
                "Approved vacation for employee: " + request.getEmployeeName());

        return saved;
    }

    /**
     * Transitions a vacation request to {@link VacationStatus#DENIED} (UC29).
     *
     * @param id  ID of the request to deny.
     * @return    The updated {@link VacationRequest}.
     * @throws com.nvivx.vixhealthsystem.exception.VacationNotFoundException When the ID does not exist.
     */
    public VacationRequest denyVacation(int id) {
        VacationRequest request = repository.findById(id)
                .orElseThrow(() -> new VacationNotFoundException("Vacation request not found: " + id));
        request.setStatus(VacationStatus.DENIED);
        VacationRequest saved = repository.save(request);

        auditService.log("DENY_VACATION", "VacationRequest", String.valueOf(id),
                "Denied vacation for employee: " + request.getEmployeeName());

        return saved;
    }

    // =========================================================
    // READ OPERATIONS
    // =========================================================

    /** @brief Returns all vacation requests across all employees and all statuses. */
    public List<VacationRequest> getAllRequests() {
        return repository.findAll();
    }

    /** @brief Returns only requests that are still waiting for a decision. */
    public List<VacationRequest> getPendingRequests() {
        return repository.findAll().stream()
                .filter(v -> v.getStatus() == VacationStatus.PENDING)
                .collect(Collectors.toList());
    }

    /**
     * Returns all requests submitted for a specific employee (any status).
     *
     * @param employeeId  Database ID of the employee.
     * @return            Non-null list; empty if the employee has no requests.
     */
    public List<VacationRequest> getRequestsForEmployee(int employeeId) {
        return repository.findAll().stream()
                .filter(v -> v.getEmployeeId() == employeeId)
                .collect(Collectors.toList());
    }

    /**
     * Returns only the approved requests for a specific employee.
     *
     * @param employeeId  Database ID of the employee.
     * @return            Non-null list of approved requests; empty if none found.
     */
    public List<VacationRequest> getApprovedRequestsForEmployee(int employeeId) {
        return repository.findAll().stream()
                .filter(v -> v.getEmployeeId() == employeeId && v.getStatus() == VacationStatus.APPROVED)
                .collect(Collectors.toList());
    }

    /**
     * Checks whether an employee has any PENDING or APPROVED request that
     *        overlaps the given date range.
     *
     * Used before creating a new request to warn the staff manager about double-booking.
     *
     * @param employeeId  Database ID of the employee.
     * @param startDate   Start of the proposed period (inclusive).
     * @param endDate     End of the proposed period (inclusive).
     * @return            {@code true} if at least one overlapping request exists.
     */
    public boolean hasOverlappingVacation(int employeeId, LocalDate startDate, LocalDate endDate) {
        return getRequestsForEmployee(employeeId).stream()
                .filter(v -> v.getStatus() == VacationStatus.APPROVED || v.getStatus() == VacationStatus.PENDING)
                .anyMatch(v ->
                        !(v.getEndDate().isBefore(startDate) || v.getStartDate().isAfter(endDate))
                );
    }

    /**
     * Counts the total approved vacation days taken by an employee in a calendar year.
     *
     * Only approved requests are counted; PENDING and DENIED requests are excluded.
     * The year filter is based on the request's start date.
     *
     * @param employeeId  Database ID of the employee.
     * @param year        Calendar year to aggregate (e.g., {@code 2025}).
     * @return            Total number of approved days in that year.
     */
    public long getTotalVacationDaysInYear(int employeeId, int year) {
        return getRequestsForEmployee(employeeId).stream()
                .filter(v -> v.getStatus() == VacationStatus.APPROVED)
                .filter(v -> v.getStartDate().getYear() == year)
                .mapToLong(VacationRequest::getDaysRequested)
                .sum();
    }

    // =========================================================
    // DELETE OPERATIONS
    // =========================================================

    /**
     * Removes a vacation request entirely from the JSON store.
     *
     * @param id  ID of the request to delete.
     */
    public void deleteRequest(int id) {
        VacationRequest request = repository.findById(id).orElse(null);
        repository.deleteById(id);

        if (request != null) {
            auditService.log("DELETE_VACATION_REQUEST", "VacationRequest", String.valueOf(id),
                    "Deleted vacation request for employee: " + request.getEmployeeName());
        }
    }

}