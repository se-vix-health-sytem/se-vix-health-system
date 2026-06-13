package com.nvivx.vixhealthsystem.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AuditLogTest {

    @Test
    void constructor_ShouldInitializeTimestamp() {
        AuditLog log = new AuditLog("CREATE_USER", "Patient", "123", "admin", "Created patient record");

        assertNotNull(log.getTimestamp());
        assertTrue(log.getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertEquals("CREATE_USER", log.getAction());
        assertEquals("Patient", log.getEntityType());
        assertEquals("123", log.getEntityId());
        assertEquals("admin", log.getUsername());
        assertEquals("Created patient record", log.getDetails());
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        AuditLog log = new AuditLog();
        LocalDateTime now = LocalDateTime.now();

        log.setId(100L);
        log.setAction("UPDATE");
        log.setEntityType("Employee");
        log.setEntityId("456");
        log.setUsername("staff");
        log.setDetails("Updated employee details");
        log.setTimestamp(now);

        assertEquals(100L, log.getId());
        assertEquals("UPDATE", log.getAction());
        assertEquals("Employee", log.getEntityType());
        assertEquals("456", log.getEntityId());
        assertEquals("staff", log.getUsername());
        assertEquals("Updated employee details", log.getDetails());
        assertEquals(now, log.getTimestamp());
    }

    @Test
    void toString_ShouldReturnFormattedString() {
        AuditLog log = new AuditLog("DELETE", "Patient", "789", "admin", "Deleted account");
        String result = log.toString();

        assertTrue(result.contains("DELETE"));
        assertTrue(result.contains("Patient"));
        assertTrue(result.contains("789"));
        assertTrue(result.contains("admin"));
    }
}