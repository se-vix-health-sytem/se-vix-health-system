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
 * JSON-backed repository for appointments.
 * Data is persisted in src/main/resources/storage/appointments.json
 */
@Repository
public class JsonAppointmentRepository {

    private final ObjectMapper mapper;
    private final String path = "src/main/resources/storage/appointments.json";

    public JsonAppointmentRepository() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public List<Appointment> findAll() {
        try {
            File file = new File(path);
            if (!file.exists() || file.length() == 0) return new ArrayList<>();
            return mapper.readValue(file, new TypeReference<List<Appointment>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error reading appointments.json: " + e.getMessage(), e);
        }
    }

    public void saveAll(List<Appointment> appointments) {
        try {
            File file = new File(path);
            file.getParentFile().mkdirs();
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, appointments);
        } catch (Exception e) {
            throw new RuntimeException("Error writing appointments.json: " + e.getMessage(), e);
        }
    }

    public Appointment findById(int id) {
        return findAll().stream().filter(a -> a.getId() == id).findFirst().orElse(null);
    }

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

    public void deleteById(int id) {
        List<Appointment> all = findAll();
        all.removeIf(a -> a.getId() == id);
        saveAll(all);
    }
}