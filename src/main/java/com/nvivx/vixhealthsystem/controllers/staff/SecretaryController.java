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

// HTML datetime-local sends "yyyy-MM-ddTHH:mm" (no seconds) : handle both

/**
 * @brief Controller for Secretary staff members : base URL {@code /secretary}.
 *
 * Secretaries handle the administrative side of patient flow and appointment
 * management.  This controller covers room management (UC21), patient admission
 * and dismissal (UC22, UC23), appointment booking/confirmation/cancellation/
 * rescheduling (UC14, UC15, UC16), patient search, and the secretary's own
 * shift and profile pages.
 *
 * Only accessible to users with {@code ROLE_SECRETARY}.
 *
 * @see RoomService
 * @see AppointmentService
 * @see PatientService
 * @see AuditService
 */
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

    // =========================================================
    // DASHBOARD
    // =========================================================

    /**
     * GET /secretary/dashboard : render the secretary's overview dashboard.
     *
     * Resolves the secretary's department name by reloading a fresh entity from
     * the database (avoiding lazy-loading failures on the detached session entity).
     * Populates room and appointment summary counts.
     *
     * @param session  HTTP session carrying the {@code "user"} Employee attribute,
     *                 used to look up the secretary's assigned department name.
     * @param model    Receives {@code departmentName}, {@code totalRooms},
     *                 {@code availableRooms}, {@code totalAppointments},
     *                 and {@code totalAvailableBeds} attributes.
     * @return         Thymeleaf template {@code secretary/dashboard}.
     */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        int totalRooms = 0, availableRoomsCount = 0, totalAppointments = 0, totalBeds = 0;
        try { totalRooms = roomService.getAllInpatientRooms().size(); } catch (Exception ignored) {}
        try { availableRoomsCount = roomService.getAvailableRooms().size(); } catch (Exception ignored) {}
        try { totalAppointments = appointmentRepository.findAll().size(); } catch (Exception ignored) {}
        try { totalBeds = roomService.getTotalAvailableBeds(); } catch (Exception ignored) {}

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
        model.addAttribute("totalRooms", totalRooms);
        model.addAttribute("availableRooms", availableRoomsCount);
        model.addAttribute("totalAppointments", totalAppointments);
        model.addAttribute("totalAvailableBeds", totalBeds);
        return "secretary/dashboard";
    }

    // =========================================================
    // ROOM MANAGEMENT (UC21)
    // =========================================================

    /**
     * GET /secretary/rooms : list all inpatient rooms with their current occupancy.
     *
     * @param model  Receives {@code rooms} and {@code patients} (for the admission form selects).
     * @return       Thymeleaf template {@code secretary/rooms}.
     */
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

    /**
     * GET /secretary/rooms/available : list only rooms with free beds.
     *
     * @param model  Receives {@code rooms} (available InternationRooms only),
     *               {@code patients}, and {@code isAvailableView=true}.
     * @return       Thymeleaf template {@code secretary/rooms}.
     */
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

    // =========================================================
    // PATIENT ADMISSION & DISMISSAL (UC22, UC23)
    // =========================================================

    /**
     * POST /secretary/patients/admit : admit a patient to a specific room.
     *
     * Delegates to {@link RoomService#admitPatient} via the Secretary domain object
     * (resolved from session) and writes an audit entry.
     *
     * @param patientId           Database ID of the patient to admit.
     * @param roomId              Database ID of the target inpatient room.
     * @param session             HTTP session; used to resolve the Secretary domain object
     *                            so the business rule check inside the model runs correctly.
     * @param redirectAttributes  Flash attributes for the redirect.
     * @return                    Redirect to {@code /secretary/rooms}.
     */
    @PostMapping("/patients/admit")
    public String admitPatient(@RequestParam Long patientId,
                               @RequestParam Long roomId,
                               @RequestParam(required = false) Boolean confirmTransfer,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        try {
            Secretary secretary = getSecretaryFromSession(session);

            // Check if the patient is already assigned to a room
            InternationRoom currentRoom = roomService.findPatientRoom(patientId);

            if (currentRoom != null && !Long.valueOf(currentRoom.getId()).equals(roomId)) {
                if (!Boolean.TRUE.equals(confirmTransfer)) {
                    // Ask for confirmation before transferring
                    Patient patient = patientService.findById(patientId);
                    Room targetRoom = roomService.findById(roomId);
                    String targetNum = targetRoom.getNumber();
                    redirectAttributes.addFlashAttribute("transferWarning",
                            "Patient " + patient.getName() + " " + patient.getSurname()
                            + " is currently assigned to Room " + currentRoom.getNumber()
                            + ". Do you want to confirm transfer to Room " + targetNum + "?");
                    redirectAttributes.addFlashAttribute("pendingPatientId", patientId);
                    redirectAttributes.addFlashAttribute("pendingRoomId", roomId);
                    return "redirect:/secretary/rooms";
                }
                // Confirmed : dismiss from current room first, then admit to new room
                roomService.dismissPatient(secretary, patientId, currentRoom.getId());
                auditService.log("TRANSFER_PATIENT", "Patient", String.valueOf(patientId),
                    "Patient #" + patientId + " transferred from room #" + currentRoom.getId() + " to room #" + roomId);
            }

            roomService.admitPatient(secretary, patientId, roomId);
            auditService.log("ADMIT_PATIENT", "Patient", String.valueOf(patientId),
                "Secretary admitted patient #" + patientId + " to room #" + roomId);
            redirectAttributes.addFlashAttribute("message",
                    "✅ Patient admitted to Room " + roomId + " successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Admission failed: " + e.getMessage());
        }
        return "redirect:/secretary/rooms";
    }

    /**
     * POST /secretary/patients/dismiss : discharge a patient from their room.
     *
     * @param patientId           Database ID of the patient to discharge.
     * @param roomId              Database ID of the room the patient is currently in.
     * @param session             HTTP session; used to resolve the Secretary domain object.
     * @param redirectAttributes  Flash attributes for the redirect.
     * @return                    Redirect to {@code /secretary/rooms}.
     */
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

    // =========================================================
    // APPOINTMENT MANAGEMENT (UC14, UC15, UC16)
    // =========================================================

    /**
     * GET /secretary/appointments : display all appointments with management controls.
     *
     * @param model  Receives {@code appointments}, {@code patients}, and {@code specialists}
     *               for the booking form dropdowns.
     * @return       Thymeleaf template {@code secretary/manage-appointments}.
     */
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

    /**
     * POST /secretary/appointments/book-for-patient : book an appointment on behalf of a patient.
     *
     * @param patientId           Database ID of the patient.
     * @param specialistId        Database ID of the target MedicalSpecialist.
     * @param dateTime            Appointment datetime string ({@code yyyy-MM-ddTHH:mm}).
     * @param session             HTTP session; used to resolve the Secretary domain object
     *                            required by {@link AppointmentService#bookForSecretary}.
     * @param redirectAttributes  Flash attributes for the redirect.
     * @return                    Redirect to {@code /secretary/appointments}.
     */
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

    /**
     * POST /secretary/appointments/{appointmentId}/cancel : cancel an appointment via the domain method.
     *
     * @param appointmentId       Numeric ID of the appointment to cancel.
     * @param redirectAttributes  Flash attributes for the redirect.
     * @return                    Redirect to {@code /secretary/appointments}.
     */
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

    /**
     * POST /secretary/appointments/{appointmentId}/reschedule : move an appointment to a new date/time.
     *
     * @param appointmentId       Numeric ID of the appointment to reschedule.
     * @param newDateTime         New datetime string ({@code yyyy-MM-ddTHH:mm}).
     * @param redirectAttributes  Flash attributes for the redirect.
     * @return                    Redirect to {@code /secretary/appointments}.
     */
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

    // =========================================================
    // PERSONAL PROFILE
    // =========================================================

    /**
     * GET /secretary/my-shifts : display the secretary's assigned shifts and approved vacations.
     *
     * @param session  HTTP session carrying the {@code "user"} Employee attribute.
     * @param model    Receives {@code shifts}, {@code vacations}, and {@code dashboardLink}.
     * @return         Thymeleaf template {@code employee/my-shifts}, or
     *                 {@code redirect:/login} when no session user is found.
     */
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

    /**
     * GET /secretary/profile : display the secretary's personal profile page.
     *
     * @param session  HTTP session carrying the {@code "user"} Employee attribute.
     * @param model    Receives {@code employee}, {@code roleLabel}, and {@code dashboardLink}.
     * @return         Thymeleaf template {@code employee/profile}, or
     *                 {@code redirect:/login} when no session user is found.
     */
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

    // =========================================================
    // PATIENT SEARCH
    // =========================================================

    /**
     * GET /secretary/patients/search : render the patient search form.
     *
     * @param model  Receives {@code pageTitle} and {@code currentPage} attributes.
     * @return       Thymeleaf template {@code secretary/patient-search}.
     */
    @GetMapping("/patients/search")
    public String showPatientSearchForm(Model model) {
        model.addAttribute("pageTitle", "Search Patients");
        model.addAttribute("currentPage", "patients");
        return "secretary/patient-search";
    }

    /**
     * GET /secretary/patients/{id} : display a patient's basic profile.
     *
     * @param id     Database ID of the patient.
     * @param model  Receives {@code patient} attributes.
     * @return       Thymeleaf template {@code secretary/patient-profile}, or
     *               redirect to {@code /secretary/patients/search} when the patient is not found.
     */
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

    /**
     * POST /secretary/patients/search : execute a patient search and display results.
     *
     * @param query  Free-text search term (name, fiscal code, etc.).
     * @param model  Receives {@code patients} list and echoes {@code query}.
     * @return       Thymeleaf template {@code secretary/patient-search-results}.
     */
    @PostMapping("/patients/search")
    public String searchPatients(@RequestParam String query, Model model) {
        List<Patient> patients = patientService.searchPatients(query);
        model.addAttribute("patients", patients);
        model.addAttribute("query", query);
        model.addAttribute("pageTitle", "Search Results");
        model.addAttribute("currentPage", "patients");
        return "secretary/patient-search-results";
    }

    // =========================================================
    // HELPERS
    // =========================================================

    /**
     * Resolve and reload the authenticated Secretary from the HTTP session.
     *
     * The session {@code "user"} attribute is used because the Spring Security
     * principal carries only the role string; the full Secretary domain object is
     * needed by room and appointment service methods that enforce business rules.
     * A fresh entity is reloaded from the database to avoid Hibernate detached-
     * object issues.
     *
     * @param session  HTTP session carrying the {@code "user"} attribute.
     * @return         A fully initialised {@link Secretary} from the database.
     * @throws RuntimeException  When the session user is absent or not a Secretary.
     */
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