package com.nvivx.vixhealthsystem.controllers.patient;

import com.nvivx.vixhealthsystem.model.medical.Appointment;
import com.nvivx.vixhealthsystem.model.person.Patient;
import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import com.nvivx.vixhealthsystem.repository.JsonAppointmentRepository;
import com.nvivx.vixhealthsystem.service.AuditService;
import com.nvivx.vixhealthsystem.service.core.EmployeeService;
import com.nvivx.vixhealthsystem.service.core.PatientService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @brief Controller for patient-side appointment operations — base URL {@code /patient}.
 *
 * Lets authenticated patients view their appointment history, browse available
 * time slots for a given specialist, book a new appointment, reschedule an
 * existing one, and cancel one.  Every mutation is written to the audit log
 * via {@link AuditService}.  All methods verify that the session contains a
 * valid patient object and redirect to the login page when it does not.
 *
 * @see PatientAuthController
 * @see JsonAppointmentRepository
 * @see AuditService
 */
@Controller
@RequestMapping("/patient")
public class PatientAppointmentController {

    private static final DateTimeFormatter DT_FORM = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm[:ss]");

    private final JsonAppointmentRepository appointmentRepository;
    private final PatientService patientService;
    private final EmployeeService employeeService;
    private final AuditService auditService;

    public PatientAppointmentController(JsonAppointmentRepository appointmentRepository,
                                        PatientService patientService,
                                        EmployeeService employeeService,
                                        AuditService auditService) {
        this.appointmentRepository = appointmentRepository;
        this.patientService = patientService;
        this.employeeService = employeeService;
        this.auditService = auditService;
    }

    // =========================================================
    // GET HANDLERS
    // =========================================================

    /**
     * GET /patient/appointments — display the logged-in patient's appointments split into upcoming and past.
     *
     * Upcoming appointments are those in the future with a status that is neither
     * CANCELLED nor COMPLETED.  Past appointments cover everything else, including
     * any future appointment that has been cancelled or marked complete early.
     *
     * @param session  HTTP session; must contain a {@code "patient"} attribute.
     * @param model    Receives {@code upcomingAppointments}, {@code pastAppointments},
     *                 {@code patient}, and {@code pageTitle} attributes.
     * @return         Thymeleaf template {@code patient/appointments}, or
     *                 {@code redirect:/patient/login} when no session patient is found.
     */
    @GetMapping("/appointments")
    public String viewAppointments(HttpSession session, Model model) {
        Patient patient = getLoggedInPatient(session);
        if (patient == null) {
            return "redirect:/patient/login";
        }

        List<Appointment> allAppointments;
        try {
            allAppointments = appointmentRepository.findAll().stream()
                    .filter(a -> a.getPatient() != null && a.getPatient().getId().equals(patient.getId()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            allAppointments = new java.util.ArrayList<>();
        }

        LocalDateTime now = LocalDateTime.now();

        List<Appointment> upcoming = allAppointments.stream()
                .filter(a -> a.getDateTime().isAfter(now) &&
                        !"CANCELLED".equals(a.getStatus()) &&
                        !"COMPLETED".equals(a.getStatus()))
                .sorted((a, b) -> a.getDateTime().compareTo(b.getDateTime()))
                .collect(Collectors.toList());

        List<Appointment> past = allAppointments.stream()
                .filter(a -> a.getDateTime().isBefore(now) ||
                        "CANCELLED".equals(a.getStatus()) ||
                        "COMPLETED".equals(a.getStatus()))
                .sorted((a, b) -> b.getDateTime().compareTo(a.getDateTime()))
                .collect(Collectors.toList());

        model.addAttribute("upcomingAppointments", upcoming);
        model.addAttribute("pastAppointments", past);
        model.addAttribute("pageTitle", "My Appointments");
        model.addAttribute("patient", patient);
        model.addAttribute("currentPage", "appointments");
        return "patient/appointments";
    }

    /**
     * GET /patient/appointments/book — render the specialist selection step of the booking flow.
     *
     * @param model    Receives {@code specialists}, {@code patient}, and {@code pageTitle}.
     * @param session  HTTP session; must contain a {@code "patient"} attribute.
     * @return         Thymeleaf template {@code patient/book-appointment}, or
     *                 {@code redirect:/patient/login} when no session patient is found.
     */
    @GetMapping("/appointments/book")
    public String showBookingForm(Model model, HttpSession session) {
        Patient patient = getLoggedInPatient(session);
        if (patient == null) {
            return "redirect:/patient/login";
        }

        List<MedicalSpecialist> specialists = employeeService.findAllMedicalSpecialists();
        model.addAttribute("specialists", specialists);
        model.addAttribute("patient", patient);
        model.addAttribute("pageTitle", "Book Appointment");
        model.addAttribute("currentPage", "bookAppointment");
        return "patient/book-appointment";
    }

    /**
     * GET /patient/appointments/book/{specialistId} — display available time slots for a specialist.
     *
     * Generates hourly slots over the next 14 weekdays (09:00–17:00) and
     * removes any slot already taken by a non-cancelled appointment.
     *
     * @param specialistId  Database ID of the chosen MedicalSpecialist.
     * @param model         Receives {@code specialist}, {@code availableSlots}, and {@code patient}.
     * @param session       HTTP session; must contain a {@code "patient"} attribute.
     * @return              Thymeleaf template {@code patient/select-slot}, or a redirect
     *                      when the specialist is not found or the session is empty.
     */
    @GetMapping("/appointments/book/{specialistId}")
    public String showTimeSlots(@PathVariable Long specialistId, Model model, HttpSession session) {
        Patient patient = getLoggedInPatient(session);
        if (patient == null) {
            return "redirect:/patient/login?redirect=appointments/book/" + specialistId;
        }

        MedicalSpecialist specialist = null;
        try {
            var employee = employeeService.findById(specialistId);
            if (employee instanceof MedicalSpecialist ms) {
                specialist = ms;
            }
        } catch (Exception e) {
            return "redirect:/patient/appointments/book";
        }

        if (specialist == null) {
            return "redirect:/patient/appointments/book";
        }

        List<LocalDateTime> availableSlots = getAvailableSlots(specialistId);

        model.addAttribute("specialist", specialist);
        model.addAttribute("availableSlots", availableSlots);
        model.addAttribute("patient", patient);
        model.addAttribute("pageTitle", "Select Time Slot - Dr. " + specialist.getName() + " " + specialist.getSurname());
        return "patient/select-slot";
    }

    // =========================================================
    // POST HANDLERS
    // =========================================================

    /**
     * POST /patient/appointments/book — save a new appointment for the logged-in patient.
     *
     * Uses the domain method {@code Patient.makeAppointment} to create the
     * {@link Appointment} entity so that model-level invariants are enforced.
     * The slot availability check is re-run here to guard against race conditions
     * since the form was rendered.
     *
     * @param specialistId        Database ID of the chosen MedicalSpecialist.
     * @param dateTime            Chosen slot in {@code yyyy-MM-ddTHH:mm} format.
     * @param session             HTTP session; must contain a {@code "patient"} attribute.
     * @param redirectAttributes  Flash attributes for the redirect to {@code /patient/appointments}.
     * @return                    Redirect to {@code /patient/appointments}.
     */
    @PostMapping("/appointments/book")
    public String bookAppointment(@RequestParam Long specialistId,
                                  @RequestParam String dateTime,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        Patient patient = getLoggedInPatient(session);
        if (patient == null) {
            return "redirect:/patient/login";
        }

        try {
            LocalDateTime appointmentTime = LocalDateTime.parse(dateTime, DT_FORM);

            var employee = employeeService.findById(specialistId);
            if (!(employee instanceof MedicalSpecialist specialist)) {
                throw new RuntimeException("Medical specialist not found");
            }

            if (!isSlotAvailable(specialistId, appointmentTime)) {
                throw new RuntimeException("Selected time slot is no longer available");
            }

            // Domain: patient creates their own appointment via model method
            Appointment appointment = patient.makeAppointment(specialist, appointmentTime);
            appointment.setId(0); // let the repository assign a sequential ID
            appointment.setDuration(30);
            appointment.setNotes("Booked via patient portal");
            appointment.setPaymentStatus(false);
            appointment.setStatus("CONFIRMED");

            Appointment saved = appointmentRepository.save(appointment);
            auditService.log("BOOK_APPOINTMENT", "Appointment", String.valueOf(saved.getId()),
                "Patient " + patient.getFiscalCode() + " booked appointment with Dr. "
                + specialist.getName() + " " + specialist.getSurname()
                + " on " + appointmentTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

            redirectAttributes.addFlashAttribute("message",
                    "✅ Appointment booked successfully!\n\n" +
                            "Date: " + appointmentTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "\n" +
                            "Specialist: Dr. " + specialist.getName() + " " + specialist.getSurname() + "\n" +
                            "Appointment ID: " + saved.getId());

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ " + e.getMessage());
        }

        return "redirect:/patient/appointments";
    }

    /**
     * POST /patient/appointments/{apptId}/reschedule — move an existing appointment to a new slot.
     *
     * Verifies that the appointment belongs to the logged-in patient before
     * delegating to the domain method {@code Appointment.reschedule}.  The new
     * slot availability is checked before the mutation to prevent double-booking.
     *
     * @param apptId              Numeric ID of the appointment to reschedule.
     * @param newDateTime         New datetime in {@code yyyy-MM-ddTHH:mm} format.
     * @param session             HTTP session; must contain a {@code "patient"} attribute.
     * @param redirectAttributes  Flash attributes for the redirect.
     * @return                    Redirect to {@code /patient/appointments}.
     */
    @PostMapping("/appointments/{apptId}/reschedule")
    public String rescheduleAppointment(@PathVariable int apptId,
                                        @RequestParam String newDateTime,
                                        HttpSession session,
                                        RedirectAttributes redirectAttributes) {
        Patient patient = getLoggedInPatient(session);
        if (patient == null) {
            return "redirect:/patient/login";
        }

        try {
            LocalDateTime newTime = LocalDateTime.parse(newDateTime, DT_FORM);
            Appointment appointment = appointmentRepository.findById(apptId);
            if (appointment == null) {
                throw new RuntimeException("Appointment not found");
            }

            if (!appointment.getPatient().getId().equals(patient.getId())) {
                throw new RuntimeException("You don't have permission to modify this appointment");
            }

            Long specialistId = appointment.getMedicalSpecialist().getId();
            if (!isSlotAvailable(specialistId, newTime)) {
                throw new RuntimeException("New time slot is not available");
            }

            LocalDateTime oldTime = appointment.getDateTime();
            // Domain: appointment reschedules itself via its own domain method
            appointment.reschedule(newTime);
            appointmentRepository.save(appointment);
            auditService.log("RESCHEDULE_APPOINTMENT", "Appointment", String.valueOf(apptId),
                "Patient " + patient.getFiscalCode() + " rescheduled appointment from "
                + oldTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                + " to " + newTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

            redirectAttributes.addFlashAttribute("message",
                    "✅ Appointment rescheduled successfully!\n\n" +
                            "Old Date/Time: " + oldTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "\n" +
                            "New Date/Time: " + newTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ " + e.getMessage());
        }

        return "redirect:/patient/appointments";
    }

    /**
     * POST /patient/appointments/{apptId}/cancel — cancel one of the patient's appointments.
     *
     * Verifies ownership before calling the domain method {@code Appointment.cancel}
     * so that a patient cannot cancel another patient's appointment by guessing an ID.
     *
     * @param apptId              Numeric ID of the appointment to cancel.
     * @param session             HTTP session; must contain a {@code "patient"} attribute.
     * @param redirectAttributes  Flash attributes for the redirect.
     * @return                    Redirect to {@code /patient/appointments}.
     */
    @PostMapping("/appointments/{apptId}/cancel")
    public String cancelAppointment(@PathVariable int apptId,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        Patient patient = getLoggedInPatient(session);
        if (patient == null) {
            return "redirect:/patient/login";
        }

        try {
            Appointment appointment = appointmentRepository.findById(apptId);
            if (appointment == null) {
                throw new RuntimeException("Appointment not found");
            }

            if (!appointment.getPatient().getId().equals(patient.getId())) {
                throw new RuntimeException("You don't have permission to cancel this appointment");
            }

            // Domain: appointment cancels itself via its own domain method
            appointment.cancel();
            appointmentRepository.save(appointment);
            auditService.log("CANCEL_APPOINTMENT", "Appointment", String.valueOf(apptId),
                "Patient " + patient.getFiscalCode() + " cancelled their appointment");

            redirectAttributes.addFlashAttribute("message", "✅ Appointment cancelled successfully!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ " + e.getMessage());
        }

        return "redirect:/patient/appointments";
    }

    // =========================================================
    // HELPERS
    // =========================================================

    /**
     * Retrieve the logged-in patient from the HTTP session.
     *
     * @param session  HTTP session that may contain a {@code "patient"} attribute.
     * @return         The session {@link Patient}, or {@code null} when absent.
     */
    private Patient getLoggedInPatient(HttpSession session) {
        return (Patient) session.getAttribute("patient");
    }

    /**
     * Check whether a given time slot is still free for a specialist.
     *
     * A slot is considered taken when any non-cancelled appointment for the same
     * specialist has an identical {@code dateTime} value.
     *
     * @param specialistId  Database ID of the specialist.
     * @param dateTime      The slot datetime to check.
     * @return              {@code true} if no conflicting appointment exists.
     */
    private boolean isSlotAvailable(Long specialistId, LocalDateTime dateTime) {
        try {
            return appointmentRepository.findAll().stream()
                    .filter(a -> a.getMedicalSpecialist() != null &&
                            a.getMedicalSpecialist().getId().equals(specialistId))
                    .filter(a -> !"CANCELLED".equals(a.getStatus()))
                    .noneMatch(a -> a.getDateTime().equals(dateTime));
        } catch (Exception e) {
            return true; // assume available if we can't read existing appointments
        }
    }

    /**
     * Generate the list of available hourly slots for a specialist over the next 14 weekdays.
     *
     * Working hours are 09:00–17:00 Monday to Friday.  Slots already taken by
     * non-cancelled appointments are excluded.  If the current time is past 17:00
     * the generation starts from 09:00 the following day; otherwise it starts
     * from the next whole hour today.
     *
     * @param specialistId  Database ID of the specialist whose calendar to query.
     * @return              Ordered list of free {@link LocalDateTime} slots.
     */
    private List<LocalDateTime> getAvailableSlots(Long specialistId) {
        List<LocalDateTime> availableSlots = new java.util.ArrayList<>();

        List<Appointment> existingAppointments;
        try {
            existingAppointments = appointmentRepository.findAll().stream()
                    .filter(a -> a.getMedicalSpecialist() != null &&
                            a.getMedicalSpecialist().getId().equals(specialistId))
                    .filter(a -> !"CANCELLED".equals(a.getStatus()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            existingAppointments = new java.util.ArrayList<>();
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDate = now.plusDays(14);
        LocalDateTime current = now.withHour(9).withMinute(0).withSecond(0).withNano(0);

        // If current time is past 9am, start from next hour
        if (now.getHour() >= 17) {
            current = current.plusDays(1).withHour(9);
        } else if (now.getHour() >= 9) {
            current = current.withHour(now.getHour() + 1);
        }

        while (current.isBefore(endDate)) {
            // Only weekdays (Monday=1 to Friday=5)
            int dayOfWeek = current.getDayOfWeek().getValue();
            if (dayOfWeek >= 1 && dayOfWeek <= 5) {
                // Working hours 9am to 5pm
                if (current.getHour() >= 9 && current.getHour() < 17) {
                    final LocalDateTime slotToCheck = current;
                    boolean isBooked = existingAppointments.stream()
                            .anyMatch(a -> a.getDateTime().equals(slotToCheck));

                    if (!isBooked) {
                        availableSlots.add(current);
                    }
                }
            }

            // Move to next hour
            current = current.plusHours(1);

            // If we've passed 5pm, move to next day 9am
            if (current.getHour() >= 17) {
                current = current.plusDays(1).withHour(9);
            }
        }

        return availableSlots;
    }
}
