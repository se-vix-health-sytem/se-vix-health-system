package com.nvivx.vixhealthsystem.service;

import com.nvivx.vixhealthsystem.model.AuditLog;
import com.nvivx.vixhealthsystem.repository.JsonAuditLogRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for audit logging (NFR02 - Traceability)
 * All actions are logged and cannot be deleted by normal users.
 */
@Service
public class AuditService {

    private final JsonAuditLogRepository auditLogRepository;

    public AuditService(JsonAuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    /**
     * Log an action in the system
     *
     * @param action The action performed (e.g., "CREATE_EMPLOYEE", "UPDATE_MEDICAL_RECORD")
     * @param entityType Type of entity affected (e.g., "Employee", "Patient", "Appointment")
     * @param entityId ID of the affected entity
     * @param details Additional details about the change (can be JSON or plain text)
     */
    public void log(String action, String entityType, String entityId, String details) {
        String username = getCurrentUsername();

        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setUsername(username);
        log.setDetails(details);
        log.setTimestamp(LocalDateTime.now());

        AuditLog saved = auditLogRepository.save(log);

        // Also print to console for real-time monitoring
        System.out.println(String.format("[AUDIT] %s | %s | %s:%s | by %s | %s",
                saved.getTimestamp(), action, entityType, entityId, username, details));
    }

    /**
     * Get all audit logs (for Staff Manager)
     */
    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAll();
    }

    /**
     * Get recent audit logs
     */
    public List<AuditLog> getRecentLogs(int limit) {
        return auditLogRepository.findRecent(limit);
    }

    /**
     * Get logs by entity type
     */
    public List<AuditLog> getLogsByEntityType(String entityType) {
        return auditLogRepository.findByEntityType(entityType);
    }

    private String getCurrentUsername() {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() &&
                    !authentication.getName().equals("anonymousUser")) {
                return authentication.getName();
            }
        } catch (Exception e) {
            // No security context yet - use SYSTEM
        }
        return "SYSTEM";
    }
}