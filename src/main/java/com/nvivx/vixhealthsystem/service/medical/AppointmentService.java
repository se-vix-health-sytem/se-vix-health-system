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

/**
 * @brief Manages appointment booking, rescheduling, and cancellation for the VIX Health System.
 *
 * Appointments are stored in a JSON file via {@link JsonAppointmentRepository} rather than
 * a relational table, so there is no {@code @Transactional} support; all reads and writes
 * are synchronized by the repository implementation.
 *
 * Slot-conflict detection is enforced before every new booking to prevent double-booking
 * across the shared appointment list.
 *
 * @see com.nvivx.vixhealthsystem.model.medical.Appointment
 * @see com.nvivx.vixhealthsystem.repository.JsonAppointmentRepository
 * @see com.nvivx.vixhealthsystem.exception.SlotNotAvailableException
 */
@Service
public class AppointmentService {

    // =========================================================
    // FIELDS
    // =========================================================

    private final JsonAppointmentRepository repo;

    // =========================================================
    // CONSTRUCTORS
    // =========================================================

    /**
     * Constructs the service with the JSON-backed appointment repository.
     *
     * @param repo  File-based repository that reads and writes {@code appointments.json}.
     */
    public AppointmentService(JsonAppointmentRepository repo) {
        this.repo = repo;
    }

    // =========================================================
    // WRITE OPERATIONS
    // =========================================================

    /**
     * Books a new appointment from a structured request DTO, checking for slot conflicts.
     *
     * @param req  Appointment details including date-time, duration, and notes.
     * @return     The persisted {@link Appointment} with a generated ID.
     * @throws SlotNotAvailableException When the requested time slot overlaps an existing booking.
     */
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
     * Books an appointment on behalf of a patient through a secretary (UC-Secretary flow).
     *
     * Delegates to {@link Secretary#makeAppointmentForPatient} so that domain invariants
     * (e.g., patient eligibility) are enforced by the model.  The slot-conflict guard
     * runs before the domain call to fail fast.
     *
     * @param secretary   The acting secretary; must not be {@code null}.
     * @param patient     The patient for whom the appointment is being booked.
     * @param specialist  The medical specialist who will conduct the appointment.
     * @param dt          Requested date and time for the appointment.
     * @param duration    Duration in minutes.
     * @param notes       Optional clinical notes; may be {@code null}.
     * @return            The persisted {@link Appointment}.
     * @throws SlotNotAvailableException When the slot overlaps an existing booking.
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
     * Cancels an appointment, delegating cancellability checks to the domain model.
     *
     * @param appointmentId  ID of the appointment to cancel.
     * @return               The updated {@link Appointment} with canceled status.
     * @throws RuntimeException When no appointment with {@code appointmentId} exists.
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
     * Reschedules an active appointment, delegating validity checks to the domain model.
     *
     * @param appointmentId  ID of the appointment to reschedule.
     * @param newDateTime    The new date and time; must be in the future.
     * @return               The updated {@link Appointment} with the new date-time.
     * @throws RuntimeException When no appointment with {@code appointmentId} exists.
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

    // =========================================================
    // READ OPERATIONS
    // =========================================================

    /** @brief Returns all appointments in the system regardless of status. */
    public List<Appointment> getAllAppointments() {
        return repo.findAll();
    }

    // =========================================================
    // HELPERS
    // =========================================================

    /**
     * Throws {@link SlotNotAvailableException} if {@code newStart + duration} overlaps
     *        any existing appointment.
     *
     * Two intervals overlap when {@code newStart < existingEnd && newEnd > existingStart}.
     *
     * @param existing  Current list of all appointments.
     * @param newStart  Requested start date-time for the new booking.
     * @param duration  Duration of the new booking in minutes.
     * @throws SlotNotAvailableException When at least one overlap is detected.
     */
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