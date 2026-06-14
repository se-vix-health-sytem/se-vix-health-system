package com.nvivx.vixhealthsystem.service.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @brief JSON-backed append log that records every resource takeout event performed by employees.
 *
 * Entries are stored in {@code src/main/resources/storage/resource-take-logs.json} and are
 * sorted newest-first on retrieval.  All public methods are {@code synchronized} to prevent
 * concurrent write corruption from multi-threaded request handling.
 *
 * Used by {@link InventoryService#removeResourceFromStorage(com.nvivx.vixhealthsystem.model.person.employee.Employee, Long, int)}
 * to maintain a separate, human-readable consumption audit trail alongside the main
 * {@link com.nvivx.vixhealthsystem.service.AuditService} log.
 *
 * @see InventoryService
 */
@Service
public class ResourceTakeLogStore {

    // =========================================================
    // INNER CLASSES
    // =========================================================

    /**
     * Immutable snapshot of a single resource takeout event.
     *
     * All fields are public for Jackson serialisation without requiring getters.
     */
    public static class Entry {
        /** UUID assigned at creation time; serves as a stable log identifier. */
        public String id;
        public Long employeeId;
        public String employeeName;
        public String employeeRole;
        public Long resourceId;
        public String resourceName;
        public Long storageId;
        public int quantity;
        public LocalDateTime takenAt;

        /** @brief No-arg constructor required by Jackson for deserialisation. */
        public Entry() {}

        /**
         * Constructs a fully populated entry with a generated UUID and current timestamp.
         *
         * @param employeeId    ID of the employee who took the resource.
         * @param employeeName  Display name of the employee.
         * @param employeeRole  Role class name (e.g., {@code "Technician"}).
         * @param resourceId    Catalogue ID of the taken resource.
         * @param resourceName  Display name of the taken resource.
         * @param storageId     ID of the storage facility the resource was taken from.
         * @param quantity      Number of units taken.
         */
        public Entry(Long employeeId, String employeeName, String employeeRole,
                     Long resourceId, String resourceName, Long storageId, int quantity) {
            this.id = UUID.randomUUID().toString();
            this.employeeId = employeeId;
            this.employeeName = employeeName;
            this.employeeRole = employeeRole;
            this.resourceId = resourceId;
            this.resourceName = resourceName;
            this.storageId = storageId;
            this.quantity = quantity;
            this.takenAt = LocalDateTime.now();
        }
    }

    // =========================================================
    // FIELDS
    // =========================================================

    private final ObjectMapper mapper;
    /** Relative path to the backing JSON file; created automatically on first use. */
    private final String path = "src/main/resources/storage/resource-take-logs.json";

    // =========================================================
    // CONSTRUCTORS
    // =========================================================

    /**
     * Initialises the Jackson mapper with Java-time support and ensures the
     *        backing JSON file exists on disk.
     */
    public ResourceTakeLogStore() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        init();
    }

    // =========================================================
    // HELPERS
    // =========================================================

    /**
     * Creates the backing JSON file and its parent directories if they do not exist.
     *
     * @throws RuntimeException When the file cannot be created due to an I/O error.
     */
    private void init() {
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                mapper.writeValue(file, new ArrayList<Entry>());
            } catch (IOException e) {
                throw new RuntimeException("Could not create resource-take-logs.json", e);
            }
        }
    }

    // =========================================================
    // READ OPERATIONS
    // =========================================================

    /**
     * Returns all log entries sorted newest-first.
     *
     * @return Mutable list of entries; empty when the log file is absent or empty.
     */
    public synchronized List<Entry> getAll() {
        try {
            File file = new File(path);
            if (!file.exists() || file.length() == 0) return new ArrayList<>();
            List<Entry> entries = mapper.readValue(file, new TypeReference<List<Entry>>() {});
            entries.sort((a, b) -> b.takenAt != null && a.takenAt != null
                    ? b.takenAt.compareTo(a.takenAt) : 0);
            return entries;
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Returns all log entries for a specific employee, newest-first.
     *
     * @param employeeId  ID of the employee whose takeout history is requested.
     * @return            Non-null list; empty when the employee has no recorded takeouts.
     */
    public synchronized List<Entry> getByEmployee(Long employeeId) {
        return getAll().stream()
                .filter(e -> employeeId.equals(e.employeeId))
                .toList();
    }

    // =========================================================
    // WRITE OPERATIONS
    // =========================================================

    /**
     * Prepends a new takeout entry to the log file.
     *
     * Prepending (index 0) keeps the file ordered newest-first without sorting on every write.
     *
     * @param employeeId    ID of the employee who took the resource.
     * @param employeeName  Display name of the employee.
     * @param employeeRole  Role class name (e.g., {@code "Technician"}).
     * @param resourceId    Catalogue ID of the resource that was taken.
     * @param resourceName  Display name of the resource.
     * @param storageId     ID of the storage facility the resource was taken from.
     * @param quantity      Number of units taken.
     * @throws RuntimeException When the updated list cannot be written to disk.
     */
    public synchronized void log(Long employeeId, String employeeName, String employeeRole,
                                 Long resourceId, String resourceName, Long storageId, int quantity) {
        List<Entry> all = getAll();
        all.add(0, new Entry(employeeId, employeeName, employeeRole,
                resourceId, resourceName, storageId, quantity));
        write(all);
    }

    /**
     * Serialises the entry list to the backing JSON file with pretty-printing.
     *
     * @param entries  The full list to persist.
     * @throws RuntimeException When the file cannot be written.
     */
    private void write(List<Entry> entries) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(path), entries);
        } catch (IOException e) {
            throw new RuntimeException("Error writing resource-take-logs.json", e);
        }
    }
}
