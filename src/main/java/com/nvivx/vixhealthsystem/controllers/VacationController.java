package com.nvivx.vixhealthsystem.controllers;

import com.nvivx.vixhealthsystem.infrastructure.VacationService;
import com.nvivx.vixhealthsystem.model.staff.Vacation;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/vacations")
public class VacationController {

    private final VacationService service;

    public VacationController(VacationService service) {
        this.service = service;
    }

    @PostMapping
    public Vacation createVacation(
            @RequestParam long employeeId,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam String notes) {

        return service.addVacation(
                employeeId,
                LocalDate.parse(startDate),
                LocalDate.parse(endDate),
                notes
        );
    }

    @DeleteMapping("/{id}")
    public void deleteVacation(@PathVariable long id) {
        service.deleteVacation(id);
    }

    @GetMapping("/{employeeId}")
    public List<Vacation> getEmployeeVacations(@PathVariable long employeeId) {
        return service.getEmployeeVacations(employeeId);
    }
}