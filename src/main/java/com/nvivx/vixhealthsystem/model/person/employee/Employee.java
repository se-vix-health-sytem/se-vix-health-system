package com.nvivx.vixhealthsystem.model.person.employee;

import com.nvivx.vixhealthsystem.model.facility.Department;
import com.nvivx.vixhealthsystem.model.person.Person;
import com.nvivx.vixhealthsystem.model.resource.Resource;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents a generic employee in the system.
 * All specific employee roles inherit from this class.
 *
 * This class is mapped using Single Table Inheritance, meaning that all
 * employee types are stored inside the same Employees table.
 *
 * The "type" column is used by JPA to understand which subclass
 * an employee belongs to.
 *
 * @see Buyer
 * @see MedicalSpecialist
 * @see Secretary
 * @see StaffManager
 * @see Technician
 */
@Entity
@Table(name = "Employees")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
        name = "type",
        discriminatorType = DiscriminatorType.STRING
)
public abstract class Employee extends Person {

    /**
     * Employee unique identifier.
     * Primary key of the Employees table.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Date when the employee was hired.
     */
    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    /**
     * Department where the employee works.
     */
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    // =====================================================
    // GETTERS & SETTERS
    // =====================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    // =====================================================
    // DOMAIN METHODS
    // =====================================================

    /**
     * Returns the list of resources accessible by the employee.
     *
     * @return list of available resources, or null if not implemented yet
     */
    protected List<Resource> viewResources() {
        return null;
    }

    /**
     * Allows an employee to take or borrow a resource from storage.
     *
     * @param r the resource to take
     * @param q quantity to take
     * @throws Exception if the resource cannot be taken
     */
    protected void takeResource(Resource r, int q) throws Exception {

    }
}