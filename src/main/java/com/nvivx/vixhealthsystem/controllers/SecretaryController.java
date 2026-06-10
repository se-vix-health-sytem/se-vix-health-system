package com.nvivx.vixhealthsystem.controllers;

import com.nvivx.vixhealthsystem.model.facility.Room;
import com.nvivx.vixhealthsystem.model.facility.InternationRoom;
import com.nvivx.vixhealthsystem.model.medical.Appointment;
import com.nvivx.vixhealthsystem.model.person.Patient;
import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import com.nvivx.vixhealthsystem.repository.JsonAppointmentRepository;
import com.nvivx.vixhealthsystem.service.core.EmployeeService;
import com.nvivx.vixhealthsystem.service.core.PatientService;
import com.nvivx.vixhealthsystem.service.resources.RoomService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/secretary")
public class SecretaryController {

    private final RoomService roomService;
    private final EmployeeService employeeService;
    private final PatientService patientService;
    private final JsonAppointmentRepository appointmentRepository;

    public SecretaryController(RoomService roomService,
                               EmployeeService employeeService,
                               PatientService patientService,
                               JsonAppointmentRepository appointmentRepository) {
        this.roomService = roomService;
        this.employeeService = employeeService;
        this.patientService = patientService;
        this.appointmentRepository = appointmentRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        var allRooms = roomService.getAllInpatientRooms();
        var availableRooms = roomService.getAvailableRooms();
        var allAppointments = appointmentRepository.findAll();

        model.addAttribute("pageTitle", "Secretary Dashboard");
        model.addAttribute("totalRooms", allRooms.size());
        model.addAttribute("availableRooms", availableRooms.size());
        model.addAttribute("totalAppointments", allAppointments.size());
        model.addAttribute("totalAvailableBeds", roomService.getTotalAvailableBeds());
        return "secretary/dashboard";
    }

    // ========== ROOM MANAGEMENT (UC21) ==========

    @GetMapping("/rooms")
    public String viewAllRooms(Model model) {
        List<Room> rooms = roomService.getAllRooms();
        List<Patient> patients = patientService.findAllPatients();

        model.addAttribute("rooms", rooms);
        model.addAttribute("patients", patients);
        model.addAttribute("pageTitle", "All Rooms");
        return "secretary/rooms";
    }

    @GetMapping("/rooms/available")
    public String viewAvailableRooms(Model model) {
        List<InternationRoom> availableRooms = roomService.getAvailableRooms();
        List<Patient> patients = patientService.findAllPatients();

        model.addAttribute("rooms", availableRooms);
        model.addAttribute("patients", patients);
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
            roomService.admitPatient(patientId, roomId);
            redirectAttributes.addFlashAttribute("message",
                    "✅ Patient #" + patientId + " admitted successfully!");
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
            roomService.dismissPatient(patientId, roomId);
            redirectAttributes.addFlashAttribute("message",
                    "✅ Patient #" + patientId + " dismissed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Dismissal failed: " + e.getMessage());
        }
        return "redirect:/secretary/rooms";
    }

    // ========== APPOINTMENT MANAGEMENT (UC14, UC15, UC16) ==========

    @GetMapping("/appointments")
    public String manageAppointments(Model model) {
        List<Appointment> appointments = appointmentRepository.findAll();
        List<Patient> patients = patientService.findAllPatients();
        List<MedicalSpecialist> specialists = employeeService.findAllMedicalSpecialists();

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
            Patient patient = patientService.findById(patientId);
            MedicalSpecialist specialist = employeeService.findById(specialistId) instanceof MedicalSpecialist ms ? ms : null;

            if (specialist == null) {
                throw new RuntimeException("Medical specialist not found");
            }

            LocalDateTime appointmentTime = LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            // Create new appointment
            Appointment appointment = new Appointment(
                    0, // ID will be generated by repository
                    appointmentTime,
                    30, // default duration 30 minutes
                    "Booked by secretary"
            );
            appointment.setPatient(patient);
            appointment.setMedicalSpecialist(specialist);
            appointment.setPaymentStatus(false);

            Appointment saved = appointmentRepository.save(appointment);

            redirectAttributes.addFlashAttribute("message",
                    "✅ Appointment booked successfully!\nID: " + saved.getId());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Booking failed: " + e.getMessage());
        }
        return "redirect:/secretary/appointments";
    }

    @PostMapping("/appointments/{appointmentId}/cancel")
    public String cancelAppointment(@PathVariable int appointmentId,
                                    RedirectAttributes redirectAttributes) {
        try {
            Appointment appointment = appointmentRepository.findById(appointmentId);
            if (appointment != null) {
                appointment.setStatus("CANCELLED");
                appointmentRepository.save(appointment);
            }
            redirectAttributes.addFlashAttribute("message", "✅ Appointment cancelled successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Cancellation failed: " + e.getMessage());
        }
        return "redirect:/secretary/appointments";
    }

    @PostMapping("/appointments/{appointmentId}/reschedule")
    public String rescheduleAppointment(@PathVariable int appointmentId,
                                        @RequestParam String newDateTime,
                                        RedirectAttributes redirectAttributes) {
        try {
            Appointment appointment = appointmentRepository.findById(appointmentId);
            if (appointment == null) {
                throw new RuntimeException("Appointment not found");
            }

            LocalDateTime newTime = LocalDateTime.parse(newDateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            appointment.setDateTime(newTime);
            appointment.setStatus("RESCHEDULED");
            appointmentRepository.save(appointment);

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
        List<Patient> patients = patientService.searchPatients(query);
        model.addAttribute("patients", patients);
        model.addAttribute("query", query);
        model.addAttribute("pageTitle", "Search Results");
        return "secretary/patient-search-results";
    }
}