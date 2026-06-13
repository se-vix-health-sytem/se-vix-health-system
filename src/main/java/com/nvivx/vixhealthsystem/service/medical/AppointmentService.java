package com.nvivx.vixhealthsystem.service.medical;

import com.nvivx.vixhealthsystem.dto.CreateAppointmentRequest;
import com.nvivx.vixhealthsystem.exception.SlotNotAvailableException;
import com.nvivx.vixhealthsystem.model.medical.Appointment;
import com.nvivx.vixhealthsystem.model.person.Patient;
import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import com.nvivx.vixhealthsystem.model.person.employee.Secretary;
import com.nvivx.vixhealthsystem.repository.JsonAppointmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService {

    private final JsonAppointmentRepository repo;

    public AppointmentService(JsonAppointmentRepository repo) {
        this.repo = repo;
    }

    public Appointment bookAppointment(CreateAppointmentRequest req) {
        List<Appointment> all = repo.findAll();

        checkConflict(all, req.getDateTime(), req.getDuration());

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

    /**
     * Books an appointment on behalf of a patient through a secretary.
     * Uses the Secretary domain method, which delegates to Patient domain logic.
     */
    public Appointment bookForSecretary(Secretary secretary,
                                        Patient patient,
                                        MedicalSpecialist specialist,
                                        LocalDateTime dt,
                                        int duration,
                                        String notes) {
        List<Appointment> all = repo.findAll();
        checkConflict(all, dt, duration);

        // Domain: secretary creates appointment for patient via model methods
        Appointment appt = secretary.makeAppointmentForPatient(patient, specialist, dt);
        appt.setId((int) System.currentTimeMillis());
        appt.setDuration(duration);
        appt.setNotes(notes);
        appt.setStatus("PENDING");
        appt.setPaymentStatus(false);

        all.add(appt);
        repo.saveAll(all);

        return appt;
    }

    /**
     * Cancels an appointment by ID using the Appointment domain method.
     */
    public Appointment cancelAppointment(int appointmentId) {
        List<Appointment> all = repo.findAll();
        Appointment appt = all.stream()
                .filter(a -> a.getId() == appointmentId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Appointment not found: " + appointmentId));

        appt.cancel(); // domain method — enforces isCancellable()
        repo.saveAll(all);
        return appt;
    }

    /**
     * Reschedules an appointment by ID using the Appointment domain method.
     */
    public Appointment rescheduleAppointment(int appointmentId, LocalDateTime newDateTime) {
        List<Appointment> all = repo.findAll();
        Appointment appt = all.stream()
                .filter(a -> a.getId() == appointmentId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Appointment not found: " + appointmentId));

        appt.reschedule(newDateTime); // domain method — enforces isActive()
        repo.saveAll(all);
        return appt;
    }

    public List<Appointment> getAllAppointments() {
        return repo.findAll();
    }

    private void checkConflict(List<Appointment> existing, LocalDateTime newStart, int duration) {
        LocalDateTime newEnd = newStart.plusMinutes(duration);
        boolean conflict = existing.stream().anyMatch(a -> {
            LocalDateTime existingStart = a.getDateTime();
            LocalDateTime existingEnd = existingStart.plusMinutes(a.getDuration());
            return newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart);
        });
        if (conflict) {
            throw new SlotNotAvailableException("This slot is already booked");
        }
    }
}