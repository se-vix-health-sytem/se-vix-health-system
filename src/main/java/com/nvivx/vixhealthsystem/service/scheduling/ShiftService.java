package com.nvivx.vixhealthsystem.service.scheduling;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nvivx.vixhealthsystem.model.staff.Shift;
import com.nvivx.vixhealthsystem.service.AuditService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class ShiftService {

    private final ObjectMapper mapper;
    private final String shiftsPath = "src/main/resources/storage/shifts.json";
    private final AuditService auditService;
    private final AtomicLong idGenerator = new AtomicLong(1);

    public ShiftService(AuditService auditService) {
        this.auditService = auditService;
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        initializeStorage();
    }

    private void initializeStorage() {
        File file = new File(shiftsPath);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                mapper.writeValue(file, new ArrayList<Shift>());
            } catch (IOException e) {
                throw new RuntimeException("Could not create shifts.json", e);
            }
        }

        // Initialize ID generator based on existing shifts
        List<Shift> existing = findAll();
        long maxId = existing.stream()
                .mapToLong(Shift::getId)
                .max()
                .orElse(0L);
        idGenerator.set(maxId + 1);
    }

    private List<Shift> findAll() {
        try {
            File file = new File(shiftsPath);
            if (!file.exists() || file.length() == 0) {
                return new ArrayList<>();
            }
            return mapper.readValue(file, new TypeReference<List<Shift>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Error reading shifts.json", e);
        }
    }

    private void saveAll(List<Shift> shifts) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(shiftsPath), shifts);
        } catch (IOException e) {
            throw new RuntimeException("Error writing shifts.json", e);
        }
    }

    public List<Shift> getShiftsForEmployee(Long employeeId) {
        return findAll().stream()
                .filter(s -> s.getEmployeeId().equals(employeeId))
                .collect(Collectors.toList());
    }

    public List<Shift> getShiftsForWeek(LocalDate startDate) {
        LocalDate endDate = startDate.plusDays(6);
        return findAll().stream()
                .filter(s -> !s.getDate().isBefore(startDate) && !s.getDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    public List<Shift> getShiftsForEmployeeBetweenDates(Long employeeId, LocalDate startDate, LocalDate endDate) {
        return findAll().stream()
                .filter(s -> s.getEmployeeId().equals(employeeId))
                .filter(s -> !s.getDate().isBefore(startDate) && !s.getDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    public boolean isEmployeeOnShift(Long employeeId, LocalDate date) {
        return getShiftsForEmployee(employeeId).stream()
                .anyMatch(s -> s.getDate().equals(date));
    }

    public void assignShift(Long employeeId, LocalDate date, String shiftType) {
        List<Shift> shifts = findAll();

        // Remove existing shift for same employee on same day
        shifts.removeIf(s -> s.getEmployeeId().equals(employeeId) && s.getDate().equals(date));

        Shift newShift = new Shift();
        newShift.setId(idGenerator.getAndIncrement());
        newShift.setEmployeeId(employeeId);
        newShift.setDate(date);
        newShift.setShiftType(shiftType);
        newShift.setNotes("Assigned by Staff Manager");

        shifts.add(newShift);
        saveAll(shifts);

        auditService.log("ASSIGN_SHIFT", "Shift", String.valueOf(newShift.getId()),
                "Assigned " + shiftType + " shift to employee " + employeeId + " on " + date);
    }

    public void removeShift(Long shiftId) {
        List<Shift> shifts = findAll();
        Shift removed = shifts.stream()
                .filter(s -> s.getId().equals(shiftId))
                .findFirst()
                .orElse(null);

        shifts.removeIf(s -> s.getId().equals(shiftId));
        saveAll(shifts);

        if (removed != null) {
            auditService.log("REMOVE_SHIFT", "Shift", String.valueOf(shiftId),
                    "Removed shift for employee " + removed.getEmployeeId() + " on " + removed.getDate());
        }
    }

    public List<Shift> getAllShifts() {
        return findAll();
    }
}