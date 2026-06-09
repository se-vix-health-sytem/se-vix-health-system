package com.nvivx.vixhealthsystem.model.person.employee;
import com.nvivx.vixhealthsystem.model.facility.Department;
import com.nvivx.vixhealthsystem.model.facility.MedicalFacility;
import com.nvivx.vixhealthsystem.model.person.Person;
import com.nvivx.vixhealthsystem.model.resource.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Represents a generic employee in the system.
 * All specific employee roles inherit from this class.
 */

public class Employee extends Person {
    private long id;
    private LocalDate hireDate;
    private Department department;

    /**
     * Gets the employee's unique ID.
     * This ID serves as the primary key in the database.
     *
     * @return the employee's ID
     */

    public long getId() {
        return id;
    }

    /**
     * Sets the employee's unique ID.
     *
     * @param id the ID to assign
     */

    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets the date when the employee was hired.
     *
     * @return the hire date
     */

    public LocalDate getHireDate() {
        return hireDate;
    }

    /**
     * Sets the employee's hire date.
     *
     * @param hireDate the date to set
     */

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    /**
     * Returns an array of resources (machines, equipment) that this employee can access.
     * Protected because only hospital system classes should call this.
     *
     * @return array of available resources, or null if none
     */

    protected List<Resource> viewResources() {
        return null;
    }

    /**
     * Allows an employee to take/borrow a resource from storage.
     * Protected because only hospital system classes should call this.
     *
     * @param r the resource to take
     */

    protected void takeResource(Resource r, int q) throws Exception {

    }
}
