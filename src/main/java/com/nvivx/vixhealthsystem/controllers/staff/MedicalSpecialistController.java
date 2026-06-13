package com.nvivx.vixhealthsystem.controllers.staff;

import com.nvivx.vixhealthsystem.model.facility.Room;
import com.nvivx.vixhealthsystem.model.facility.SpecializedRoom;
import com.nvivx.vixhealthsystem.model.medical.Appointment;
import com.nvivx.vixhealthsystem.model.medical.Surgery;
import com.nvivx.vixhealthsystem.model.person.Patient;
import com.nvivx.vixhealthsystem.model.person.employee.Employee;
import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import com.nvivx.vixhealthsystem.model.staff.Shift;
import com.nvivx.vixhealthsystem.repository.JsonAppointmentRepository;
import com.nvivx.vixhealthsystem.repository.RoomRepository;
import com.nvivx.vixhealthsystem.repository.SurgeryRepository;
import com.nvivx.vixhealthsystem.service.AuditService;
import com.nvivx.vixhealthsystem.service.core.EmployeeService;
import com.nvivx.vixhealthsystem.service.medical.MedicalRecordService;
import com.nvivx.vixhealthsystem.service.core.PatientService;
import com.nvivx.vixhealthsystem.service.scheduling.ShiftService;
import com.nvivx.vixhealthsystem.service.scheduling.VacationService;
import com.nvivx.vixhealthsystem.model.staff.VacationRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/medical-specialist")
public class MedicalSpecialistController {

    private final MedicalRecordService medicalRecordService;
    private final PatientService patientService;
    private final ShiftService shiftService;
    private final VacationService vacationService;
    private final SurgeryRepository surgeryRepository;
    private final RoomRepository roomRepository;
    private final JsonAppointmentRepository appointmentRepository;
    private final AuditService auditService;
    private final EmployeeService employeeService;

    public MedicalSpecialistController(MedicalRecordService medicalRecordService,
                                       PatientService patientService,
                                       ShiftService shiftService,
                                       VacationService vacationService,
                                       SurgeryRepository surgeryRepository,
                                       RoomRepository roomRepository,
                                       JsonAppointmentRepository appointmentRepository,
                                       AuditService auditService,
                                       EmployeeService employeeService) {
        this.medicalRecordService = medicalRecordService;
        this.patientService = patientService;
        this.shiftService = shiftService;
        this.vacationService = vacationService;
        this.surgeryRepository = surgeryRepository;
        this.roomRepository = roomRepository;
        this.appointmentRepository = appointmentRepository;
        this.auditService = auditService;
        this.employeeService = employeeService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "Medical Specialist Dashboard");
        model.addAttribute("currentPage", "dashboard");
        return "medical-specialist/dashboard";
    }

    @GetMapping("/patients/search")
    public String searchPatientsForm(Model model) {
        model.addAttribute("pageTitle", "Search Patients");
        model.addAttribute("currentPage", "patients");
        return "medical-specialist/patient-search";
    }

    @PostMapping("/patients/search")
    public String searchPatients(@RequestParam String query, Model model) {
        var patients = patientService.searchPatients(query);
        model.addAttribute("pageTitle", "Search Results");
        model.addAttribute("currentPage", "patients");
        model.addAttribute("patients", patients);
        model.addAttribute("query", query);
        return "medical-specialist/search-results";
    }

    @GetMapping("/patients/{patientId}/record")
    public String viewMedicalRecord(@PathVariable Long patientId, Model model) {
        Patient patient = medicalRecordService.getPatientWithMedicalRecord(patientId);
        List<SpecializedRoom> specializedRooms = roomRepository.findAll().stream()
            .filter(r -> r instanceof SpecializedRoom)
            .map(r -> (SpecializedRoom) r)
            .toList();
        model.addAttribute("pageTitle", "Medical Record - " + patient.getName() + " " + patient.getSurname());
        model.addAttribute("currentPage", "patients");
        model.addAttribute("patient", patient);
        model.addAttribute("medicalRecord", patient.getMedicalRecord());
        model.addAttribute("patientId", patientId);
        model.addAttribute("specializedRooms", specializedRooms);
        return "medical-specialist/medical-record";
    }

    // datetime-local inputs send "yyyy-MM-ddTHH:mm" (no seconds); handle both formats
    private static final DateTimeFormatter DT_FORM = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm[:ss]");

    @PostMapping("/patients/{patientId}/add-surgery")
    public String addSurgery(@PathVariable Long patientId,
                             @RequestParam String surgeryName,
                             @RequestParam(required = false) String surgeryDescription,
                             @RequestParam String surgeryDateTime,
                             @RequestParam(required = false) Long roomId,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        try {
            Patient patient = medicalRecordService.getPatientWithMedicalRecord(patientId);
            if (patient.getMedicalRecord() == null) {
                throw new RuntimeException("Patient has no medical record");
            }

            SpecializedRoom specializedRoom = null;
            if (roomId != null) {
                Room roomEntity = roomRepository.findById(roomId)
                        .orElseThrow(() -> new RuntimeException("Room not found: " + roomId));
                if (!(roomEntity instanceof SpecializedRoom)) {
                    throw new RuntimeException("Selected room is not a specialized operating room");
                }
                specializedRoom = (SpecializedRoom) roomEntity;
            }

            Employee user = (Employee) session.getAttribute("user");
            MedicalSpecialist specialist = null;
            if (user != null) {
                Employee fresh = employeeService.findById(user.getId());
                if (fresh instanceof MedicalSpecialist ms) specialist = ms;
            }

            Surgery surgery = new Surgery();
            surgery.setName(surgeryName);
            surgery.setDescription(surgeryDescription != null ? surgeryDescription : "");
            surgery.setDateTime(LocalDateTime.parse(surgeryDateTime, DT_FORM));
            surgery.setSpecializedRoom(specializedRoom);

            if (specialist != null) {
                specialist.scheduleSurgeryForPatient(patient, surgery);
            } else {
                surgery.setMedicalRecord(patient.getMedicalRecord());
            }

            surgeryRepository.save(surgery);
            auditService.log("ADD_SURGERY", "MedicalRecord",
                    String.valueOf(patient.getMedicalRecord().getId()),
                    "Scheduled surgery '" + surgeryName + "' for patient " + patientId);
            redirectAttributes.addFlashAttribute("successMessage", "✅ Surgery scheduled successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Error scheduling surgery: " + e.getMessage());
        }
        return "redirect:/medical-specialist/patients/" + patientId + "/record";
    }

    @PostMapping("/patients/{patientId}/add-diagnosis")
    public String addDiagnosis(@PathVariable Long patientId,
                               @RequestParam String diagnosisName,
                               @RequestParam(required = false) String description,
                               @RequestParam String severity,
                               RedirectAttributes redirectAttributes) {
        try {
            String desc = (description != null && !description.isEmpty()) ? description : "No description provided";
            medicalRecordService.addDiagnosis(patientId, diagnosisName, desc, severity);
            redirectAttributes.addFlashAttribute("successMessage", "✅ Diagnosis added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Error: " + e.getMessage());
        }
        return "redirect:/medical-specialist/patients/" + patientId + "/record";
    }

    @PostMapping("/patients/{patientId}/add-prescription")
    public String addPrescription(@PathVariable Long patientId,
                                  @RequestParam String medication,
                                  @RequestParam String dosage,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        try {
            Employee user = (Employee) session.getAttribute("user");
            if (user == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Not logged in.");
                return "redirect:/login";
            }

            Long specialistId = user.getId();
            String fullMedication = medication + " - " + dosage;

            medicalRecordService.addPrescription(patientId, specialistId, fullMedication);
            redirectAttributes.addFlashAttribute("successMessage", "✅ Prescription added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Error: " + e.getMessage());
        }
        return "redirect:/medical-specialist/patients/" + patientId + "/record";
    }

    @PostMapping("/patients/{patientId}/add-exam-result")
    public String addExamResult(@PathVariable Long patientId,
                                @RequestParam String examType,
                                @RequestParam String result,
                                @RequestParam(required = false) String notes,
                                RedirectAttributes redirectAttributes) {
        try {
            medicalRecordService.addExamResult(patientId, examType, result, notes);
            redirectAttributes.addFlashAttribute("successMessage", "✅ Exam result added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Error: " + e.getMessage());
        }
        return "redirect:/medical-specialist/patients/" + patientId + "/record";
    }

    @GetMapping("/appointments")
    public String viewAppointments(HttpSession session, Model model) {
        Employee user = (Employee) session.getAttribute("user");
        List<Appointment> myAppointments = new java.util.ArrayList<>();
        if (user != null) {
            try {
                myAppointments = appointmentRepository.findAll().stream()
                    .filter(a -> a.getMedicalSpecialist() != null
                             && a.getMedicalSpecialist().getId() != null
                             && a.getMedicalSpecialist().getId().equals(user.getId()))
                    .sorted(java.util.Comparator.comparing(
                        a -> a.getDateTime() != null ? a.getDateTime() : java.time.LocalDateTime.MAX))
                    .collect(Collectors.toList());
            } catch (Exception ignored) {}
        }
        model.addAttribute("appointments", myAppointments);
        model.addAttribute("pageTitle", "My Appointments");
        model.addAttribute("currentPage", "appointments");
        return "medical-specialist/appointments";
    }

    @GetMapping("/calendar")
    public String viewCalendar(@RequestParam(required = false) String month,
                               HttpSession session, Model model) {
        Employee user = (Employee) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        YearMonth ymTemp;
        try {
            ymTemp = (month != null && !month.isBlank()) ? YearMonth.parse(month) : YearMonth.now();
        } catch (Exception e) {
            ymTemp = YearMonth.now();
        }
        final YearMonth ym = ymTemp;

        YearMonth prev = ym.minusMonths(1);
        YearMonth next = ym.plusMonths(1);

        // Collect event dates for the month
        Set<LocalDate> shiftDates = shiftService.getShiftsForEmployee(user.getId()).stream()
                .filter(s -> s.getDate() != null && YearMonth.from(s.getDate()).equals(ym))
                .map(Shift::getDate).collect(Collectors.toSet());

        Set<LocalDate> apptDates = appointmentRepository.findAll().stream()
                .filter(a -> a.getMedicalSpecialist() != null
                        && a.getMedicalSpecialist().getId().equals(user.getId())
                        && !"CANCELLED".equals(a.getStatus())
                        && a.getDateTime() != null
                        && YearMonth.from(a.getDateTime().toLocalDate()).equals(ym))
                .map(a -> a.getDateTime().toLocalDate()).collect(Collectors.toSet());

        Set<LocalDate> surgeryDates = surgeryRepository.findByMedicalSpecialistId(user.getId()).stream()
                .filter(s -> s.getDateTime() != null
                        && YearMonth.from(s.getDateTime().toLocalDate()).equals(ym))
                .map(s -> s.getDateTime().toLocalDate()).collect(Collectors.toSet());

        List<VacationRequest> vacations = vacationService.getApprovedRequestsForEmployee(user.getId().intValue());

        // Pass date sets as simple String lists — JavaScript builds the grid
        List<String> shiftDatesList   = shiftDates.stream().map(LocalDate::toString).collect(Collectors.toList());
        List<String> apptDatesList    = apptDates.stream().map(LocalDate::toString).collect(Collectors.toList());
        List<String> surgeryDatesList = surgeryDates.stream().map(LocalDate::toString).collect(Collectors.toList());
        List<Map<String, String>> vacRanges = vacations.stream()
                .filter(v -> v.getStartDate() != null && v.getEndDate() != null)
                .map(v -> {
                    Map<String, String> m = new HashMap<>();
                    m.put("start", v.getStartDate().toString());
                    m.put("end",   v.getEndDate().toString());
                    return m;
                }).collect(Collectors.toList());

        model.addAttribute("calYear",       ym.getYear());
        model.addAttribute("calMonth",      ym.getMonthValue());
        model.addAttribute("monthLabel",    ym.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + ym.getYear());
        model.addAttribute("prevMonth",     prev.toString());
        model.addAttribute("nextMonth",     next.toString());
        model.addAttribute("shiftDates",    shiftDatesList);
        model.addAttribute("apptDates",     apptDatesList);
        model.addAttribute("surgeryDates",  surgeryDatesList);
        model.addAttribute("vacRanges",     vacRanges);
        model.addAttribute("pageTitle", "My Calendar");
        model.addAttribute("currentPage", "calendar");
        return "medical-specialist/calendar";
    }

    @GetMapping("/my-schedule")
    public String viewMySchedule(@RequestParam(required = false) String date,
                                 HttpSession session, Model model) {
        Employee user = (Employee) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        LocalDate sdTemp;
        try {
            sdTemp = (date != null && !date.isBlank()) ? LocalDate.parse(date) : LocalDate.now();
        } catch (Exception e) {
            sdTemp = LocalDate.now();
        }
        final LocalDate selectedDate = sdTemp;

        LocalDate prev = selectedDate.minusDays(1);
        LocalDate next = selectedDate.plusDays(1);

        List<Shift> todayShifts = shiftService.getShiftsForEmployee(user.getId()).stream()
                .filter(s -> s.getDate() != null && s.getDate().equals(selectedDate))
                .collect(Collectors.toList());

        List<Appointment> todayAppts = appointmentRepository.findAll().stream()
                .filter(a -> a.getMedicalSpecialist() != null
                        && a.getMedicalSpecialist().getId().equals(user.getId())
                        && !"CANCELLED".equals(a.getStatus())
                        && a.getDateTime() != null
                        && a.getDateTime().toLocalDate().equals(selectedDate))
                .sorted(java.util.Comparator.comparing(Appointment::getDateTime))
                .collect(Collectors.toList());

        List<Surgery> todaySurgeries = surgeryRepository.findByMedicalSpecialistId(user.getId()).stream()
                .filter(s -> s.getDateTime() != null && s.getDateTime().toLocalDate().equals(selectedDate))
                .sorted(java.util.Comparator.comparing(Surgery::getDateTime))
                .collect(Collectors.toList());

        List<VacationRequest> vacations = vacationService.getApprovedRequestsForEmployee(user.getId().intValue());
        boolean onVacation = vacations.stream().anyMatch(v ->
                v.getStartDate() != null && v.getEndDate() != null
                && !selectedDate.isBefore(v.getStartDate()) && !selectedDate.isAfter(v.getEndDate()));

        model.addAttribute("selectedDate", selectedDate);
        model.addAttribute("selectedDateLabel", selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                + ", " + selectedDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));
        model.addAttribute("prevDate", prev.toString());
        model.addAttribute("nextDate", next.toString());
        model.addAttribute("todayShifts", todayShifts);
        model.addAttribute("todayAppts", todayAppts);
        model.addAttribute("todaySurgeries", todaySurgeries);
        model.addAttribute("onVacation", onVacation);
        model.addAttribute("pageTitle", "My Schedule");
        model.addAttribute("currentPage", "schedule");
        return "medical-specialist/schedule";
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
            model.addAttribute("roleLabel", "Medical Specialist");
            model.addAttribute("dashboardLink", "/medical-specialist/dashboard");
            model.addAttribute("isSpecialist", true);
        } catch (Exception e) {
            model.addAttribute("employee", sessionUser);
            model.addAttribute("currentPage", "profile");
        }
        return "employee/profile";
    }

    @GetMapping("/surgeries")
    public String viewSurgeries(HttpSession session, Model model) {
        Employee user = (Employee) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        List<Surgery> surgeries = surgeryRepository.findByMedicalSpecialistId(user.getId());
        model.addAttribute("surgeries", surgeries != null ? surgeries : new ArrayList<>());
        model.addAttribute("pageTitle", "My Surgeries");
        model.addAttribute("currentPage", "surgeries");
        return "medical-specialist/surgeries";
    }
}
