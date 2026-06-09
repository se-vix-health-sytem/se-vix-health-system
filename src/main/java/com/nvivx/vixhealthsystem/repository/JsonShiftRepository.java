package com.nvivx.vixhealthsystem.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nvivx.vixhealthsystem.model.staff.Shift;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JsonShiftRepository {

    private final ObjectMapper mapper = new ObjectMapper();

    private final File file =
            new File("src/main/resources/storage/shifts.json");

    public List<Shift> findAll() {
        try {
            if (!file.exists()) return new ArrayList<>();

            return mapper.readValue(file, new TypeReference<List<Shift>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public void saveAll(List<Shift> shifts) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, shifts);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}