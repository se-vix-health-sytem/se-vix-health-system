package com.nvivx.vixhealthsystem.model.facility;

import com.nvivx.vixhealthsystem.model.person.employee.Employee;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a department within a medical facility.
 *
 * A department groups employees working in the same medical area,
 * such as Cardiology, Neurology, Radiology or Emergency Care.
 *
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
    // GETTERS & SETTERS
    // =====================================================

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public MedicalFacility getMedicalFacility() {
        return medicalFacility;
    }

    public void setMedicalFacility(MedicalFacility medicalFacility) {
        this.medicalFacility = medicalFacility;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }
}