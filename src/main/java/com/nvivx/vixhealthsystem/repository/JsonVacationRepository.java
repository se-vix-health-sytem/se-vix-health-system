package com.nvivx.vixhealthsystem.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nvivx.vixhealthsystem.model.staff.VacationRequest;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JSON-backed repository for vacation requests.
 * Data is persisted in src/main/resources/storage/vacations.json
 */
@Repository
public class JsonVacationRepository {

    private final ObjectMapper mapper;
    private final String path = "src/main/resources/storage/vacations.json";

    public JsonVacationRepository() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public List<VacationRequest> findAll() {
        try {
            File file = new File(path);
            if (!file.exists() || file.length() == 0) return new ArrayList<>();
            return mapper.readValue(file, new TypeReference<List<VacationRequest>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error reading vacations.json: " + e.getMessage(), e);
        }
    }

    public void saveAll(List<VacationRequest> requests) {
        try {
            File file = new File(path);
            file.getParentFile().mkdirs();
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, requests);
        } catch (Exception e) {
            throw new RuntimeException("Error writing vacations.json: " + e.getMessage(), e);
        }
    }

    public Optional<VacationRequest> findById(int id) {
        return findAll().stream().filter(v -> v.getId() == id).findFirst();
    }

    public VacationRequest save(VacationRequest request) {
        List<VacationRequest> all = findAll();
        if (request.getId() == 0) {
            int newId = all.stream().mapToInt(VacationRequest::getId).max().orElse(0) + 1;
            request.setId(newId);
            all.add(request);
        } else {
            all.removeIf(v -> v.getId() == request.getId());
            all.add(request);
        }
        saveAll(all);
        return request;
    }

    public void deleteById(int id) {
        List<VacationRequest> all = findAll();
        all.removeIf(v -> v.getId() == id);
        saveAll(all);
    }
}