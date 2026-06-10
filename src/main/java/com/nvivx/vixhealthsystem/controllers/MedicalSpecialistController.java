package com.nvivx.vixhealthsystem.controllers;

import com.nvivx.vixhealthsystem.model.person.Patient;
import com.nvivx.vixhealthsystem.service.medical.MedicalRecordService;
import com.nvivx.vixhealthsystem.service.core.PatientService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/medical-specialist")
public class MedicalSpecialistController {

    private final MedicalRecordService medicalRecordService;
    private final PatientService patientService;

    public MedicalSpecialistController(MedicalRecordService medicalRecordService,
                                       PatientService patientService) {
        this.medicalRecordService = medicalRecordService;
        this.patientService = patientService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "Medical Specialist Dashboard");
        return "medical-specialist/dashboard";
    }

    @GetMapping("/patients/search")
    public String searchPatientsForm(Model model) {
        model.addAttribute("pageTitle", "Search Patients");
        return "medical-specialist/patient-search";
    }

    @PostMapping("/patients/search")
    public String searchPatients(@RequestParam String query, Model model) {
        var patients = patientService.searchPatients(query);
        model.addAttribute("pageTitle", "Search Results");
        model.addAttribute("patients", patients);
        model.addAttribute("query", query);
        return "medical-specialist/search-results";
    }

    @GetMapping("/patients/{patientId}/record")
    public String viewMedicalRecord(@PathVariable Long patientId, Model model) {
        Patient patient = medicalRecordService.getPatientWithMedicalRecord(patientId);
        model.addAttribute("pageTitle", "Medical Record - " + patient.getName() + " " + patient.getSurname());
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
        return "medical-specialist/result";
    }

    @PostMapping("/patients/{patientId}/add-prescription")
    public String addPrescription(@PathVariable Long patientId,
                                  @RequestParam String medication,
                                  @RequestParam String dosage,
                                  Model model) {
        try {
            // TODO: Get current medical specialist ID from authentication
            Long currentSpecialistId = 1L; // Placeholder until auth is implemented
            String fullMedication = medication + " - " + dosage;
            medicalRecordService.addPrescription(patientId, currentSpecialistId, fullMedication);
            model.addAttribute("pageTitle", "Prescription Added");
            model.addAttribute("message", "✅ Prescription added successfully!");
        } catch (Exception e) {
            model.addAttribute("pageTitle", "Error");
            model.addAttribute("message", "❌ Error: " + e.getMessage());
        }
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
        return "medical-specialist/result";
    }

    @GetMapping("/appointments")
    public String viewAppointments(Model model) {
        model.addAttribute("pageTitle", "My Appointments");
        // TODO: Implement appointment listing from JSON repository
        return "medical-specialist/appointments";
    }

    @GetMapping("/calendar")
    public String viewCalendar(Model model) {
        model.addAttribute("pageTitle", "My Calendar");
        return "medical-specialist/calendar";
    }

    @GetMapping("/my-schedule")
    public String viewMySchedule(Model model) {
        model.addAttribute("pageTitle", "My Schedule");
        return "medical-specialist/schedule";
    }
}