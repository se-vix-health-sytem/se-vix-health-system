// src/main/java/com/nvivx/vixhealthsystem/controllers/PatientAppointmentController.java
package com.nvivx.vixhealthsystem.controllers;

import com.nvivx.vixhealthsystem.mock.MockDatabase;
import com.nvivx.vixhealthsystem.model.medical.Appointment;
import com.nvivx.vixhealthsystem.model.person.Patient;
import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import com.nvivx.vixhealthsystem.service.PatientAppointmentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/patient")
public class PatientAppointmentController {

    private final PatientAppointmentService appointmentService;
    private final MockDatabase mockDatabase;

    public PatientAppointmentController(PatientAppointmentService appointmentService,
                                        MockDatabase mockDatabase) {
        this.appointmentService = appointmentService;
        this.mockDatabase = mockDatabase;
    }

    // GET /patient/appointments - show all appointments
    @GetMapping("/appointments")
    public String viewAppointments(HttpSession session, Model model) {
        Patient patient = getLoggedInPatient(session);
        if (patient == null) {
            return "redirect:/patient/login";
        }

        List<Appointment> allAppointments = appointmentService.getPatientAppointments(patient.getId());

        LocalDateTime now = LocalDateTime.now();
        List<Appointment> upcoming = allAppointments.stream()
                .filter(a -> a.getDateTime().isAfter(now) && !"CANCELLED".equals(a.getStatus()))
                .sorted((a, b) -> a.getDateTime().compareTo(b.getDateTime()))
                .collect(Collectors.toList());

        List<Appointment> past = allAppointments.stream()
                .filter(a -> a.getDateTime().isBefore(now) || "CANCELLED".equals(a.getStatus()))
                .sorted((a, b) -> b.getDateTime().compareTo(a.getDateTime()))
                .collect(Collectors.toList());

        model.addAttribute("upcomingAppointments", upcoming);
        model.addAttribute("pastAppointments", past);
        model.addAttribute("pageTitle", "My Appointments");
        model.addAttribute("patient", patient);
        return "patient/appointments";
    }

    // GET /patient/appointments/book - show booking form with doctors
    @GetMapping("/appointments/book")
    public String showBookingForm(Model model, HttpSession session) {
        Patient patient = getLoggedInPatient(session);
        if (patient == null) {
            return "redirect:/patient/login";
        }

        List<MedicalSpecialist> specialists = appointmentService.getAvailableSpecialists();
        model.addAttribute("specialists", specialists);
        model.addAttribute("patient", patient);
        model.addAttribute("pageTitle", "Book Appointment");
        return "patient/book-appointment";
    }


    // GET /patient/appointments/book/{doctorId} - show available time slots for a specific doctor
    @GetMapping("/appointments/book/{doctorId}")
    public String showTimeSlots(@PathVariable int doctorId, Model model, HttpSession session) {
        Patient patient = getLoggedInPatient(session);
        if (patient == null) {
            return "redirect:/patient/login";
        }

        MedicalSpecialist doctor = mockDatabase.findMedicalSpecialistById(doctorId);
        if (doctor == null) {
            return "redirect:/patient/appointments/book";
        }

        List<LocalDateTime> availableSlots = appointmentService.getAvailableSlots(doctorId, LocalDate.now(), LocalDate.now().plusDays(14));

        model.addAttribute("doctor", doctor);
        model.addAttribute("availableSlots", availableSlots);
        model.addAttribute("patient", patient);
        model.addAttribute("pageTitle", "Select Time Slot - Dr. " + doctor.getName());
        return "patient/select-slot";
    }

    // POST /patient/appointments/book - save new appointment
    @PostMapping("/appointments/book")
    public String bookAppointment(@RequestParam int doctorId,
                                  @RequestParam String dateTime,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        Patient patient = getLoggedInPatient(session);
        if (patient == null) {
            return "redirect:/patient/login";
        }

        LocalDateTime appointmentTime = LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        try {
            Appointment appointment = appointmentService.bookAppointment(patient.getId(), doctorId, appointmentTime);
            redirectAttributes.addFlashAttribute("message",
                    "✅ Appointment booked successfully!\n\n" +
                            "Date: " + appointmentTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "\n" +
                            "Doctor: Dr. " + appointmentService.getDoctorName(doctorId) + "\n" +
                            "Appointment ID: " + appointment.getId());
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

        LocalDateTime newTime = LocalDateTime.parse(newDateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        try {
            appointmentService.rescheduleAppointment(apptId, patient.getId(), newTime);
            redirectAttributes.addFlashAttribute("message",
                    "✅ Appointment rescheduled successfully!\n\n" +
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
            appointmentService.cancelAppointment(apptId, patient.getId());
            redirectAttributes.addFlashAttribute("message", "✅ Appointment cancelled successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ " + e.getMessage());
        }

        return "redirect:/patient/appointments";
    }

    private Patient getLoggedInPatient(HttpSession session) {
        return (Patient) session.getAttribute("patient");
    }
}