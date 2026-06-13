package com.nvivx.vixhealthsystem.model.facility;

import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DepartmentTest {
    private Department department;

    @BeforeEach
    void setUp() {
        department = new Department();
        department.setId(1L);
        department.setName("Cardiology");
        department.setDescription("Heart and cardiovascular care");
        department.setEmail("cardiology@vixhealth.com");
        department.setPhoneNumber("+39 0461 123456");
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        assertEquals(1L, department.getId());
        assertEquals("Cardiology", department.getName());
        assertEquals("Heart and cardiovascular care", department.getDescription());
        assertEquals("cardiology@vixhealth.com", department.getEmail());
        assertEquals("+39 0461 123456", department.getPhoneNumber());
    }

    @Test
    void medicalFacility_ShouldBeAssignable() {
        MedicalFacility facility = new MedicalFacility();
        facility.setId(10L);
        department.setMedicalFacility(facility);

        assertEquals(facility, department.getMedicalFacility());
    }

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