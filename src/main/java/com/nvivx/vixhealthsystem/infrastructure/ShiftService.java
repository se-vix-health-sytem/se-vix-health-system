package com.nvivx.vixhealthsystem.infrastructure;

import com.nvivx.vixhealthsystem.model.staff.Shift;
import com.nvivx.vixhealthsystem.repository.JsonShiftRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ShiftService {

    private final JsonShiftRepository repository;

    public ShiftService(JsonShiftRepository repository) {
        this.repository = repository;
    }

    public Shift assignShift(long employeeId, LocalDate date, String shiftType, String notes) {

        List<Shift> shifts = repository.findAll();

        long nextId = shifts.stream()
                .mapToLong(Shift::getId)
                .max()
                .orElse(0L) + 1;

        Shift shift = new Shift(nextId, employeeId, date, shiftType, notes);

        shifts.add(shift);

        repository.saveAll(shifts);

        return shift;
    }

    public void deleteShift(long id) {
        List<Shift> shifts = repository.findAll();

        shifts.removeIf(s -> s.getId() == id);

        repository.saveAll(shifts);
    }

    public List<Shift> getEmployeeShifts(long employeeId) {
        return repository.findAll()
                .stream()
                .filter(s -> s.getEmployeeId() == employeeId)
                .toList();
    }
}