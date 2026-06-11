package com.nvivx.vixhealthsystem.model.person.employee;

import com.nvivx.vixhealthsystem.model.facility.Department;
import com.nvivx.vixhealthsystem.model.person.Person;
import com.nvivx.vixhealthsystem.model.resource.Resource;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents a generic employee in the system.
 * <p>
 * All specific employee roles inherit from this class.
 * This class is mapped using Single Table Inheritance, meaning that all
 * employee types are stored inside the same Employees table.
 * <p>
 * The "type" discriminator column is used by JPA to determine which
 * subclass an employee row belongs to.
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
     * Firebase unique user identifier.
     * Used to link the employee to an external Firebase account.
     */
    @Column(name = "firebase_uid", unique = true)
    private String firebaseUid;

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

    /**
     * Returns the employee unique identifier.
     *
     * @return the employee ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the employee unique identifier.
     *
     * @param id the employee ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the employee unique identifier used in firebase.
     *
     * @return the employee firebase user id
     */
    public String getFirebaseUid() {
        return firebaseUid;
    }

    /**
     * Sets the employee unique identifier.
     *
     * @param firebaseUid the employee Firebase User ID to set
     */
    public void setFirebaseUid(String firebaseUid) {
        this.firebaseUid = firebaseUid;
    }

    /**
     * Returns the date when the employee was hired.
     *
     * @return the hire date
     */
    public LocalDate getHireDate() {
        return hireDate;
    }

    /**
     * Sets the date when the employee was hired.
     *
     * @param hireDate the hire date to set
     */
    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    /**
     * Returns the department where the employee works.
     *
     * @return the department
     */
    public Department getDepartment() {
        return department;
    }

    /**
     * Sets the department where the employee works.
     *
     * @param department the department to set
     */
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