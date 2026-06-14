package com.nvivx.vixhealthsystem.model.facility;

import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @brief Unit tests for Department.
 *
 * Checks that a department stores its identity fields, can be linked to a
 * MedicalFacility, and maintains a mutable employee list. Plain JUnit — no
 * Spring context loaded.
 *
 * @see Department
 */
class DepartmentTest {
    private Department department;

    /** @brief Builds a fully populated Department fixture shared by all tests. */
    @BeforeEach
    void setUp() {
        department = new Department();
        department.setId(1L);
        department.setName("Cardiology");
        department.setDescription("Heart and cardiovascular care");
        department.setEmail("cardiology@vixhealth.com");
        department.setPhoneNumber("+39 0461 123456");
    }

    /**
     * Verifies that all identity and contact fields round-trip through
     *        their setters and getters without data loss.
     */
    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        assertEquals(1L, department.getId());
        assertEquals("Cardiology", department.getName());
        assertEquals("Heart and cardiovascular care", department.getDescription());
        assertEquals("cardiology@vixhealth.com", department.getEmail());
        assertEquals("+39 0461 123456", department.getPhoneNumber());
    }

    /**
     * Verifies that a MedicalFacility can be assigned to a department,
     *        establishing the ownership association.
     */
    @Test
    void medicalFacility_ShouldBeAssignable() {
        MedicalFacility facility = new MedicalFacility();
        facility.setId(10L);
        department.setMedicalFacility(facility);

        assertEquals(facility, department.getMedicalFacility());
    }

    /**
     * Verifies that employees can be added to the department list and
     *        that the bidirectional link (employee.department) is set on the
     *        employee side as well.
     */
    @Test
    void employees_ShouldBeManageable() {
        assertTrue(department.getEmployees().isEmpty());

        MedicalSpecialist specialist = new MedicalSpecialist();
        specialist.setDepartment(department);
        department.getEmployees().add(specialist);

        assertEquals(1, department.getEmployees().size());
        assertEquals(specialist, department.getEmployees().get(0));
    }
}