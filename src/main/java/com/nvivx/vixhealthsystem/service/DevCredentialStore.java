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
 * DEV-ONLY: stores plaintext employee credentials so the login page can show
 * current passwords during demos.  Never ship this to production.
 */
@Service
public class DevCredentialStore {

    public static class Entry {
        public String email;
        public String name;
        public String role;
        public String password;
        /** True when a password reset link was triggered — actual password is unknown. */
        public boolean passwordResetTriggered;
        public LocalDateTime updatedAt;

        public Entry() {}

        public Entry(String email, String name, String role, String password) {
            this.email = email;
            this.name = name;
            this.role = role;
            this.password = password;
            this.updatedAt = LocalDateTime.now();
        }
    }

    private final ObjectMapper mapper;
    private final String path = "src/main/resources/storage/dev-credentials.json";

    public DevCredentialStore() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        init();
    }

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

    public synchronized List<Entry> getAll() {
        try {
            File file = new File(path);
            if (!file.exists() || file.length() == 0) return new ArrayList<>();
            return mapper.readValue(file, new TypeReference<List<Entry>>() {});
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public synchronized void store(String email, String name, String role, String password) {
        List<Entry> all = getAll();
        all.removeIf(e -> email.equals(e.email));
        all.add(new Entry(email, name, role, password));
        write(all);
    }

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
