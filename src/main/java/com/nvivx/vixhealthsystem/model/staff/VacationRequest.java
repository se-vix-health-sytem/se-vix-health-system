package com.nvivx.vixhealthsystem.model.staff;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;

/**
 * Represents a vacation request from an employee.
 * Stored as JSON (vacations.json), not in the SQL database.
 * The staff manager adds/approves these manually.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VacationRequest {

    private int id;
    private int employeeId;
    private String employeeName;  // denormalized for display
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private String status;        // PENDING, APPROVED, DENIED

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    public VacationRequest() {}

    public VacationRequest(int id, int employeeId, String employeeName,
                           LocalDate startDate, LocalDate endDate,
                           String reason, String status) {
        this.id = id;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.status = status;
    }

    // =====================================================
    // COMPUTED FIELDS
    // =====================================================

    /**
     * Returns the number of days requested (inclusive).
     */
    public long getDaysRequested() {
        if (startDate == null || endDate == null) return 0;
        return startDate.until(endDate).getDays() + 1;
    }

    // =====================================================
    // GETTERS & SETTERS
    // =====================================================

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}