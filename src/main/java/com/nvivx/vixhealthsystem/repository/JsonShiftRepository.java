package com.nvivx.vixhealthsystem.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nvivx.vixhealthsystem.model.staff.Shift;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @brief JSON-file-backed repository for {@link Shift} entities.
 *
 * Persists employee shift schedules to {@code src/main/resources/storage/shifts.json}.
 * Silently returns an empty list on read errors so that missing or malformed files do
 * not crash the staff-manager interface.
 *
 * @see com.nvivx.vixhealthsystem.model.staff.Shift
 * @see JsonVacationRepository
 */
@Repository
public class JsonShiftRepository {

    // =========================================================
    // STATE
    // =========================================================

    /** Jackson mapper used for JSON serialisation/deserialisation. */
    private final ObjectMapper mapper = new ObjectMapper();

    /** Handle to the JSON backing file. */
    private final File file =
            new File("src/main/resources/storage/shifts.json");

    // =========================================================
    // CRUD OPERATIONS
    // =========================================================

    /**
     * Returns all shifts stored in the JSON file.
     *
     * Returns an empty list when the file does not exist or cannot be parsed,
     * rather than propagating an exception.
     *
     * @return mutable list of all shifts; never {@code null}
     */
    public List<Shift> findAll() {
        try {
            if (!file.exists()) return new ArrayList<>();

            return mapper.readValue(file, new TypeReference<List<Shift>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * Overwrites the JSON file with the complete shift list.
     *
     * @param shifts replacement list; must not be {@code null}
     * @throws RuntimeException if the file cannot be written
     */
    public void saveAll(List<Shift> shifts) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, shifts);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}