package com.nvivx.vixhealthsystem.controllers;

import com.nvivx.vixhealthsystem.model.facility.Room;
import com.nvivx.vixhealthsystem.model.medical.Appointment;
import com.nvivx.vixhealthsystem.model.person.Patient;
import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import com.nvivx.vixhealthsystem.service.SecretaryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/secretary")
public class SecretaryController {

    private final SecretaryService secretaryService;

    public SecretaryController(SecretaryService secretaryService) {
        this.secretaryService = secretaryService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "Secretary Dashboard");
        model.addAttribute("totalRooms", secretaryService.getAllRooms().size());
        model.addAttribute("availableRooms", secretaryService.getAvailableRooms().size());
        model.addAttribute("totalAppointments", secretaryService.getAllAppointments().size());
        return "secretary/dashboard";
    }

    // ========== ROOM MANAGEMENT (UC21) ==========

    @GetMapping("/rooms")
    public String viewAllRooms(Model model) {
        List<Room> rooms = secretaryService.getAllRooms();
        model.addAttribute("rooms", rooms);
        model.addAttribute("pageTitle", "All Rooms");
        return "secretary/rooms";
    }

    @GetMapping("/rooms/available")
    public String viewAvailableRooms(Model model) {
        List<Room> availableRooms = secretaryService.getAvailableRooms();
        model.addAttribute("rooms", availableRooms);
        model.addAttribute("pageTitle", "Available Rooms");
        model.addAttribute("isAvailableView", true);
        return "secretary/rooms";
    }

    // ========== PATIENT ADMISSION & DISMISSAL (UC22, UC23) ==========

    @PostMapping("/patients/admit")
    public String admitPatient(@RequestParam Long patientId,
                               @RequestParam Long roomId,
                               RedirectAttributes redirectAttributes) {
        try {
            Room room = secretaryService.admitPatient(patientId, roomId);
            redirectAttributes.addFlashAttribute("message",
                    "✅ Patient #" + patientId + " admitted successfully to room " + room.getRoomNumber());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Admission failed: " + e.getMessage());
        }
        return "redirect:/secretary/rooms";
    }

    @PostMapping("/patients/dismiss")
    public String dismissPatient(@RequestParam Long patientId,
                                 @RequestParam Long roomId,
                                 RedirectAttributes redirectAttributes) {
        try {
            Room room = secretaryService.dismissPatient(patientId, roomId);
            redirectAttributes.addFlashAttribute("message",
                    "✅ Patient #" + patientId + " dismissed successfully from room " + room.getRoomNumber());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Dismissal failed: " + e.getMessage());
        }
        return "redirect:/secretary/rooms";
    }

    // ========== APPOINTMENT MANAGEMENT (UC14, UC15, UC16) ==========

    @GetMapping("/appointments")
    public String manageAppointments(Model model) {
        List<Appointment> appointments = secretaryService.getAllAppointments();
        List<Patient> patients = secretaryService.getAllPatients();
        List<MedicalSpecialist> specialists = secretaryService.getAllMedicalSpecialists();

        model.addAttribute("appointments", appointments);
        model.addAttribute("patients", patients);
        model.addAttribute("specialists", specialists);
        model.addAttribute("pageTitle", "Manage Appointments");
        return "secretary/manage-appointments";
    }

    @PostMapping("/appointments/book-for-patient")
    public String bookAppointmentForPatient(@RequestParam Long patientId,
                                            @RequestParam Long specialistId,
                                            @RequestParam String dateTime,
                                            RedirectAttributes redirectAttributes) {
        try {
            LocalDateTime appointmentTime = LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            Appointment appointment = secretaryService.bookAppointmentForPatient(patientId, specialistId, appointmentTime);
            redirectAttributes.addFlashAttribute("message",
                    "✅ Appointment booked successfully!\nID: " + appointment.getId());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Booking failed: " + e.getMessage());
        }
        return "redirect:/secretary/appointments";
    }

    @PostMapping("/appointments/{appointmentId}/cancel")
    public String cancelAppointment(@PathVariable Long appointmentId,
                                    RedirectAttributes redirectAttributes) {
        try {
            secretaryService.cancelAppointment(appointmentId);
            redirectAttributes.addFlashAttribute("message", "✅ Appointment cancelled successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Cancellation failed: " + e.getMessage());
        }
        return "redirect:/secretary/appointments";
    }

    @PostMapping("/appointments/{appointmentId}/reschedule")
    public String rescheduleAppointment(@PathVariable Long appointmentId,
                                        @RequestParam String newDateTime,
                                        RedirectAttributes redirectAttributes) {
        try {
            LocalDateTime newTime = LocalDateTime.parse(newDateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            secretaryService.rescheduleAppointment(appointmentId, newTime);
            redirectAttributes.addFlashAttribute("message", "✅ Appointment rescheduled successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Rescheduling failed: " + e.getMessage());
        }
        return "redirect:/secretary/appointments";
    }

    // ========== PATIENT SEARCH ==========

    @GetMapping("/patients/search")
    public String showPatientSearchForm(Model model) {
        model.addAttribute("pageTitle", "Search Patients");
        return "secretary/patient-search";
    }

    @PostMapping("/patients/search")
    public String searchPatients(@RequestParam String query, Model model) {
        List<Patient> patients = secretaryService.searchPatients(query);
        model.addAttribute("patients", patients);
        model.addAttribute("query", query);
        model.addAttribute("pageTitle", "Search Results");
        return "secretary/patient-search-results";
    }
}