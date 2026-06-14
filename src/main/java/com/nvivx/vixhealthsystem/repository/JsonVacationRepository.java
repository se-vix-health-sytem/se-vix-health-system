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
 * @brief JSON-file-backed repository for {@link VacationRequest} entities.
 *
 * Persists employee vacation requests to {@code src/main/resources/storage/vacations.json}.
 * Auto-assigns sequential integer IDs on first save, and replaces existing records
 * when an ID already exists.
 *
 * @see com.nvivx.vixhealthsystem.model.staff.VacationRequest
 * @see JsonShiftRepository
 */
@Repository
public class JsonVacationRepository {

    // =========================================================
    // STATE
    // =========================================================

    /** Jackson mapper configured to serialise date/time values as ISO-8601 strings. */
    private final ObjectMapper mapper;

    /** Relative path to the JSON backing file; resolved from the working directory. */
    private final String path = "src/main/resources/storage/vacations.json";

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    /**
     * Configures the Jackson mapper with Java 8 date/time support.
     */
    public JsonVacationRepository() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    // =========================================================
    // CRUD OPERATIONS
    // =========================================================

    /**
     * Returns all vacation requests from the JSON file.
     *
     * @return mutable list of all requests; never {@code null}, empty if file is absent
     * @throws RuntimeException if the file cannot be parsed
     */
    public List<VacationRequest> findAll() {
        try {
            File file = new File(path);
            if (!file.exists() || file.length() == 0) return new ArrayList<>();
            return mapper.readValue(file, new TypeReference<List<VacationRequest>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error reading vacations.json: " + e.getMessage(), e);
        }
    }

    /**
     * Overwrites the JSON file with the complete vacation request list.
     *
     * @param requests replacement list; must not be {@code null}
     * @throws RuntimeException if the file cannot be written
     */
    public void saveAll(List<VacationRequest> requests) {
        try {
            File file = new File(path);
            file.getParentFile().mkdirs();
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, requests);
        } catch (Exception e) {
            throw new RuntimeException("Error writing vacations.json: " + e.getMessage(), e);
        }
    }

    /**
     * Finds a vacation request by its numeric ID.
     *
     * @param id the vacation request ID
     * @return an {@link Optional} containing the request, or empty if not found
     */
    public Optional<VacationRequest> findById(int id) {
        return findAll().stream().filter(v -> v.getId() == id).findFirst();
    }

    /**
     * Persists a vacation request, inserting or replacing by ID.
     *
     * Assigns a new sequential ID when {@code request.getId() == 0}.
     *
     * @param request the request to persist; must not be {@code null}
     * @return the same request with its ID populated
     */
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

    /**
     * Removes the vacation request with the given ID.
     *
     * No-op if the ID does not exist.
     *
     * @param id the vacation request ID to remove
     */
    public void deleteById(int id) {
        List<VacationRequest> all = findAll();
        all.removeIf(v -> v.getId() == id);
        saveAll(all);
    }
}