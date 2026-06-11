package com.nvivx.vixhealthsystem.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class AuditLogTest {

    @Test
    void testAuditLogCreation() {
        AuditLog log = new AuditLog("CREATE", "Employee", "123", "admin", "Created employee");

        assertEquals("CREATE", log.getAction());
        assertEquals("Employee", log.getEntityType());
        assertEquals("123", log.getEntityId());
        assertEquals("admin", log.getUsername());
        assertEquals("Created employee", log.getDetails());
        assertNotNull(log.getTimestamp());
    }

    @Test
    void testAuditLogSetters() {
        AuditLog log = new AuditLog();
        LocalDateTime now = LocalDateTime.now();

        log.setId(1L);
        log.setAction("DELETE");
        log.setEntityType("Patient");
        log.setEntityId("456");
        log.setUsername("doctor");
        log.setDetails("Deleted patient");
        log.setTimestamp(now);

        assertEquals(1L, log.getId());
        assertEquals("DELETE", log.getAction());
        assertEquals("Patient", log.getEntityType());
        assertEquals("456", log.getEntityId());
        assertEquals("doctor", log.getUsername());
        assertEquals("Deleted patient", log.getDetails());
        assertEquals(now, log.getTimestamp());
    }

    @Test
    void testToString() {
        AuditLog log = new AuditLog("UPDATE", "Appointment", "789", "secretary", "Rescheduled");
        log.setTimestamp(LocalDateTime.of(2024, 1, 15, 10, 30));
        assertTrue(log.toString().contains("UPDATE"));
        assertTrue(log.toString().contains("Appointment"));
    }
}