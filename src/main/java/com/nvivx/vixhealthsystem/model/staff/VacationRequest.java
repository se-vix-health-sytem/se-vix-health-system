package com.nvivx.vixhealthsystem.model.staff;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nvivx.vixhealthsystem.model.enums.VacationStatus;
import java.time.LocalDate;

/**
 * Represents a vacation request submitted by an employee.
 * <p>
 * Vacation requests are stored as JSON (vacations.json), not in the SQL database.
 * The staff manager reviews and approves or denies requests manually.
 *
 * @see com.nvivx.vixhealthsystem.model.person.employee.StaffManager
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VacationRequest {

    /**
     * Unique vacation request identifier.
     */
    private int id;

    /**
     * Identifier of the employee submitting the request.
     */
    private int employeeId;

    /**
     * Employee full name, denormalized for display purposes.
     */
    private String employeeName;

    /**
     * Start date of the requested vacation period.
     */
    private LocalDate startDate;

    /**
     * End date of the requested vacation period.
     */
    private LocalDate endDate;

    /**
     * Reason provided by the employee for the vacation request.
     */
    private String reason;

    /**
     * Current status of the request.
     */
    private VacationStatus status;

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    /**
     * Default constructor required for JSON deserialization.
     */
    public VacationRequest() {
    }

    /**
     * Creates a vacation request with all specified details.
     *
     * @param id           the request identifier
     * @param employeeId   the employee identifier
     * @param employeeName the employee full name
     * @param startDate    the start date of the vacation
     * @param endDate      the end date of the vacation
     * @param reason       the reason for the request
     * @param status       the initial status (e.g. PENDING)
     */
    public VacationRequest(
            int id,
            int employeeId,
            String employeeName,
            LocalDate startDate,
            LocalDate endDate,
            String reason,
            VacationStatus status
    ) {
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
     * Returns the total number of days requested, inclusive of both endpoints.
     *
     * @return number of vacation days, or 0 if dates are not set
     */
    public long getDaysRequested() {
        if (startDate == null || endDate == null) {
            return 0;
        }
        return startDate.until(endDate).getDays() + 1;
    }

    // =====================================================
    // GETTERS & SETTERS
    // =====================================================

    /// @cond INTERNAL
    /**
     * Returns the unique vacation request identifier.
     *
     * @return the request ID
     */
    public int getId() {
        return id;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the unique vacation request identifier.
     *
     * @param id the request ID to set
     */
    public void setId(int id) {
        this.id = id;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Returns the identifier of the employee submitting the request.
     *
     * @return the employee ID
     */
    public int getEmployeeId() {
        return employeeId;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the identifier of the employee submitting the request.
     *
     * @param employeeId the employee ID to set
     */
    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Returns the employee full name.
     *
     * @return the employee name
     */
    public String getEmployeeName() {
        return employeeName;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the employee full name.
     *
     * @param employeeName the employee name to set
     */
    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Returns the start date of the vacation period.
     *
     * @return the start date
     */
    public LocalDate getStartDate() {
        return startDate;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the start date of the vacation period.
     *
     * @param startDate the start date to set
     */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Returns the end date of the vacation period.
     *
     * @return the end date
     */
    public LocalDate getEndDate() {
        return endDate;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the end date of the vacation period.
     *
     * @param endDate the end date to set
     */
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Returns the reason provided for the vacation request.
     *
     * @return the reason
     */
    public String getReason() {
        return reason;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the reason provided for the vacation request.
     *
     * @param reason the reason to set
     */
    public void setReason(String reason) {
        this.reason = reason;
    }
    /// @endcond

    /**
     * Returns the vacation status enum.
     */
    public VacationStatus getStatus() {
        return status;
    }

    /**
     * Returns the vacation status as a String (backward-compatible for templates).
     */
    public String getStatusName() {
        return status != null ? status.name() : null;
    }

    /**
     * Sets the vacation status using the enum.
     */
    public void setStatus(VacationStatus status) {
        this.status = status;
    }

    /**
     * Sets the vacation status from a String (backward-compatible for JSON deserialization).
     */
    public void setStatus(String status) {
        this.status = status != null ? VacationStatus.valueOf(status) : null;
    }
}
