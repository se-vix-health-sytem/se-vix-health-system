package com.nvivx.vixhealthsystem.model.facility;

import com.nvivx.vixhealthsystem.model.person.employee.Employee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
@Getter
@Setter
@Entity
@DiscriminatorValue("OFFICE")
public class Office extends Room {


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
     * @param number room number
     * @param employee assigned employee
     */
    public Office(String number, Employee employee) {
        super(number);
        this.employee = employee;
    }

    // =====================================================
    // OFFICE MANAGEMENT METHODS
    // =====================================================

    /**
     * Assigns an employee to the office.
     *
     * @param e employee to assign
     */
    public void assignEmployee(Employee e) {
        this.employee = e;
    }

}