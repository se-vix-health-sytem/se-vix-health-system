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

/**
 * Manages employee shift assignments, backed by a JSON file rather than the database.
 *
 * Shifts are stored in {@code src/main/resources/storage/shifts.json}. The JSON
 * approach was chosen because shift data changes frequently and has no relational
 * dependencies that require foreign-key constraints. Every write is flushed to disk
 * immediately (no caching) so the file always reflects the current state.
 *
 * The ID sequence is rebuilt from the existing JSON on startup, which means IDs
 * continue incrementing even after a server restart : they are never reused.
 *
 * All mutating operations are audit-logged via {@link AuditService}.
 *
 * @see VacationService
 * @see com.nvivx.vixhealthsystem.model.staff.Shift
 */
@Service
public class ShiftService {

    // =========================================================
    // FIELDS
    // =========================================================

    private final ObjectMapper mapper;
    /** Path to the JSON shift store, relative to the working directory. */
    private final String shiftsPath = "src/main/resources/storage/shifts.json";
    private final AuditService auditService;
    /** Auto-increments on every new shift; seeded from the max existing ID at startup. */
    private final AtomicLong idGenerator = new AtomicLong(1);

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    /**
     * Wires up dependencies and ensures the JSON file exists.
     *
     * @param auditService Used to record shift assignments and removals.
     */
    public ShiftService(AuditService auditService) {
        this.auditService = auditService;
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        initializeStorage();
    }

    // =========================================================
    // INTERNAL STORAGE HELPERS
    // =========================================================

    /** Creates the JSON file if it doesn't exist, then seeds the ID generator. */
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

    /** Reads the entire JSON file and deserialises it into a list of shifts. */
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

    /** Serialises the full list back to disk, overwriting the file. */
    private void saveAll(List<Shift> shifts) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(shiftsPath), shifts);
        } catch (IOException e) {
            throw new RuntimeException("Error writing shifts.json", e);
        }
    }

    // =========================================================
    // READ OPERATIONS
    // =========================================================

    /**
     * Returns all shifts assigned to a specific employee.
     *
     * @param employeeId  The employee's primary key.
     * @return            List of shifts; empty if the employee has none.
     */
    public List<Shift> getShiftsForEmployee(Long employeeId) {
        return findAll().stream()
                .filter(s -> s.getEmployeeId().equals(employeeId))
                .collect(Collectors.toList());
    }

    /**
     * Returns all shifts that fall within the 7-day window starting on {@code startDate}.
     *
     * @param startDate  First day of the week (inclusive).
     * @return           Shifts across all employees for those 7 days.
     */
    public List<Shift> getShiftsForWeek(LocalDate startDate) {
        LocalDate endDate = startDate.plusDays(6);
        return findAll().stream()
                .filter(s -> !s.getDate().isBefore(startDate) && !s.getDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    /**
     * Returns an employee's shifts within an inclusive date range.
     *
     * @param employeeId  The employee's primary key.
     * @param startDate   Start of the range (inclusive).
     * @param endDate     End of the range (inclusive).
     * @return            Matching shifts; empty list if none found.
     */
    public List<Shift> getShiftsForEmployeeBetweenDates(Long employeeId, LocalDate startDate, LocalDate endDate) {
        return findAll().stream()
                .filter(s -> s.getEmployeeId().equals(employeeId))
                .filter(s -> !s.getDate().isBefore(startDate) && !s.getDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    /**
     * Checks whether an employee is scheduled on a given date.
     *
     * @param employeeId  The employee's primary key.
     * @param date        The date to check.
     * @return            {@code true} if at least one shift exists for that day.
     */
    public boolean isEmployeeOnShift(Long employeeId, LocalDate date) {
        return getShiftsForEmployee(employeeId).stream()
                .anyMatch(s -> s.getDate().equals(date));
    }

    // =========================================================
    // WRITE OPERATIONS
    // =========================================================

    /**
     * Assigns a shift to an employee on a given date, replacing any existing shift for that day.
     *
     * Only one shift per employee per day is allowed : if one already exists, it is silently
     * removed before the new one is inserted.
     *
     * @param employeeId  The employee's primary key.
     * @param date        The date of the shift.
     * @param shiftType   One of {@code "MORNING"}, {@code "AFTERNOON"}, or {@code "NIGHT"}.
     */
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

    /**
     * Removes a shift by ID. Does nothing if the ID doesn't exist.
     *
     * @param shiftId  ID of the shift to delete.
     */
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

    /** @brief Returns all shifts in the system, used by the staff manager overview. */
    public List<Shift> getAllShifts() {
        return findAll();
    }
}