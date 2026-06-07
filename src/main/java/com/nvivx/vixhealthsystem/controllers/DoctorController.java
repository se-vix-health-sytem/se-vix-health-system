package com.nvivx.vixhealthsystem.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/doctor")

public class DoctorController {
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "Medical Staff Dashboard");
        return "doctor/dashboard";
    }

    @GetMapping("/appointments")
    public String viewAppointments(Model model) {
        model.addAttribute("pageTitle", "My Appointments");
        model.addAttribute("message", "Appointment list will be displayed here");
        return "doctor/result";
    }

    @GetMapping("/calendar")
    public String viewCalendar(Model model) {
        model.addAttribute("pageTitle", "Calendar");
        model.addAttribute("message", "Calendar view will be displayed here");
        return "doctor/result";
    }

    @GetMapping("/patients/search")
    public String searchPatientsForm(Model model) {
        model.addAttribute("pageTitle", "Search Patients");
        return "doctor/patient-search";
    }

    @PostMapping("/patients/search")
    public String searchPatients(@RequestParam String query, Model model) {
        // Placeholder: will call patientService.searchPatients()
        model.addAttribute("pageTitle", "Search Results");
        model.addAttribute("message",
                "BACKEND RECEIVED: Searching for patient '" + query + "'");
        return "doctor/result";
    }

    @GetMapping("/patients/{patientId}/record")
    public String viewMedicalRecord(@PathVariable Long patientId, Model model) {
        // Placeholder: will call medicalRecordService.getMedicalRecord()
        model.addAttribute("pageTitle", "Medical Record");
        model.addAttribute("message",
                "BACKEND RECEIVED: Viewing medical record for patient #" + patientId);
        return "doctor/result";
    }

    @PostMapping("/patients/{patientId}/add-condition")
    public String addMedicalCondition(@PathVariable Long patientId,
                                      @RequestParam String conditionName,
                                      @RequestParam String description,
                                      Model model) {
        model.addAttribute("pageTitle", "Condition Added Successfully");
        model.addAttribute("message",
                "✅ Medical Condition Added!\n\n" +
                        "Patient ID: " + patientId + "\n" +
                        "Condition: " + conditionName + "\n" +
                        "Description: " + description + "\n\n" +
                        "This proves the backend received your form data!");
        return "doctor/result";
    }

    @PostMapping("/patients/{patientId}/add-prescription")
    public String addPrescription(@PathVariable Long patientId,
                                  @RequestParam String medication,
                                  @RequestParam String dosage,
                                  Model model) {
        // This PROVES the frontend sent data to the backend!
        model.addAttribute("pageTitle", "Prescription Added Successfully");
        model.addAttribute("message",
                "✅ Prescription Added!\n\n" +
                        "Patient ID: " + patientId + "\n" +
                        "Medication: " + medication + "\n" +
                        "Dosage: " + dosage + "\n\n" +
                        "This data was sent from the form to the backend controller.\n" +
                        "In production, this would be saved to the database.");
        return "doctor/result";
    }


    // UC20 - Add Diagnosis (FR4.3, FR4.5)
    @PostMapping("/patients/{patientId}/add-diagnosis")
    public String addDiagnosis(@PathVariable Long patientId,
                               @RequestParam String diagnosisName,
                               @RequestParam String description,
                               @RequestParam String severity,
                               Model model) {
        model.addAttribute("pageTitle", "Diagnosis Added Successfully");
        model.addAttribute("message",
                "✅ Diagnosis Added!\n\n" +
                        "Patient ID: " + patientId + "\n" +
                        "Diagnosis: " + diagnosisName + "\n" +
                        "Severity: " + severity + "\n" +
                        "Description: " + description + "\n\n" +
                        "Frontend → Backend communication verified!");
        return "doctor/result";
    }

    // UC20 - Add Exam Results (FR4.5, FR4.7)
    @PostMapping("/patients/{patientId}/add-exam-result")
    public String addExamResult(@PathVariable Long patientId,
                                @RequestParam String examType,
                                @RequestParam String result,
                                @RequestParam(required = false, defaultValue = "") String notes,
                                Model model) {
        model.addAttribute("pageTitle", "Exam Result Added Successfully");
        model.addAttribute("message",
                "✅ Exam Result Added!\n\n" +
                        "Patient ID: " + patientId + "\n" +
                        "Exam Type: " + examType + "\n" +
                        "Result: " + result + "\n" +
                        (notes.isEmpty() ? "" : "Notes: " + notes + "\n") +
                        "\nBackend successfully received this data!");
        return "doctor/result";
    }

    // FR4.6 - View Family Disease History
    @GetMapping("/patients/{patientId}/family-history")
    public String viewFamilyHistory(@PathVariable Long patientId, Model model) {
        // TODO: TEMPORARY - Testing frontend-backend communication
        // FUTURE: Will call medicalRecordService.getFamilyHistory(patientId)
        //         Patient.familyHistory is part of MedicalRecord entity
        model.addAttribute("pageTitle", "Family Disease History");
        model.addAttribute("message",
                "BACKEND RECEIVED: Viewing family disease history for patient #" + patientId);
        return "doctor/result";
    }

    // UC30 - View Surgeries Schedule
    @GetMapping("/surgeries")
    public String viewSurgeries(Model model) {
        // TODO: TEMPORARY - Testing frontend-backend communication
        // FUTURE: Will call surgeryService.getSurgeriesByDoctor(doctorId, date)
        //         Doctor will have List<Surgery> in their calendar
        model.addAttribute("pageTitle", "My Surgeries");
        model.addAttribute("message",
                "BACKEND RECEIVED: Viewing scheduled surgeries for current doctor");
        return "doctor/result";
    }

}
