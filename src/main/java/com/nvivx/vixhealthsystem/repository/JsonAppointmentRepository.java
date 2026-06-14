package com.nvivx.vixhealthsystem.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nvivx.vixhealthsystem.model.medical.Appointment;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @brief JSON-file-backed repository for {@link Appointment} entities.
 *
 * Persists appointments to {@code src/main/resources/storage/appointments.json}
 * instead of a relational database, keeping scheduling data separate from the
 * H2/PostgreSQL schema.  Auto-assigns sequential integer IDs on first save.
 *
 * @see JsonAuditLogRepository
 * @see com.nvivx.vixhealthsystem.model.medical.Appointment
 */
@Repository
public class JsonAppointmentRepository {

    // =========================================================
    // STATE
    // =========================================================

    /** Jackson mapper configured to serialise {@link java.time.LocalDateTime} as ISO strings. */
    private final ObjectMapper mapper;

    /** Relative path to the JSON backing file; resolved from the working directory. */
    private final String path = "src/main/resources/storage/appointments.json";

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    /**
     * Configures the Jackson mapper with Java 8 date/time support.
     *
     * Registers {@link JavaTimeModule} and disables timestamp-based serialisation
     * so that dates are stored as human-readable ISO-8601 strings.
     */
    public JsonAppointmentRepository() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    // =========================================================
    // CRUD OPERATIONS
    // =========================================================

    /**
     * Returns all appointments stored in the JSON file.
     *
     * Returns an empty list when the file does not yet exist or is empty.
     *
     * @return mutable list of all appointments; never {@code null}
     * @throws RuntimeException if the file cannot be parsed
     */
    public List<Appointment> findAll() {
        try {
            File file = new File(path);
            if (!file.exists() || file.length() == 0) return new ArrayList<>();
            return mapper.readValue(file, new TypeReference<List<Appointment>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error reading appointments.json: " + e.getMessage(), e);
        }
    }

    /**
     * Overwrites the JSON file with the given appointment list.
     *
     * Creates parent directories if they do not exist.
     *
     * @param appointments complete replacement list; must not be {@code null}
     * @throws RuntimeException if the file cannot be written
     */
    public void saveAll(List<Appointment> appointments) {
        try {
            File file = new File(path);
            file.getParentFile().mkdirs();
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, appointments);
        } catch (Exception e) {
            throw new RuntimeException("Error writing appointments.json: " + e.getMessage(), e);
        }
    }

    /**
     * Finds a single appointment by its numeric ID.
     *
     * @param id appointment ID
     * @return the matching appointment, or {@code null} if not found
     */
    public Appointment findById(int id) {
        return findAll().stream().filter(a -> a.getId() == id).findFirst().orElse(null);
    }

    /**
     * Persists an appointment, inserting or replacing based on ID.
     *
     * When {@code appointment.getId() == 0} a new sequential ID is assigned.
     * Otherwise the existing record with the same ID is replaced.
     *
     * @param appointment the appointment to persist; must not be {@code null}
     * @return the same appointment with its ID populated
     */
    public Appointment save(Appointment appointment) {
        List<Appointment> all = findAll();
        if (appointment.getId() == 0) {
            int newId = all.stream().mapToInt(Appointment::getId).max().orElse(0) + 1;
            appointment.setId(newId);
            all.add(appointment);
        } else {
            all.removeIf(a -> a.getId() == appointment.getId());
            all.add(appointment);
        }
        saveAll(all);
        return appointment;
    }

    /**
     * Removes the appointment with the given ID from the JSON store.
     *
     * No-op if the ID does not exist.
     *
     * @param id appointment ID to remove
     */
    public void deleteById(int id) {
        List<Appointment> all = findAll();
        all.removeIf(a -> a.getId() == id);
        saveAll(all);
    }
}