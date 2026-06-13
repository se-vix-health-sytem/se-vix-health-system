package com.nvivx.vixhealthsystem.service;

import com.nvivx.vixhealthsystem.model.AuditLog;
import com.nvivx.vixhealthsystem.repository.JsonAuditLogRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuditService.
 *
 * These tests do not use the real JSON file.
 * JsonAuditLogRepository is mocked with Mockito.
 */
@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @Mock
    private JsonAuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditService service;

    /**
     * Clears the security context after every test
     * so authentication does not affect other tests.
     */
    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    /**
     * Tests that log() creates an AuditLog with SYSTEM username
     * when no authenticated user exists.
     */
    @Test
    void shouldLogActionWithSystemUsernameWhenNoUserAuthenticated() {
        AuditLog savedLog = new AuditLog();
        savedLog.setId(1L);
        savedLog.setAction("CREATE_PATIENT");
        savedLog.setEntityType("Patient");
        savedLog.setEntityId("10");
        savedLog.setUsername("SYSTEM");
        savedLog.setDetails("Created patient");
        savedLog.setTimestamp(LocalDateTime.now());

        when(auditLogRepository.save(any(AuditLog.class)))
                .thenReturn(savedLog);

        service.log(
                "CREATE_PATIENT",
                "Patient",
                "10",
                "Created patient"
        );

        ArgumentCaptor<AuditLog> captor =
                ArgumentCaptor.forClass(AuditLog.class);

        verify(auditLogRepository).save(captor.capture());

        AuditLog log = captor.getValue();

        assertEquals("CREATE_PATIENT", log.getAction());
        assertEquals("Patient", log.getEntityType());
        assertEquals("10", log.getEntityId());
        assertEquals("SYSTEM", log.getUsername());
        assertEquals("Created patient", log.getDetails());
        assertNotNull(log.getTimestamp());
    }

    /**
     * Tests that log() uses the authenticated username
     * when a user is present in the security context.
     */
    @Test
    void shouldLogActionWithAuthenticatedUsername() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "admin@vixhealth.com",
                        null,
                        List.of()
                )
        );

        AuditLog savedLog = new AuditLog();
        savedLog.setId(1L);
        savedLog.setAction("UPDATE_EMPLOYEE");
        savedLog.setEntityType("Employee");
        savedLog.setEntityId("5");
        savedLog.setUsername("admin@vixhealth.com");
        savedLog.setDetails("Updated employee");
        savedLog.setTimestamp(LocalDateTime.now());

        when(auditLogRepository.save(any(AuditLog.class)))
                .thenReturn(savedLog);

        service.log(
                "UPDATE_EMPLOYEE",
                "Employee",
                "5",
                "Updated employee"
        );

        ArgumentCaptor<AuditLog> captor =
                ArgumentCaptor.forClass(AuditLog.class);

        verify(auditLogRepository).save(captor.capture());

        AuditLog log = captor.getValue();

        assertEquals("UPDATE_EMPLOYEE", log.getAction());
        assertEquals("Employee", log.getEntityType());
        assertEquals("5", log.getEntityId());
        assertEquals("admin@vixhealth.com", log.getUsername());
        assertEquals("Updated employee", log.getDetails());
        assertNotNull(log.getTimestamp());
    }

    /**
     * Tests that log() uses SYSTEM username
     * when the authenticated user is anonymousUser.
     */
    @Test
    void shouldUseSystemUsernameForAnonymousUser() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "anonymousUser",
                        null,
                        List.of()
                )
        );

        AuditLog savedLog = new AuditLog();
        savedLog.setId(1L);
        savedLog.setTimestamp(LocalDateTime.now());

        when(auditLogRepository.save(any(AuditLog.class)))
                .thenReturn(savedLog);

        service.log(
                "DELETE_PATIENT",
                "Patient",
                "20",
                "Deleted patient"
        );

        ArgumentCaptor<AuditLog> captor =
                ArgumentCaptor.forClass(AuditLog.class);

        verify(auditLogRepository).save(captor.capture());

        assertEquals("SYSTEM", captor.getValue().getUsername());
    }

    /**
     * Tests that getAllLogs() returns all audit logs
     * provided by the repository.
     */
    @Test
    void shouldReturnAllLogs() {
        AuditLog log1 = createLog(1L, "CREATE_PATIENT", "Patient");
        AuditLog log2 = createLog(2L, "UPDATE_EMPLOYEE", "Employee");

        when(auditLogRepository.findAll())
                .thenReturn(List.of(log1, log2));

        List<AuditLog> result = service.getAllLogs();

        assertEquals(2, result.size());
        assertEquals("CREATE_PATIENT", result.get(0).getAction());
        assertEquals("UPDATE_EMPLOYEE", result.get(1).getAction());

        verify(auditLogRepository).findAll();
    }

    /**
     * Tests that getRecentLogs() returns recent logs
     * from the repository using the requested limit.
     */
    @Test
    void shouldReturnRecentLogs() {
        AuditLog log = createLog(1L, "CREATE_PATIENT", "Patient");

        when(auditLogRepository.findRecent(5))
                .thenReturn(List.of(log));

        List<AuditLog> result = service.getRecentLogs(5);

        assertEquals(1, result.size());
        assertEquals("CREATE_PATIENT", result.get(0).getAction());

        verify(auditLogRepository).findRecent(5);
    }

    /**
     * Tests that getLogsByEntityType() returns only logs
     * for the requested entity type.
     */
    @Test
    void shouldReturnLogsByEntityType() {
        AuditLog log = createLog(1L, "CREATE_PATIENT", "Patient");

        when(auditLogRepository.findByEntityType("Patient"))
                .thenReturn(List.of(log));

        List<AuditLog> result =
                service.getLogsByEntityType("Patient");

        assertEquals(1, result.size());
        assertEquals("Patient", result.get(0).getEntityType());

        verify(auditLogRepository).findByEntityType("Patient");
    }

    private AuditLog createLog(Long id, String action, String entityType) {
        AuditLog log = new AuditLog();
        log.setId(id);
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId("1");
        log.setUsername("SYSTEM");
        log.setDetails("Details");
        log.setTimestamp(LocalDateTime.now());
        return log;
    }
}