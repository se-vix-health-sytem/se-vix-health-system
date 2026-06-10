package com.nvivx.vixhealthsystem.model;

import java.time.LocalDateTime;

/**
 * Audit log entry for tracking changes (NFR02)
 * These records cannot be deleted by normal users
 */
public class AuditLog {

    private Long id;
    private String action;           // e.g., "CREATE_EMPLOYEE", "ADD_DIAGNOSIS"
    private String entityType;       // e.g., "Employee", "Patient", "MedicalRecord"
    private String entityId;         // ID of the affected entity
    private String username;         // Who performed the action
    private String details;          // What changed (JSON or description)
    private LocalDateTime timestamp;

    // Constructors
    public AuditLog() {}

    public AuditLog(String action, String entityType, String entityId, String username, String details) {
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.username = username;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s: %s (by %s)",
                timestamp, action, entityType, entityId, username);
    }
}