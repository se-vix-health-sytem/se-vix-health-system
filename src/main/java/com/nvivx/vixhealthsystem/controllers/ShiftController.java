package com.nvivx.vixhealthsystem.controllers;

import com.nvivx.vixhealthsystem.infrastructure.ShiftService;
import com.nvivx.vixhealthsystem.model.staff.Shift;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/shifts")
public class ShiftController {

    private final ShiftService service;

    public ShiftController(ShiftService service) {
        this.service = service;
    }

    @PostMapping
    public Shift createShift(
            @RequestParam long employeeId,
            @RequestParam String date,
            @RequestParam String shiftType,
            @RequestParam String notes) {

        return service.assignShift(
                employeeId,
                LocalDate.parse(date),
                shiftType,
                notes
        );
    }

    @DeleteMapping("/{id}")
    public void deleteShift(@PathVariable long id) {
        service.deleteShift(id);
    }

    @GetMapping("/{employeeId}")
    public List<Shift> getEmployeeShifts(@PathVariable long employeeId) {
        return service.getEmployeeShifts(employeeId);
    }
}