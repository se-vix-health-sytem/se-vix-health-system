package com.nvivx.vixhealthsystem.controllers;

import com.nvivx.vixhealthsystem.model.person.Patient;
import com.nvivx.vixhealthsystem.model.staff.Shift;
import com.nvivx.vixhealthsystem.service.medical.MedicalRecordService;
import com.nvivx.vixhealthsystem.service.core.PatientService;
import com.nvivx.vixhealthsystem.service.scheduling.ShiftService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/medical-specialist")
public class MedicalSpecialistController {

    private final MedicalRecordService medicalRecordService;
    private final PatientService patientService;
    private final ShiftService shiftService;

    public MedicalSpecialistController(MedicalRecordService medicalRecordService,
                                       PatientService patientService,
                                       ShiftService shiftService) {
        this.medicalRecordService = medicalRecordService;
        this.patientService = patientService;
        this.shiftService = shiftService;
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
        model.addAttribute("pageTitle", "Medical Record - " + patient.getName() + " " + patient.getSurname());
        model.addAttribute("currentPage", "patients");
        model.addAttribute("patient", patient);
        model.addAttribute("medicalRecord", patient.getMedicalRecord());
        model.addAttribute("patientId", patientId);
        return "medical-specialist/medical-record";
    }

    @PostMapping("/patients/{patientId}/add-diagnosis")
    public String addDiagnosis(@PathVariable Long patientId,
                               @RequestParam String diagnosisName,
                               @RequestParam String description,
                               @RequestParam String severity,
                               Model model) {
        try {
            medicalRecordService.addDiagnosis(patientId, diagnosisName, description, severity);
            model.addAttribute("pageTitle", "Diagnosis Added");
            model.addAttribute("message", "✅ Diagnosis added successfully!");
        } catch (Exception e) {
            model.addAttribute("pageTitle", "Error");
            model.addAttribute("message", "❌ Error: " + e.getMessage());
        }
        model.addAttribute("currentPage", "patients");
        return "medical-specialist/result";
    }

    @PostMapping("/patients/{patientId}/add-prescription")
    public String addPrescription(@PathVariable Long patientId,
                                  @RequestParam String medication,
                                  @RequestParam String dosage,
                                  HttpSession session,
                                  Model model) {
        try {
            com.nvivx.vixhealthsystem.model.person.employee.Employee user =
                    (com.nvivx.vixhealthsystem.model.person.employee.Employee) session.getAttribute("user");
            Long specialistId = (user != null) ? user.getId() : 1L;
            String fullMedication = medication + " - " + dosage;
            medicalRecordService.addPrescription(patientId, specialistId, fullMedication);
            model.addAttribute("pageTitle", "Prescription Added");
            model.addAttribute("message", "✅ Prescription added successfully!");
        } catch (Exception e) {
            model.addAttribute("pageTitle", "Error");
            model.addAttribute("message", "❌ Error: " + e.getMessage());
        }
        model.addAttribute("currentPage", "patients");
        return "medical-specialist/result";
    }

    @PostMapping("/patients/{patientId}/add-exam-result")
    public String addExamResult(@PathVariable Long patientId,
                                @RequestParam String examType,
                                @RequestParam String result,
                                @RequestParam(required = false) String notes,
                                Model model) {
        try {
            medicalRecordService.addExamResult(patientId, examType, result, notes);
            model.addAttribute("pageTitle", "Exam Result Added");
            model.addAttribute("message", "✅ Exam result added successfully!");
        } catch (Exception e) {
            model.addAttribute("pageTitle", "Error");
            model.addAttribute("message", "❌ Error: " + e.getMessage());
        }
        model.addAttribute("currentPage", "patients");
        return "medical-specialist/result";
    }

    @GetMapping("/appointments")
    public String viewAppointments(Model model) {
        model.addAttribute("pageTitle", "My Appointments");
        model.addAttribute("currentPage", "appointments");
        return "medical-specialist/appointments";
    }

    @GetMapping("/calendar")
    public String viewCalendar(HttpSession session, Model model) {
        com.nvivx.vixhealthsystem.model.person.employee.Employee user =
                (com.nvivx.vixhealthsystem.model.person.employee.Employee) session.getAttribute("user");
        if (user != null) {
            LocalDate today = LocalDate.now();
            List<Shift> upcoming = shiftService.getShiftsForEmployee(user.getId()).stream()
                    .filter(s -> s.getDate() != null && !s.getDate().isBefore(today))
                    .sorted(java.util.Comparator.comparing(Shift::getDate))
                    .collect(Collectors.toList());
            // Group by "MMMM yyyy" label for section headers
            Map<String, List<Shift>> shiftsByMonth = upcoming.stream()
                    .collect(Collectors.groupingBy(
                            s -> s.getDate().getMonth().getDisplayName(
                                    java.time.format.TextStyle.FULL, java.util.Locale.ENGLISH)
                                    + " " + s.getDate().getYear(),
                            LinkedHashMap::new,
                            Collectors.toList()));
            model.addAttribute("shiftsByMonth", shiftsByMonth);
        }
        model.addAttribute("pageTitle", "My Calendar");
        model.addAttribute("currentPage", "calendar");
        return "medical-specialist/calendar";
    }

    @GetMapping("/my-schedule")
    public String viewMySchedule(HttpSession session, Model model) {
        com.nvivx.vixhealthsystem.model.person.employee.Employee user =
                (com.nvivx.vixhealthsystem.model.person.employee.Employee) session.getAttribute("user");
        if (user != null) {
            List<Shift> shifts = shiftService.getShiftsForEmployee(user.getId());
            model.addAttribute("shifts", shifts);
        }
        model.addAttribute("pageTitle", "My Schedule");
        model.addAttribute("currentPage", "schedule");
        return "medical-specialist/schedule";
    }
}
