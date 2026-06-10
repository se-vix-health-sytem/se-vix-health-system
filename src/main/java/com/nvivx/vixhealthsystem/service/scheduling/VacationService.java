package com.nvivx.vixhealthsystem.service.scheduling;

import com.nvivx.vixhealthsystem.exception.VacationNotFoundException;
import com.nvivx.vixhealthsystem.model.staff.VacationRequest;
import com.nvivx.vixhealthsystem.repository.JsonVacationRepository;
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

    public VacationService(JsonVacationRepository repository) {
        this.repository = repository;
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
        return repository.save(request);
    }

    /**
     * Approves a vacation request. UC29.
     */
    public VacationRequest approveVacation(int id) {
        VacationRequest request = repository.findById(id)
                .orElseThrow(() -> new VacationNotFoundException("Vacation request not found: " + id));
        request.setStatus("APPROVED");
        return repository.save(request);
    }

    /**
     * Denies a vacation request. UC29.
     */
    public VacationRequest denyVacation(int id) {
        VacationRequest request = repository.findById(id)
                .orElseThrow(() -> new VacationNotFoundException("Vacation request not found: " + id));
        request.setStatus("DENIED");
        return repository.save(request);
    }

    /** Returns all vacation requests. */
    public List<VacationRequest> getAllRequests() {
        return repository.findAll();
    }

    /** Returns only pending requests. */
    public List<VacationRequest> getPendingRequests() {
        return repository.findAll().stream()
                .filter(v -> "PENDING".equals(v.getStatus()))
                .collect(Collectors.toList());
    }

    /** Returns all requests for a specific employee. */
    public List<VacationRequest> getRequestsForEmployee(int employeeId) {
        return repository.findAll().stream()
                .filter(v -> v.getEmployeeId() == employeeId)
                .collect(Collectors.toList());
    }

    /** Deletes a vacation request. */
    public void deleteRequest(int id) {
        repository.deleteById(id);
    }
}