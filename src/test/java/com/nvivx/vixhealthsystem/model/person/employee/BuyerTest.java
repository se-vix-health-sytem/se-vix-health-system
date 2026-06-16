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

import static org.junit.jupiter.api.Assertions.*;

/**
 * @brief Unit tests for Buyer.
 *
 * Verifies system role, employee type, and the addResource domain method that
 * adds stock to the facility storage through the buyer's department chain.
 * Also guards against the missing-department, missing-facility, and
 * missing-storage error paths. Plain JUnit : no Spring context loaded.
 *
 * @see Buyer
 */
class BuyerTest {
    private Buyer buyer;
    private Department department;
    private MedicalFacility facility;
    private Storage storage;
    private Resource resource;

    /** @brief Builds the fixture shared by all tests in this class. */
    @BeforeEach
    void setUp() {
        buyer = new Buyer();
        buyer.setId(1L);
        buyer.setName("Francesca");
        buyer.setSurname("Marini");

        department = new Department();
        department.setId(10L);
        department.setName("Purchasing");

        facility = new MedicalFacility();
        facility.setId(100L);
        facility.setName("Main Hospital");

        storage = new Storage();
        storage.setId(1000L);
        storage.setMedicalFacility(facility);

        facility.setStorage(storage);
        department.setMedicalFacility(facility);
        buyer.setDepartment(department);

        resource = new Resource("Surgical Gloves", "Disposable gloves", new BigDecimal("0.20"));
        resource.setId(500L);
    }

    /**
     * Verifies that the buyer's Spring Security role is ROLE_BUYER,
     *        granting access to purchasing features only.
     */
    @Test
    void getSystemRole_ShouldReturnBuyerRole() {
        assertEquals(Role.ROLE_BUYER, buyer.getSystemRole());
    }

    /**
     * Verifies that the employee type discriminator is BUYER for
     *        correct polymorphic handling in the UI and audit logs.
     */
    @Test
    void getEmployeeType_ShouldReturnBuyerType() {
        assertEquals(EmployeeType.BUYER, buyer.getEmployeeType());
    }

    /**
     * Verifies that purchasing a resource places the specified quantity
     *        in the facility's storage map.
     */
    @Test
    void addResource_ShouldAddResourceToStorage() {
        assertNull(storage.getResources().get(resource));

        buyer.addResource(resource, 100);

        assertEquals(100, storage.getResources().get(resource));
    }

    /**
     * Verifies that purchasing the same resource twice accumulates the
     *        quantities rather than overwriting the first delivery.
     */
    @Test
    void addResource_ShouldAccumulateQuantityWhenResourceAlreadyExists() {
        buyer.addResource(resource, 50);
        buyer.addResource(resource, 30);

        assertEquals(80, storage.getResources().get(resource));
    }

    /**
     * Verifies that a buyer without an assigned department cannot
     *        purchase resources, preventing orphan stock entries.
     */
    @Test
    void addResource_ShouldThrowExceptionWhenDepartmentMissing() {
        buyer.setDepartment(null);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            buyer.addResource(resource, 10);
        });

        assertTrue(exception.getMessage().contains("no department"));
    }

    /**
     * Verifies that a department not linked to a facility blocks resource
     *        purchase, ensuring no stock can be added without a known location.
     */
    @Test
    void addResource_ShouldThrowExceptionWhenFacilityMissing() {
        department.setMedicalFacility(null);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            buyer.addResource(resource, 10);
        });

        assertTrue(exception.getMessage().contains("no department"));
    }

    /**
     * Verifies that a facility without a storage unit blocks resource
     *        purchase, preventing stock from being added to a non-existent store.
     */
    @Test
    void addResource_ShouldThrowExceptionWhenStorageMissing() {
        facility.setStorage(null);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            buyer.addResource(resource, 10);
        });

        assertTrue(exception.getMessage().contains("no department"));
    }
}