package com.nvivx.vixhealthsystem.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nvivx.vixhealthsystem.model.AuditLog;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JSON-backed repository for audit logs.
 * Data is persisted in src/main/resources/storage/audit-logs.json
 *
 * NFR02 - Traceability: Normal users cannot delete or edit log records.
 * This repository provides NO delete or update methods.
 */
@Repository
public class JsonAuditLogRepository {

    private final ObjectMapper mapper;
    private final String path = "src/main/resources/storage/audit-logs.json";

    public JsonAuditLogRepository() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        initializeStorage();
    }

    private void initializeStorage() {
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                mapper.writeValue(file, new ArrayList<AuditLog>());
            } catch (IOException e) {
                throw new RuntimeException("Could not create audit-logs.json", e);
            }
        }
    }

    /**
     * Returns all audit logs (READ ONLY - no modification allowed by normal users)
     * This satisfies NFR02 - Traceability
     */
    public List<AuditLog> findAll() {
        try {
            File file = new File(path);
            if (!file.exists() || file.length() == 0) {
                return new ArrayList<>();
            }
            return mapper.readValue(file, new TypeReference<List<AuditLog>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Error reading audit-logs.json: " + e.getMessage(), e);
        }
    }

    /**
     * Saves a new audit log (APPEND ONLY - no overwriting existing logs)
     * This satisfies NFR02 - Logs cannot be deleted or edited
     */
    public synchronized AuditLog save(AuditLog log) {
        List<AuditLog> allLogs = findAll();

        // Generate ID if not present
        if (log.getId() == null) {
            long newId = allLogs.stream()
                    .mapToLong(AuditLog::getId)
                    .max()
                    .orElse(0L) + 1;
            log.setId(newId);
        }

        allLogs.add(log);

        try {
            File file = new File(path);
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, allLogs);
        } catch (IOException e) {
            throw new RuntimeException("Error writing to audit-logs.json: " + e.getMessage(), e);
        }

        return log;
    }

    /**
     * Find logs by entity type (e.g., "Employee", "Patient", "Appointment")
     */
    public List<AuditLog> findByEntityType(String entityType) {
        return findAll().stream()
                .filter(log -> entityType.equals(log.getEntityType()))
                .collect(Collectors.toList());
    }

    /**
     * Find logs by username
     */
    public List<AuditLog> findByUsername(String username) {
        return findAll().stream()
                .filter(log -> username.equals(log.getUsername()))
                .collect(Collectors.toList());
    }

    /**
     * Find logs by action (e.g., "CREATE", "UPDATE", "DELETE")
     */
    public List<AuditLog> findByAction(String action) {
        return findAll().stream()
                .filter(log -> log.getAction() != null && log.getAction().contains(action))
                .collect(Collectors.toList());
    }

    /**
     * Get recent logs (most recent first)
     */
    public List<AuditLog> findRecent(int limit) {
        return findAll().stream()
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    // IMPORTANT: NO delete() or update() methods!
    // This enforces NFR02 - Normal users cannot delete or edit log records
}