package com.nvivx.vixhealthsystem.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DEV-ONLY store that keeps plaintext employee credentials visible on the demo login board.
 *
 * Because Firebase does not expose passwords after creation, newly created employee
 * accounts would otherwise be unrecoverable in a demo environment without a password-reset
 * email.  This store solves that by holding the temporary password ({@code ChangeMe123!})
 * alongside the employee's name and role so the staff manager demo page can display it.
 *
 * Credentials are written to {@code dev-credentials.json} and are never used for actual
 * authentication : all auth goes through Firebase.  Do NOT include this service in any
 * production build.
 *
 * All public methods are {@code synchronized} to prevent concurrent read/write corruption
 * from multi-threaded request handling.
 *
 * @see com.nvivx.vixhealthsystem.service.core.EmployeeService
 */
@Service
public class DevCredentialStore {

    // =========================================================
    // INNER CLASSES
    // =========================================================

    /**
     * One credential entry per employee; serialised as a flat JSON object.
     *
     * All fields are public for Jackson serialisation without requiring getters.
     */
    public static class Entry {
        public String email;
        public String name;
        public String role;
        public String password;
        /** Set to {@code true} when a Firebase reset link was sent : password is no longer known. */
        public boolean passwordResetTriggered;
        public LocalDateTime updatedAt;

        /** @brief No-arg constructor required by Jackson for deserialisation. */
        public Entry() {}

        /**
         * Creates a new entry with the current timestamp.
         *
         * @param email     Employee login email.
         * @param name      Display name (used on the demo board).
         * @param role      Employee subtype name (e.g., {@code "MedicalSpecialist"}).
         * @param password  Plaintext temporary password.
         */
        public Entry(String email, String name, String role, String password) {
            this.email = email;
            this.name = name;
            this.role = role;
            this.password = password;
            this.updatedAt = LocalDateTime.now();
        }
    }

    // =========================================================
    // FIELDS
    // =========================================================

    private final ObjectMapper mapper;
    /** Relative path to the backing JSON file; created automatically on first use. */
    private final String path = "src/main/resources/storage/dev-credentials.json";

    // =========================================================
    // CONSTRUCTORS
    // =========================================================

    /**
     * Initialises the Jackson mapper with Java-time support and ensures the
     *        backing JSON file exists on disk.
     */
    public DevCredentialStore() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        init();
    }

    // =========================================================
    // HELPERS
    // =========================================================

    /** Creates the JSON file and its parent directories if they do not exist. */
    private void init() {
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                mapper.writeValue(file, new ArrayList<Entry>());
            } catch (IOException e) {
                throw new RuntimeException("Could not create dev-credentials.json", e);
            }
        }
    }

    // =========================================================
    // READ OPERATIONS
    // =========================================================

    /** @brief Returns all stored credential entries, or an empty list if the file is missing. */
    public synchronized List<Entry> getAll() {
        try {
            File file = new File(path);
            if (!file.exists() || file.length() == 0) return new ArrayList<>();
            return mapper.readValue(file, new TypeReference<List<Entry>>() {});
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    // =========================================================
    // WRITE OPERATIONS
    // =========================================================

    /**
     * Adds or replaces the credential entry for the given email.
     *
     * If an entry already exists for the email (e.g., from a previous create attempt),
     * it is removed before the new one is inserted.
     *
     * @param email     Employee login email (used as the unique key).
     * @param name      Display name for the demo board.
     * @param role      Employee subtype name.
     * @param password  Plaintext temporary password.
     */
    public synchronized void store(String email, String name, String role, String password) {
        List<Entry> all = getAll();
        all.removeIf(e -> email.equals(e.email));
        all.add(new Entry(email, name, role, password));
        write(all);
    }

    /**
     * Updates the stored password for an existing entry and clears the reset flag.
     *
     * @param email        Email identifying the entry to update.
     * @param newPassword  The new plaintext password.
     */
    public synchronized void updatePassword(String email, String newPassword) {
        List<Entry> all = getAll();
        for (Entry e : all) {
            if (email.equals(e.email)) {
                e.password = newPassword;
                e.passwordResetTriggered = false;
                e.updatedAt = LocalDateTime.now();
            }
        }
        write(all);
    }

    /** Marks that a Firebase password-reset link was sent for this employee. */
    public synchronized void markResetTriggered(String email) {
        List<Entry> all = getAll();
        for (Entry e : all) {
            if (email.equals(e.email)) {
                e.passwordResetTriggered = true;
                e.updatedAt = LocalDateTime.now();
            }
        }
        write(all);
    }

    /**
     * Removes the credential entry for the given email, if it exists.
     *
     * Called during employee deletion so the demo board stays clean.
     *
     * @param email  Email identifying the entry to remove.
     */
    public synchronized void remove(String email) {
        List<Entry> all = getAll();
        all.removeIf(e -> email.equals(e.email));
        write(all);
    }

    private void write(List<Entry> entries) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(path), entries);
        } catch (IOException e) {
            throw new RuntimeException("Error writing dev-credentials.json", e);
        }
    }
}
