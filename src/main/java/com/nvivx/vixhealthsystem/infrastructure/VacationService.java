package com.nvivx.vixhealthsystem.service;

import com.nvivx.vixhealthsystem.exception.VacationNotFoundException;
import com.nvivx.vixhealthsystem.model.staff.VacationRequest;
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

    public VacationRequest requestVacation(
            int employeeId,
            LocalDate startDate,
            LocalDate endDate,
            String reason) {

        List<VacationRequest> vacations =
                repository.findAll();

        VacationRequest request =
                new VacationRequest(
                        vacations.size() + 1,
                        employeeId,
                        startDate,
                        endDate,
                        reason,
                        "PENDING"
                );

        vacations.add(request);

        repository.saveAll(vacations);

        return request;
    }

    public VacationRequest approveVacation(int id) {

        List<VacationRequest> vacations =
                repository.findAll();

        for (VacationRequest request : vacations) {

            if (request.getId() == id) {

                request.setStatus("APPROVED");

                repository.saveAll(vacations);

                return request;
            }
        }

        throw new VacationNotFoundException(
                "Vacation request not found"
        );
    }

    public VacationRequest rejectVacation(int id) {

        List<VacationRequest> vacations =
                repository.findAll();

        for (VacationRequest request : vacations) {

            if (request.getId() == id) {

                request.setStatus("REJECTED");

                repository.saveAll(vacations);

                return request;
            }
        }

        throw new VacationNotFoundException(
                "Vacation request not found"
        );
    }

    public List<VacationRequest> getAllRequests() {

        return repository.findAll();
    }

    public VacationRequest approve(int id) {
        List<VacationRequest> list = repository.findAll();

        for (VacationRequest v : list) {
            if (v.getId() == id) {
                v.setStatus("APPROVED");
                repository.saveAll(list);
                return v;
            }
        }

        throw new VacationNotFoundException("Vacation not found");
    }
    public VacationRequest reject(int id) {
        List<VacationRequest> list = repository.findAll();

        for (VacationRequest v : list) {
            if (v.getId() == id) {
                v.setStatus("REJECTED");
                repository.saveAll(list);
                return v;
            }
        }

        throw new VacationNotFoundException("Vacation not found");
    }
}