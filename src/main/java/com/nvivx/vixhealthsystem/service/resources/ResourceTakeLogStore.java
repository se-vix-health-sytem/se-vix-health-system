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

@Service
public class ResourceTakeLogStore {

    public static class Entry {
        public String id;
        public Long employeeId;
        public String employeeName;
        public String employeeRole;
        public Long resourceId;
        public String resourceName;
        public Long storageId;
        public int quantity;
        public LocalDateTime takenAt;

        public Entry() {}

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

    private final ObjectMapper mapper;
    private final String path = "src/main/resources/storage/resource-take-logs.json";

    public ResourceTakeLogStore() {
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
                throw new RuntimeException("Could not create resource-take-logs.json", e);
            }
        }
    }

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

    public synchronized List<Entry> getByEmployee(Long employeeId) {
        return getAll().stream()
                .filter(e -> employeeId.equals(e.employeeId))
                .toList();
    }

    public synchronized void log(Long employeeId, String employeeName, String employeeRole,
                                 Long resourceId, String resourceName, Long storageId, int quantity) {
        List<Entry> all = getAll();
        all.add(0, new Entry(employeeId, employeeName, employeeRole,
                resourceId, resourceName, storageId, quantity));
        write(all);
    }

    private void write(List<Entry> entries) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(path), entries);
        } catch (IOException e) {
            throw new RuntimeException("Error writing resource-take-logs.json", e);
        }
    }
}
