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

    // GET /patient/appointments - show all appointments for logged-in patient
    @GetMapping("/appointments")
    public String viewAppointments(HttpSession session, Model model) {
        Patient patient = getLoggedInPatient(session);
        if (patient == null) {
            return "redirect:/patient/login";
        }

        List<Appointment> allAppointments = appointmentRepository.findAll().stream()
                .filter(a -> a.getPatient() != null && a.getPatient().getId().equals(patient.getId()))
                .collect(Collectors.toList());

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

    // GET /patient/appointments/book - show booking form with specialists
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

    // GET /patient/appointments/book/{specialistId} - show available time slots
    @GetMapping("/appointments/book/{specialistId}")
    public String showTimeSlots(@PathVariable Long specialistId, Model model, HttpSession session) {
        Patient patient = getLoggedInPatient(session);
        if (patient == null) {
            return "redirect:/patient/login";
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

    // POST /patient/appointments/book - save new appointment
    @PostMapping("/appointments/book")
    public String bookAppointment(@RequestParam Long specialistId,
                                  @RequestParam String dateTime,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        Patient patient = getLoggedInPatient(session);
        if (patient == null) {
            return "redirect:/patient/login";
        }

        LocalDateTime appointmentTime = LocalDateTime.parse(dateTime, DT_FORM);

        try {
            // Get the specialist
            var employee = employeeService.findById(specialistId);
            if (!(employee instanceof MedicalSpecialist specialist)) {
                throw new RuntimeException("Medical specialist not found");
            }

            // Check if slot is available
            if (!isSlotAvailable(specialistId, appointmentTime)) {
                throw new RuntimeException("Selected time slot is no longer available");
            }

            // Domain: patient creates their own appointment via model method
            Appointment appointment = patient.makeAppointment(specialist, appointmentTime);
            appointment.setId((int) System.currentTimeMillis());
            appointment.setDuration(30);
            appointment.setNotes("Booked via patient portal");
            appointment.setPaymentStatus(false);
            appointment.setStatus("PENDING");

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

    // POST /patient/appointments/{apptId}/reschedule - update date/time
    @PostMapping("/appointments/{apptId}/reschedule")
    public String rescheduleAppointment(@PathVariable int apptId,
                                        @RequestParam String newDateTime,
                                        HttpSession session,
                                        RedirectAttributes redirectAttributes) {
        Patient patient = getLoggedInPatient(session);
        if (patient == null) {
            return "redirect:/patient/login";
        }

        LocalDateTime newTime = LocalDateTime.parse(newDateTime, DT_FORM);

        try {
            Appointment appointment = appointmentRepository.findById(apptId);
            if (appointment == null) {
                throw new RuntimeException("Appointment not found");
            }

            if (!appointment.getPatient().getId().equals(patient.getId())) {
                throw new RuntimeException("You don't have permission to modify this appointment");
            }

            // Check if new slot is available
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

    // POST /patient/appointments/{apptId}/cancel - cancel appointment
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

    // Helper method to get logged-in patient from session
    private Patient getLoggedInPatient(HttpSession session) {
        return (Patient) session.getAttribute("patient");
    }

    // Helper method to check if a time slot is available for a specialist
    private boolean isSlotAvailable(Long specialistId, LocalDateTime dateTime) {
        List<Appointment> existingAppointments = appointmentRepository.findAll().stream()
                .filter(a -> a.getMedicalSpecialist() != null &&
                        a.getMedicalSpecialist().getId().equals(specialistId))
                .filter(a -> !"CANCELLED".equals(a.getStatus()))
                .collect(Collectors.toList());

        return existingAppointments.stream()
                .noneMatch(a -> a.getDateTime().equals(dateTime));
    }

    // Helper method to get available slots for a specialist (next 14 days, 9am-5pm, weekdays only)
    private List<LocalDateTime> getAvailableSlots(Long specialistId) {
        List<LocalDateTime> availableSlots = new java.util.ArrayList<>();

        List<Appointment> existingAppointments = appointmentRepository.findAll().stream()
                .filter(a -> a.getMedicalSpecialist() != null &&
                        a.getMedicalSpecialist().getId().equals(specialistId))
                .filter(a -> !"CANCELLED".equals(a.getStatus()))
                .collect(Collectors.toList());

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
                    // Fix the lambda warning by using a effectively final variable
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