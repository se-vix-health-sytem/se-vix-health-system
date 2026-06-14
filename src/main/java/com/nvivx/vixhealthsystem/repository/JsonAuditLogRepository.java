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
 * @brief Append-only JSON-file-backed repository for {@link AuditLog} entries.
 *
 * Persists audit logs to {@code src/main/resources/storage/audit-logs.json}.
 * Intentionally exposes no {@code delete} or {@code update} methods to enforce
 * NFR02 (Traceability) — once written, a log entry is immutable for normal users.
 * The {@link #save(AuditLog)} method is {@code synchronized} to prevent ID
 * collisions under concurrent request handling.
 *
 * @see com.nvivx.vixhealthsystem.model.AuditLog
 */
@Repository
public class JsonAuditLogRepository {

    // =========================================================
    // STATE
    // =========================================================

    /** Jackson mapper configured to serialise dates as ISO-8601 strings. */
    private final ObjectMapper mapper;

    /** Relative path to the JSON backing file; resolved from the working directory. */
    private final String path = "src/main/resources/storage/audit-logs.json";

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    /**
     * Configures the mapper and ensures the backing file exists on disk.
     *
     * Creates an empty JSON array file if {@code audit-logs.json} is missing so
     * that subsequent reads never fail with a file-not-found error.
     */
    public JsonAuditLogRepository() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        initializeStorage();
    }

    // =========================================================
    // INTERNAL HELPERS
    // =========================================================

    /**
     * Creates the backing file with an empty array if it does not already exist.
     *
     * @throws RuntimeException if the file cannot be created
     */
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

    // =========================================================
    // READ OPERATIONS
    // =========================================================

    /**
     * Returns all audit log entries in insertion order (NFR02 — read-only access).
     *
     * @return all log entries; never {@code null}, empty list if file is empty
     * @throws RuntimeException if the file cannot be parsed
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

    // =========================================================
    // WRITE OPERATIONS (append-only)
    // =========================================================

    /**
     * Appends a new log entry, assigning a sequential ID if absent (NFR02).
     *
     * Synchronised to prevent duplicate IDs under concurrent writes.
     * Existing entries are never modified.
     *
     * @param log the entry to append; must not be {@code null}
     * @return the same entry with its ID populated
     * @throws RuntimeException if the file cannot be written
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
     * Finds all log entries for a given entity type.
     *
     * @param entityType case-sensitive entity name (e.g. {@code "Employee"}, {@code "Patient"})
     * @return matching entries; empty list if none found
     */
    public List<AuditLog> findByEntityType(String entityType) {
        return findAll().stream()
                .filter(log -> entityType.equals(log.getEntityType()))
                .collect(Collectors.toList());
    }

    /**
     * Finds all log entries recorded under a specific username.
     *
     * @param username the actor's username as stored in the log entries
     * @return matching entries; empty list if none found
     */
    public List<AuditLog> findByUsername(String username) {
        return findAll().stream()
                .filter(log -> username.equals(log.getUsername()))
                .collect(Collectors.toList());
    }

    /**
     * Finds all log entries whose action field contains the given keyword.
     *
     * @param action action keyword (e.g. {@code "CREATE"}, {@code "UPDATE"}, {@code "DELETE"})
     * @return matching entries; empty list if none found
     */
    public List<AuditLog> findByAction(String action) {
        return findAll().stream()
                .filter(log -> log.getAction() != null && log.getAction().contains(action))
                .collect(Collectors.toList());
    }

    /**
     * Returns the most recent log entries sorted by timestamp descending.
     *
     * @param limit maximum number of entries to return; must be positive
     * @return up to {@code limit} most recent entries
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