package com.nvivx.vixhealthsystem.model.person.employee;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nvivx.vixhealthsystem.model.enums.EmployeeType;
import com.nvivx.vixhealthsystem.model.enums.Role;
import com.nvivx.vixhealthsystem.model.facility.Department;
import com.nvivx.vixhealthsystem.model.person.Person;
import com.nvivx.vixhealthsystem.model.resource.Resource;
import com.nvivx.vixhealthsystem.model.resource.Storage;
import jakarta.persistence.*;

import java.time.LocalDate;

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
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, ignoreUnknown = true)
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
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    // =====================================================
    // GETTERS & SETTERS
    // =====================================================

    /// @cond INTERNAL
    /**
     * Returns the employee unique identifier.
     *
     * @return the employee ID
     */
    public Long getId() {
        return id;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the employee unique identifier.
     *
     * @param id the employee ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Returns the employee unique identifier used in firebase.
     *
     * @return the employee firebase user id
     */
    public String getFirebaseUid() {
        return firebaseUid;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the employee unique identifier.
     *
     * @param firebaseUid the employee Firebase User ID to set
     */
    public void setFirebaseUid(String firebaseUid) {
        this.firebaseUid = firebaseUid;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Returns the date when the employee was hired.
     *
     * @return the hire date
     */
    public LocalDate getHireDate() {
        return hireDate;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the date when the employee was hired.
     *
     * @param hireDate the hire date to set
     */
    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Returns the department where the employee works.
     *
     * @return the department
     */
    public Department getDepartment() {
        return department;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the department where the employee works.
     *
     * @param department the department to set
     */
    public void setDepartment(Department department) {
        this.department = department;
    }
    /// @endcond

    // =====================================================
    // DOMAIN METHODS
    // =====================================================

    /**
     * Returns the system authentication role for this employee type.
     */
    public abstract Role getSystemRole();

    /**
     * Returns the employee type enum identifying this employee's role in the organisation.
     */
    public abstract EmployeeType getEmployeeType();

    /**
     * Takes a quantity of a resource from the facility storage,
     * navigating through the employee's own department → facility → storage chain.
     *
     * @param r the resource to take
     * @param q the quantity to take
     * @throws IllegalStateException if department, facility or storage is not set
     * @throws Exception if the storage has insufficient stock
     */
    /**
     * Returns whether this employee has a facility storage they can draw from.
     *
     * @return {@code true} when department, facility, and storage are all set.
     */
    public boolean hasFacilityStorage() {
        return getDepartment() != null
                && getDepartment().getMedicalFacility() != null
                && getDepartment().getMedicalFacility().getStorage() != null;
    }

    /**
     * Returns the storage unit belonging to this employee's facility.
     *
     * @return the facility's {@link Storage}
     * @throws IllegalStateException if department, facility, or storage is not set
     */
    public Storage getFacilityStorage() {
        if (!hasFacilityStorage()) {
            throw new IllegalStateException("Employee has no department/facility/storage assigned");
        }
        return getDepartment().getMedicalFacility().getStorage();
    }

    /**
     * Returns the name of the medical facility this employee belongs to.
     *
     * @return facility name, or {@code null} if no facility is assigned
     */
    public String getFacilityName() {
        if (getDepartment() == null || getDepartment().getMedicalFacility() == null) return null;
        return getDepartment().getMedicalFacility().getName();
    }

    /**
     * Withdraws a quantity of a resource from this employee's facility storage.
     *
     * @param r the resource to take
     * @param q the quantity to take
     * @throws IllegalStateException if department, facility or storage is not set
     * @throws Exception if the storage has insufficient stock
     */
    public void takeResource(Resource r, int q) throws Exception {
        getFacilityStorage().removeResource(r, q);
    }
}