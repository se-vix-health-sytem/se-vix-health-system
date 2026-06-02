package com.nvivx.vixhealthsystem.model.person.employee;
import com.nvivx.vixhealthsystem.model.person.Person;
import com.nvivx.vixhealthsystem.model.resource.Resource;
import java.time.LocalDate;

/**
 * Represents a generic employee in the system.
 * All specific employee roles inherit from this class.
 */

public class Employee extends Person {
    private int id;
    private LocalDate hireDate;

    /**
     * Gets the employee's unique ID.
     * This ID serves as the primary key in the database.
     *
     * @return the employee's ID
     */

    public int getId() {
        return id;
    }

    /**
     * Sets the employee's unique ID.
     *
     * @param id the ID to assign
     */

    public void setId(int id) {
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

    protected Resource[] viewResources() {

        // Will query the database for resources assigned to this employee

        return null;
    }

    /**
     * Allows an employee to take/borrow a resource from storage.
     * Protected because only hospital system classes should call this.
     *
     * @param r the resource to take
     */

    protected void takeResource(Resource r){

        // Will update database to mark this resource as taken by the employee

    }
}
