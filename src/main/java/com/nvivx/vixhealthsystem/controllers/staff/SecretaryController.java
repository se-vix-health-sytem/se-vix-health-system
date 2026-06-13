package com.nvivx.vixhealthsystem.controllers.staff;

import com.nvivx.vixhealthsystem.model.facility.Room;
import com.nvivx.vixhealthsystem.model.facility.InternationRoom;
import com.nvivx.vixhealthsystem.model.medical.Appointment;
import com.nvivx.vixhealthsystem.model.person.Patient;
import com.nvivx.vixhealthsystem.model.person.employee.Employee;
import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import com.nvivx.vixhealthsystem.model.person.employee.Secretary;
import com.nvivx.vixhealthsystem.model.staff.VacationRequest;
import com.nvivx.vixhealthsystem.repository.JsonAppointmentRepository;
import com.nvivx.vixhealthsystem.service.AuditService;
import com.nvivx.vixhealthsystem.service.core.EmployeeService;
import com.nvivx.vixhealthsystem.service.core.PatientService;
import com.nvivx.vixhealthsystem.service.medical.AppointmentService;
import com.nvivx.vixhealthsystem.service.resources.RoomService;
import com.nvivx.vixhealthsystem.service.scheduling.ShiftService;
import com.nvivx.vixhealthsystem.service.scheduling.VacationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

// HTML datetime-local sends "yyyy-MM-ddTHH:mm" (no seconds) — handle both


@Controller
@RequestMapping("/secretary")
public class SecretaryController {

    private static final DateTimeFormatter DT_FORM = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm[:ss]");

    private final RoomService roomService;
    private final EmployeeService employeeService;
    private final PatientService patientService;
    private final AppointmentService appointmentService;
    private final JsonAppointmentRepository appointmentRepository;
    private final AuditService auditService;
    private final ShiftService shiftService;
    private final VacationService vacationService;

    public SecretaryController(RoomService roomService,
                               EmployeeService employeeService,
                               PatientService patientService,
                               AppointmentService appointmentService,
                               JsonAppointmentRepository appointmentRepository,
                               AuditService auditService,
                               ShiftService shiftService,
                               VacationService vacationService) {
        this.roomService = roomService;
        this.employeeService = employeeService;
        this.patientService = patientService;
        this.appointmentService = appointmentService;
        this.appointmentRepository = appointmentRepository;
        this.auditService = auditService;
        this.shiftService = shiftService;
        this.vacationService = vacationService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        var allRooms = roomService.getAllInpatientRooms();
        var availableRooms = roomService.getAvailableRooms();
        var allAppointments = appointmentRepository.findAll();

        // Resolve department name within this request to avoid lazy-loading the detached session entity
        String departmentName = "No department assigned";
        Employee sessionUser = (Employee) session.getAttribute("user");
        if (sessionUser != null) {
            try {
                Employee freshUser = employeeService.findById(sessionUser.getId());
                if (freshUser != null && freshUser.getDepartment() != null) {
                    departmentName = freshUser.getDepartment().getName();
                }
            } catch (Exception ignored) {}
        }

        model.addAttribute("pageTitle", "Secretary Dashboard");
        model.addAttribute("currentPage", "dashboard");
        model.addAttribute("departmentName", departmentName);
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
        model.addAttribute("currentPage", "rooms");
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
        model.addAttribute("currentPage", "rooms");
        return "secretary/rooms";
    }

    // ========== PATIENT ADMISSION & DISMISSAL (UC22, UC23) ==========

    @PostMapping("/patients/admit")
    public String admitPatient(@RequestParam Long patientId,
                               @RequestParam Long roomId,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        try {
            Secretary secretary = getSecretaryFromSession(session);
            roomService.admitPatient(secretary, patientId, roomId);
            auditService.log("ADMIT_PATIENT", "Patient", String.valueOf(patientId),
                "Secretary admitted patient #" + patientId + " to room #" + roomId);
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
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        try {
            Secretary secretary = getSecretaryFromSession(session);
            roomService.dismissPatient(secretary, patientId, roomId);
            auditService.log("DISMISS_PATIENT", "Patient", String.valueOf(patientId),
                "Secretary dismissed patient #" + patientId + " from room #" + roomId);
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
        model.addAttribute("currentPage", "appointments");
        return "secretary/manage-appointments";
    }

    @PostMapping("/appointments/book-for-patient")
    public String bookAppointmentForPatient(@RequestParam Long patientId,
                                            @RequestParam Long specialistId,
                                            @RequestParam String dateTime,
                                            HttpSession session,
                                            RedirectAttributes redirectAttributes) {
        try {
            Secretary secretary = getSecretaryFromSession(session);
            Patient patient = patientService.findById(patientId);
            MedicalSpecialist specialist = employeeService.findById(specialistId) instanceof MedicalSpecialist ms ? ms : null;

            if (specialist == null) {
                throw new RuntimeException("Medical specialist not found");
            }

            LocalDateTime appointmentTime = LocalDateTime.parse(dateTime, DT_FORM);

            // Domain: secretary books appointment for patient via model methods
            Appointment saved = appointmentService.bookForSecretary(
                    secretary, patient, specialist, appointmentTime, 30, "Booked by secretary");

            auditService.log("BOOK_APPOINTMENT", "Appointment", String.valueOf(saved.getId()),
                "Secretary booked appointment for patient " + patient.getFiscalCode()
                + " with Dr. " + specialist.getName() + " " + specialist.getSurname()
                + " on " + appointmentTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

            redirectAttributes.addFlashAttribute("message",
                    "✅ Appointment booked successfully!\nID: " + saved.getId());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Booking failed: " + e.getMessage());
        }
        return "redirect:/secretary/appointments";
    }

    @PostMapping("/appointments/{appointmentId}/confirm")
    public String confirmAppointment(@PathVariable int appointmentId,
                                     RedirectAttributes redirectAttributes) {
        try {
            Appointment appointment = appointmentRepository.findById(appointmentId);
            if (appointment != null) {
                appointment.setStatus("CONFIRMED");
                appointmentRepository.save(appointment);
                String patientInfo = appointment.getPatient() != null ? appointment.getPatient().getFiscalCode() : "unknown";
                auditService.log("CONFIRM_APPOINTMENT", "Appointment", String.valueOf(appointmentId),
                    "Secretary confirmed appointment for patient " + patientInfo);
            }
            redirectAttributes.addFlashAttribute("message", "✅ Appointment confirmed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Confirmation failed: " + e.getMessage());
        }
        return "redirect:/secretary/appointments";
    }

    @PostMapping("/appointments/{appointmentId}/cancel")
    public String cancelAppointment(@PathVariable int appointmentId,
                                    RedirectAttributes redirectAttributes) {
        try {
            // Domain: appointment cancels itself via its own domain method
            Appointment appointment = appointmentService.cancelAppointment(appointmentId);
            String patientInfo = appointment.getPatient() != null ? appointment.getPatient().getFiscalCode() : "unknown";
            auditService.log("CANCEL_APPOINTMENT", "Appointment", String.valueOf(appointmentId),
                "Secretary cancelled appointment for patient " + patientInfo);
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
            LocalDateTime newTime = LocalDateTime.parse(newDateTime, DT_FORM);

            // Domain: appointment reschedules itself via its own domain method
            Appointment appointment = appointmentService.rescheduleAppointment(appointmentId, newTime);
            String patientInfo = appointment.getPatient() != null ? appointment.getPatient().getFiscalCode() : "unknown";
            auditService.log("RESCHEDULE_APPOINTMENT", "Appointment", String.valueOf(appointmentId),
                "Secretary rescheduled appointment for patient " + patientInfo
                + " to " + newTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

            redirectAttributes.addFlashAttribute("message", "✅ Appointment rescheduled successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Rescheduling failed: " + e.getMessage());
        }
        return "redirect:/secretary/appointments";
    }

    // ========== PERSONAL PROFILE ==========

    @GetMapping("/my-shifts")
    public String viewMyShifts(HttpSession session, Model model) {
        Employee user = (Employee) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        List<VacationRequest> vacations = vacationService.getApprovedRequestsForEmployee(user.getId().intValue());
        model.addAttribute("shifts", shiftService.getShiftsForEmployee(user.getId()));
        model.addAttribute("vacations", vacations);
        model.addAttribute("dashboardLink", "/secretary/dashboard");
        model.addAttribute("pageTitle", "My Shifts");
        model.addAttribute("currentPage", "myShifts");
        return "employee/my-shifts";
    }

    @GetMapping("/profile")
    public String viewProfile(HttpSession session, Model model) {
        Employee sessionUser = (Employee) session.getAttribute("user");
        if (sessionUser == null) {
            return "redirect:/login";
        }
        try {
            Employee fresh = employeeService.findById(sessionUser.getId());
            model.addAttribute("employee", fresh);
            model.addAttribute("pageTitle", "My Profile");
            model.addAttribute("currentPage", "profile");
            model.addAttribute("roleLabel", "Secretary");
            model.addAttribute("dashboardLink", "/secretary/dashboard");
            model.addAttribute("isSpecialist", false);
        } catch (Exception e) {
            model.addAttribute("employee", sessionUser);
            model.addAttribute("currentPage", "profile");
        }
        return "employee/profile";
    }

    // ========== PATIENT SEARCH ==========

    @GetMapping("/patients/search")
    public String showPatientSearchForm(Model model) {
        model.addAttribute("pageTitle", "Search Patients");
        model.addAttribute("currentPage", "patients");
        return "secretary/patient-search";
    }

    @GetMapping("/patients/{id}")
    public String viewPatientProfile(@PathVariable Long id, Model model) {
        try {
            Patient patient = patientService.findById(id);
            model.addAttribute("patient", patient);
            model.addAttribute("pageTitle", "Patient Profile");
            model.addAttribute("currentPage", "patients");
        } catch (Exception e) {
            return "redirect:/secretary/patients/search";
        }
        return "secretary/patient-profile";
    }

    @PostMapping("/patients/search")
    public String searchPatients(@RequestParam String query, Model model) {
        List<Patient> patients = patientService.searchPatients(query);
        model.addAttribute("patients", patients);
        model.addAttribute("query", query);
        model.addAttribute("pageTitle", "Search Results");
        model.addAttribute("currentPage", "patients");
        return "secretary/patient-search-results";
    }

    // ========== HELPERS ==========

    private Secretary getSecretaryFromSession(HttpSession session) {
        Employee user = (Employee) session.getAttribute("user");
        if (user instanceof Secretary secretary) {
            // Reload from DB to get a fully initialized entity
            Employee fresh = employeeService.findById(secretary.getId());
            if (fresh instanceof Secretary s) {
                return s;
            }
        }
        throw new RuntimeException("Authenticated user is not a secretary");
    }
}