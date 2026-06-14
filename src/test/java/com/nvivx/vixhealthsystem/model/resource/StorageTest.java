package com.nvivx.vixhealthsystem.model.resource;

import com.nvivx.vixhealthsystem.model.facility.MedicalFacility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @brief Unit tests for Storage using plain JUnit (no Spring context).
 * Covers addResource (accumulation), removeResource (decrement, zero-removal, not-present
 * and insufficient-quantity errors), getTotalQuantity, and setResources replacement.
 */
class StorageTest {
    private Storage storage;
    private Resource syringe;
    private Resource glove;
    private MedicalFacility facility;

    @BeforeEach
    void setUp() {
        storage = new Storage();
        storage.setId(1L);

        facility = new MedicalFacility();
        facility.setId(10L);
        storage.setMedicalFacility(facility);

        syringe = new Resource("Syringes", "Sterile syringes", new BigDecimal("0.50"));
        syringe.setId(100L);

        glove = new Resource("Gloves", "Medical gloves", new BigDecimal("0.20"));
        glove.setId(101L);
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        assertEquals(1L, storage.getId());
        assertEquals(facility, storage.getMedicalFacility());
        assertNotNull(storage.getResources());
        assertTrue(storage.getResources().isEmpty());
    }

    @Test
    void addResource_ShouldAddNewResource() {
        storage.addResource(syringe, 100);

        assertEquals(100, storage.getResources().get(syringe));
    }

    @Test
    void addResource_ShouldAccumulateQuantityForExistingResource() {
        storage.addResource(syringe, 50);
        storage.addResource(syringe, 30);

        assertEquals(80, storage.getResources().get(syringe));
    }

    @Test
    void removeResource_ShouldDecreaseQuantity() throws Exception {
        storage.addResource(syringe, 100);
        storage.removeResource(syringe, 30);

        assertEquals(70, storage.getResources().get(syringe));
    }

    @Test
    void removeResource_ShouldRemoveResourceWhenQuantityReachesZero() throws Exception {
        storage.addResource(syringe, 50);
        storage.removeResource(syringe, 50);

        assertFalse(storage.getResources().containsKey(syringe));
    }

    @Test
    void removeResource_ShouldThrowExceptionWhenResourceNotPresent() {
        Exception exception = assertThrows(Exception.class, () -> {
            storage.removeResource(syringe, 10);
        });

        assertTrue(exception.getMessage().contains("not present"));
    }

    @Test
    void removeResource_ShouldThrowExceptionWhenInsufficientQuantity() throws Exception {
        storage.addResource(syringe, 10);

        Exception exception = assertThrows(Exception.class, () -> {
            storage.removeResource(syringe, 20);
        });

        assertTrue(exception.getMessage().contains("Not enough"));
    }

    @Test
    void getTotalQuantity_ShouldReturnSumOfAllResources() {
        storage.addResource(syringe, 100);
        storage.addResource(glove, 250);

        assertEquals(350, storage.getTotalQuantity());
    }

    @Test
    void getTotalQuantity_ShouldReturnZeroWhenEmpty() {
        assertEquals(0, storage.getTotalQuantity());
    }

    @Test
    void setResources_ShouldReplaceResourceMap() {
        Map<Resource, Integer> newMap = new HashMap<>();
        newMap.put(syringe, 500);

        storage.setResources(newMap);

        assertEquals(500, storage.getResources().get(syringe));
        assertEquals(1, storage.getResources().size());
    }
}