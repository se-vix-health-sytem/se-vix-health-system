package com.nvivx.vixhealthsystem.model.resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @brief Unit tests for Resource using plain JUnit (no Spring context).
 * Verifies field setters/getters, the parameterized constructor, equals/hashCode
 * ID-based comparison, and null-ID edge cases.
 */
class ResourceTest {
    private Resource resource;

    @BeforeEach
    void setUp() {
        resource = new Resource("Syringes", "Disposable sterile syringes", new BigDecimal("0.50"));
        resource.setId(1L);
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        assertEquals(1L, resource.getId());
        assertEquals("Syringes", resource.getName());
        assertEquals("Disposable sterile syringes", resource.getDescription());
        assertEquals(new BigDecimal("0.50"), resource.getPrice());
    }

    @Test
    void parameterizedConstructor_ShouldInitializeResource() {
        Resource newResource = new Resource("Gloves", "Medical gloves", new BigDecimal("0.20"));

        assertEquals("Gloves", newResource.getName());
        assertEquals("Medical gloves", newResource.getDescription());
        assertEquals(new BigDecimal("0.20"), newResource.getPrice());
        assertNull(newResource.getId());
    }

    @Test
    void equals_ShouldCompareById() {
        Resource resource1 = new Resource("A", "Desc A", BigDecimal.ONE);
        resource1.setId(100L);

        Resource resource2 = new Resource("B", "Desc B", BigDecimal.TEN);
        resource2.setId(100L);

        Resource resource3 = new Resource("C", "Desc C", BigDecimal.valueOf(5));
        resource3.setId(101L);

        assertEquals(resource1, resource2);
        assertNotEquals(resource1, resource3);
        assertNotEquals(null, resource1);
        assertNotEquals("string", resource1);
    }

    @Test
    void hashCode_ShouldBeConsistentWithId() {
        Resource resource1 = new Resource();
        resource1.setId(100L);

        Resource resource2 = new Resource();
        resource2.setId(100L);

        assertEquals(resource1.hashCode(), resource2.hashCode());
    }

    @Test
    void equals_WithNullId_ShouldReturnFalseForDifferentObjects() {
        Resource r1 = new Resource();
        Resource r2 = new Resource();

        // IDs are null, so equals should return false (different objects)
        assertNotEquals(r1, r2);
    }

    @Test
    void equals_WithSameObject_ShouldReturnTrue() {
        assertEquals(resource, resource);
    }
}