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

class BuyerTest {
    private Buyer buyer;
    private Department department;
    private MedicalFacility facility;
    private Storage storage;
    private Resource resource;

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

    @Test
    void getSystemRole_ShouldReturnBuyerRole() {
        assertEquals(Role.ROLE_BUYER, buyer.getSystemRole());
    }

    @Test
    void getEmployeeType_ShouldReturnBuyerType() {
        assertEquals(EmployeeType.BUYER, buyer.getEmployeeType());
    }

    @Test
    void addResource_ShouldAddResourceToStorage() {
        assertNull(storage.getResources().get(resource));

        buyer.addResource(resource, 100);

        assertEquals(100, storage.getResources().get(resource));
    }

    @Test
    void addResource_ShouldAccumulateQuantityWhenResourceAlreadyExists() {
        buyer.addResource(resource, 50);
        buyer.addResource(resource, 30);

        assertEquals(80, storage.getResources().get(resource));
    }

    @Test
    void addResource_ShouldThrowExceptionWhenDepartmentMissing() {
        buyer.setDepartment(null);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            buyer.addResource(resource, 10);
        });

        assertTrue(exception.getMessage().contains("no department"));
    }

    @Test
    void addResource_ShouldThrowExceptionWhenFacilityMissing() {
        department.setMedicalFacility(null);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            buyer.addResource(resource, 10);
        });

        assertTrue(exception.getMessage().contains("no department"));
    }

    @Test
    void addResource_ShouldThrowExceptionWhenStorageMissing() {
        facility.setStorage(null);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            buyer.addResource(resource, 10);
        });

        assertTrue(exception.getMessage().contains("no department"));
    }
}