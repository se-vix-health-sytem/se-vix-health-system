package com.nvivx.vixhealthsystem.model.facility;

import com.nvivx.vixhealthsystem.model.person.employee.Employee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
@Getter
@Setter
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

}