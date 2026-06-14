package com.nvivx.vixhealthsystem.model.person.employee;

import com.nvivx.vixhealthsystem.model.enums.EmployeeType;
import com.nvivx.vixhealthsystem.model.enums.Role;
import com.nvivx.vixhealthsystem.model.facility.Department;
import com.nvivx.vixhealthsystem.model.facility.MedicalFacility;
import com.nvivx.vixhealthsystem.model.resource.Resource;
import com.nvivx.vixhealthsystem.model.resource.Storage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

// Concrete subclass for testing abstract Employee
class TestEmployee extends Employee {
    @Override
    public Role getSystemRole() { return Role.ROLE_MEDICAL_SPECIALIST; }
    @Override
    public EmployeeType getEmployeeType() { return EmployeeType.MEDICAL_SPECIALIST; }
}

/**
 * @brief Unit tests for the abstract Employee base class.
 *
 * Uses a minimal TestEmployee stub to exercise shared fields (hire date,
 * Firebase UID) and the takeResource domain method that withdraws stock from
 * facility storage. Plain JUnit — no Spring context loaded.
 *
 * @see Employee
 */
class EmployeeTest {
    private TestEmployee employee;
    private Department department;
    private MedicalFacility facility;
    private Storage storage;
    private Resource resource;

    /** @brief Builds the fixture shared by all tests in this class. */
    @BeforeEach
    void setUp() {
        employee = new TestEmployee();
        employee.setId(1L);
        employee.setName("Marco");
        employee.setSurname("Rossi");
        employee.setEmail("marco.rossi@vixhealth.com");
        employee.setHireDate(LocalDate.of(2020, 1, 15));

        department = new Department();
        department.setId(10L);
        department.setName("Cardiology");

        facility = new MedicalFacility();
        facility.setId(100L);
        facility.setName("Main Hospital");

        storage = new Storage();
        storage.setId(1000L);
        storage.setMedicalFacility(facility);

        facility.setStorage(storage);
        department.setMedicalFacility(facility);
        employee.setDepartment(department);

        resource = new Resource("Syringes", "Medical syringes", new BigDecimal("0.50"));
        resource.setId(500L);
    }

    /**
     * Verifies that employee identity and employment fields round-trip
     *        correctly including the department association.
     */
    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        assertEquals(1L, employee.getId());
        assertEquals("Marco", employee.getName());
        assertEquals("Rossi", employee.getSurname());
        assertEquals("marco.rossi@vixhealth.com", employee.getEmail());
        assertEquals(LocalDate.of(2020, 1, 15), employee.getHireDate());
        assertEquals(department, employee.getDepartment());
    }

    /**
     * Verifies that the Firebase UID starts null and can be set to an
     *        arbitrary string representing the auth identity.
     */
    @Test
    void firebaseUid_ShouldBeManageable() {
        assertNull(employee.getFirebaseUid());
        employee.setFirebaseUid("firebase123456");
        assertEquals("firebase123456", employee.getFirebaseUid());
    }

    /**
     * Verifies that taking a resource decreases its quantity in storage
     *        by exactly the requested amount.
     */
    @Test
    void takeResource_ShouldRemoveResourceFromStorage() throws Exception {
        storage.addResource(resource, 100);
        assertEquals(100, storage.getResources().get(resource));

        employee.takeResource(resource, 30);

        assertEquals(70, storage.getResources().get(resource));
    }

    /**
     * Verifies that requesting more units than available throws an
     *        exception, preventing negative stock values.
     */
    @Test
    void takeResource_ShouldThrowExceptionWhenInsufficientQuantity() throws Exception {
        storage.addResource(resource, 10);

        Exception exception = assertThrows(Exception.class, () -> {
            employee.takeResource(resource, 20);
        });

        assertTrue(exception.getMessage().contains("Not enough"));
    }

    /**
     * Verifies that requesting a resource not present in storage at all
     *        throws an exception rather than silently doing nothing.
     */
    @Test
    void takeResource_ShouldThrowExceptionWhenResourceNotPresent() {
        Exception exception = assertThrows(Exception.class, () -> {
            employee.takeResource(resource, 5);
        });

        assertTrue(exception.getMessage().contains("not present"));
    }

    /**
     * Verifies that an employee without a department cannot take any
     *        resource, because the storage chain cannot be resolved.
     */
    @Test
    void takeResource_ShouldThrowExceptionWhenDepartmentMissing() {
        employee.setDepartment(null);

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            employee.takeResource(resource, 5);
        });

        assertTrue(exception.getMessage().contains("no department"));
    }
}