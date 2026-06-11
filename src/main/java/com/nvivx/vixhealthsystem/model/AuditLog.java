package com.nvivx.vixhealthsystem.model;

import java.time.LocalDateTime;

/**
 * Represents an audit log entry for tracking system changes (NFR02).
 * <p>
 * Audit log entries are stored as JSON (audit-logs.json), not in the SQL database.
 * These records cannot be deleted or modified by normal users —
 * they provide an immutable history of all significant actions in the system.
 */
public class AuditLog {

    /**
     * Unique audit log entry identifier.
     */
    private Long id;

    /**
     * Action performed (e.g. CREATE_EMPLOYEE, ADD_DIAGNOSIS).
     */
    private String action;

    /**
     * Type of entity affected (e.g. Employee, Patient, MedicalRecord).
     */
    private String entityType;

    /**
     * Identifier of the affected entity.
     */
    private String entityId;

    /**
     * Username of the person who performed the action.
     */
    private String username;

    /**
     * Description of what changed, stored as a JSON string or plain text.
     */
    private String details;

    /**
     * Date and time when the action was recorded.
     */
    private LocalDateTime timestamp;

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    /**
     * Default constructor required for JSON deserialization.
     */
    public AuditLog() {
    }

    /**
     * Creates an audit log entry and sets the timestamp to the current date and time.
     *
     * @param action     the action performed
     * @param entityType the type of entity affected
     * @param entityId   the identifier of the affected entity
     * @param username   the username of the person who performed the action
     * @param details    a description of what changed
     */
    public AuditLog(
            String action,
            String entityType,
            String entityId,
            String username,
            String details
    ) {
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.username = username;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }

    // =====================================================
    // GETTERS & SETTERS
    // =====================================================

    /**
     * Returns the unique audit log entry identifier.
     *
     * @return the log entry ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique audit log entry identifier.
     *
     * @param id the log entry ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the action performed.
     *
     * @return the action (e.g. CREATE_EMPLOYEE, ADD_DIAGNOSIS)
     */
    public String getAction() {
        return action;
    }

    /**
     * Sets the action performed.
     *
     * @param action the action to set
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * Returns the type of entity affected.
     *
     * @return the entity type (e.g. Employee, Patient)
     */
    public String getEntityType() {
        return entityType;
    }

    /**
     * Sets the type of entity affected.
     *
     * @param entityType the entity type to set
     */
    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    /**
     * Returns the identifier of the affected entity.
     *
     * @return the entity ID
     */
    public String getEntityId() {
        return entityId;
    }

    /**
     * Sets the identifier of the affected entity.
     *
     * @param entityId the entity ID to set
     */
    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    /**
     * Returns the username of the person who performed the action.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the person who performed the action.
     *
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the description of what changed.
     *
     * @return the change details
     */
    public String getDetails() {
        return details;
    }

    /**
     * Sets the description of what changed.
     *
     * @param details the change details to set
     */
    public void setDetails(String details) {
        this.details = details;
    }

    /**
     * Returns the date and time when the action was recorded.
     *
     * @return the timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the date and time when the action was recorded.
     *
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    // =====================================================
    // OBJECT METHODS
    // =====================================================

    /**
     * Returns a formatted string representation of the audit log entry.
     *
     * @return a summary of the audit log entry
     */
    @Override
    public String toString() {
        return String.format(
                "[%s] %s - %s: %s (by %s)",
                timestamp, action, entityType, entityId, username
        );
    }
}
