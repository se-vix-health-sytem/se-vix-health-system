package com.nvivx.vixhealthsystem.infrastructure;

import com.nvivx.vixhealthsystem.model.staff.Vacation;
import com.nvivx.vixhealthsystem.repository.JsonVacationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class VacationService {

    private final JsonVacationRepository repository;

    public VacationService(JsonVacationRepository repository) {
        this.repository = repository;
    }

    public Vacation addVacation(long employeeId, LocalDate startDate, LocalDate endDate, String notes) {

        List<Vacation> vacations = repository.findAll();

        long nextId = vacations.stream()
                .mapToLong(Vacation::getId)
                .max()
                .orElse(0L) + 1;

        Vacation vacation = new Vacation(nextId, employeeId, startDate, endDate, notes);

        vacations.add(vacation);

        repository.saveAll(vacations);

        return vacation;
    }

    public void deleteVacation(long id) {

        List<Vacation> vacations = repository.findAll();

        vacations.removeIf(v -> v.getId() == id);

        repository.saveAll(vacations);
    }

    public List<Vacation> getEmployeeVacations(long employeeId) {

        return repository.findAll()
                .stream()
                .filter(v -> v.getEmployeeId() == employeeId)
                .toList();
    }
}