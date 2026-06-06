package com.nvivx.vixhealthsystem.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nvivx.vixhealthsystem.model.medical.Appointment;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JsonAppointmentRepository {

    private final ObjectMapper mapper = new ObjectMapper();
    private final String path = "src/main/resources/storage/appointments.json";

    public List<Appointment> findAll() {
        try {
            File file = new File(path);

            if (!file.exists()) return new ArrayList<>();

            return mapper.readValue(file, new TypeReference<List<Appointment>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error reading JSON file", e);
        }
    }

    public void saveAll(List<Appointment> appointments) {
        try {
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(path), appointments);
        } catch (Exception e) {
            throw new RuntimeException("Error writing JSON file", e);
        }
    }
}