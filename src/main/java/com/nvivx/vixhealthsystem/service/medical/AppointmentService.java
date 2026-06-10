package com.nvivx.vixhealthsystem.service.medical;

import com.nvivx.vixhealthsystem.dto.CreateAppointmentRequest;
import com.nvivx.vixhealthsystem.exception.SlotNotAvailableException;
import com.nvivx.vixhealthsystem.model.medical.Appointment;
import com.nvivx.vixhealthsystem.repository.JsonAppointmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentService {

    private final JsonAppointmentRepository repo;

    public AppointmentService(JsonAppointmentRepository repo) {
        this.repo = repo;
    }

    public Appointment bookAppointment(CreateAppointmentRequest req) {

        List<Appointment> all = repo.findAll();

        // CHECK SLOT AVAILABILITY
        boolean conflict = all.stream().anyMatch(a ->
                a.getDateTime().equals(req.getDateTime())
        );

        if (conflict) {
            throw new SlotNotAvailableException("This slot is already booked");
        }

        Appointment appt = new Appointment(
                (int) System.currentTimeMillis(),
                req.getDateTime(),
                req.getDuration(),
                req.getNotes()
        );

        all.add(appt);
        repo.saveAll(all);

        return appt;
    }

    public List<Appointment> getAllAppointments() {
        return repo.findAll();
    }
}