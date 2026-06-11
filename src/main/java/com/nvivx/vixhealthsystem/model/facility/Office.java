package com.nvivx.vixhealthsystem.model.facility;

import com.nvivx.vixhealthsystem.model.person.employee.Employee;
import jakarta.persistence.*;

/**
 * Represents an office assigned to an employee.
 * <p>
 * Offices are rooms used by employees to perform
 * administrative, managerial or medical activities.
 * <p>
 * Each office can be assigned to at most one employee.
 *
 * @see Room
 * @see Employee
 */
@Entity
@DiscriminatorValue("OFFICE")
public class Office extends Room {

    /** Employee assigned to this office. */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    /**
     * Default constructor required by JPA.
     */
    public Office() {
    }

    /**
     * Creates an office and assigns an employee.
     *
     * @param number the room number
     * @param employee the employee to assign
     */
    public Office(String number, Employee employee) {
        super(number);
        this.employee = employee;
    }

    // =====================================================
    // GETTERS & SETTERS
    // =====================================================

    /**
     * Returns the employee assigned to this office.
     *
     * @return the assigned employee, or null if unassigned
     */
    public Employee getEmployee() {
        return employee;
    }

    /**
     * Sets the employee assigned to this office.
     *
     * @param employee the employee to assign
     */
    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    // =====================================================
    // OFFICE MANAGEMENT METHODS
    // =====================================================

    /**
     * Assigns an employee to the office.
     *
     * @param e the employee to assign
     */
    public void assignEmployee(Employee e) {
        this.employee = e;
    }
}