package com.nvivx.vixhealthsystem.service;

import com.nvivx.vixhealthsystem.model.AuditLog;
import com.nvivx.vixhealthsystem.repository.JsonAuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Writes immutable audit log entries for every significant action in the system (NFR02).
 *
 * Every entry records who did what, to which entity, and when. Logs are stored in a
 * JSON file via {@link com.nvivx.vixhealthsystem.repository.JsonAuditLogRepository}
 * and are readable by the staff manager but can't be deleted through the UI.
 *
 * The current username is pulled from the Spring Security context automatically;
 * if there's no authenticated user (e.g. during startup), the entry is attributed
 * to {@code SYSTEM}.
 *
 * @see com.nvivx.vixhealthsystem.model.AuditLog
 * @see com.nvivx.vixhealthsystem.repository.JsonAuditLogRepository
 */
@Service
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);

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

        AuditLog entry = new AuditLog();
        entry.setAction(action);
        entry.setEntityType(entityType);
        entry.setEntityId(entityId);
        entry.setUsername(username);
        entry.setDetails(details);
        entry.setTimestamp(LocalDateTime.now());

        AuditLog saved = auditLogRepository.save(entry);

        log.info("[AUDIT] {} | {} | {}:{} | by {} | {}",
                saved.getTimestamp(), action, entityType, entityId, username, details);
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