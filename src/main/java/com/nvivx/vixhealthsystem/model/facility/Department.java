package com.nvivx.vixhealthsystem.model.facility;

import com.nvivx.vixhealthsystem.model.person.employee.Employee;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a department within a medical facility.
 * <p>
 * A department groups employees working in the same medical area,
 * such as Cardiology, Neurology, Radiology or Emergency Care.
 * <p>
 * Each department belongs to a single medical facility and can
 * contain multiple employees.
 *
 * @see MedicalFacility
 * @see Employee
 */
@Entity
@Table(name = "Departments")
public class Department {

    /**
     * Unique department identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Department name.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Department description.
     */
    @Column(name = "description")
    private String description;

    /**
     * Department contact email.
     */
    @Column(name = "email")
    private String email;

    /**
     * Department contact phone number.
     */
    @Column(name = "phone")
    private String phoneNumber;

    /**
     * Medical facility to which this department belongs.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", nullable = false)
    private MedicalFacility medicalFacility;

    /**
     * Employees assigned to this department.
     */
    @OneToMany(mappedBy = "department")
    private List<Employee> employees = new ArrayList<>();

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    /**
     * Default constructor required by JPA.
     */
    public Department() {
    }

    // =====================================================
    // GETTERS & SETTERS
    // =====================================================

    /// @cond INTERNAL
    /**
     * Returns the unique department identifier.
     *
     * @return the department ID
     */
    public Long getId() {
        return id;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the unique department identifier.
     *
     * @param id the department ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Returns the department name.
     *
     * @return the department name
     */
    public String getName() {
        return name;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the department name.
     *
     * @param name the department name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Returns the department description.
     *
     * @return the department description
     */
    public String getDescription() {
        return description;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the department description.
     *
     * @param description the department description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Returns the department contact email.
     *
     * @return the department email
     */
    public String getEmail() {
        return email;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the department contact email.
     *
     * @param email the department email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Returns the department contact phone number.
     *
     * @return the department phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the department contact phone number.
     *
     * @param phoneNumber the department phone number to set
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Returns the medical facility to which this department belongs.
     *
     * @return the medical facility
     */
    public MedicalFacility getMedicalFacility() {
        return medicalFacility;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the medical facility to which this department belongs.
     *
     * @param medicalFacility the medical facility to set
     */
    public void setMedicalFacility(MedicalFacility medicalFacility) {
        this.medicalFacility = medicalFacility;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Returns the list of employees assigned to this department.
     *
     * @return the list of employees
     */
    public List<Employee> getEmployees() {
        return employees;
    }
    /// @endcond

    /// @cond INTERNAL
    /**
     * Sets the list of employees assigned to this department.
     *
     * @param employees the list of employees to set
     */
    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }
    /// @endcond
}