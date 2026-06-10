// src/main/java/com/nvivx/vixhealthsystem/controllers/QuestionnaireController.java
package com.nvivx.vixhealthsystem.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/questionnaire")
public class QuestionnaireController {

    @GetMapping
    public String showQuestionnaire(Model model) {
        model.addAttribute("pageTitle", "Find the Right Specialist");
        model.addAttribute("symptomCategories", getSymptomCategories());
        return "questionnaire/index";
    }

    @PostMapping("/result")
    public String processQuestionnaire(@RequestParam String symptomArea,
                                       @RequestParam(required = false) String severity,
                                       @RequestParam(required = false) String duration,
                                       Model model,
                                       RedirectAttributes redirectAttributes) {

        Map<String, Object> recommendation = analyzeSymptoms(symptomArea, severity, duration);

        model.addAttribute("recommendation", recommendation);
        model.addAttribute("pageTitle", "Specialist Recommendation");
        return "questionnaire/result";
    }

    private Map<String, Object> analyzeSymptoms(String symptomArea, String severity, String duration) {
        Map<String, Object> result = new HashMap<>();

        Map<String, String> symptomToSpecialist = new HashMap<>();
        symptomToSpecialist.put("chest_pain", "Cardiology");
        symptomToSpecialist.put("palpitations", "Cardiology");
        symptomToSpecialist.put("shortness_breath", "Cardiology / Pulmonology");
        symptomToSpecialist.put("severe_headache", "Neurology");
        symptomToSpecialist.put("dizziness", "Neurology");
        symptomToSpecialist.put("joint_pain", "Orthopaedics");
        symptomToSpecialist.put("back_pain", "Orthopaedics");
        symptomToSpecialist.put("abdominal_pain", "Gastroenterology / General Medicine");
        symptomToSpecialist.put("fever", "General Medicine / Infectious Diseases");
        symptomToSpecialist.put("skin_rash", "Dermatology");
        symptomToSpecialist.put("vision_problems", "Ophthalmology");
        symptomToSpecialist.put("anxiety", "Psychiatry / Psychology");

        String specialist = symptomToSpecialist.getOrDefault(symptomArea, "General Medicine");

        String urgency;
        if ("high".equals(severity)) {
            urgency = "HIGH - Please seek medical attention within 24 hours";
        } else if (symptomArea.equals("chest_pain") || symptomArea.equals("shortness_breath")) {
            urgency = "HIGH - Seek immediate attention";
        } else if ("medium".equals(severity) || "weeks".equals(duration)) {
            urgency = "MEDIUM - Schedule an appointment within 1-2 weeks";
        } else {
            urgency = "LOW - Can schedule a routine appointment";
        }

        String description = getDescription(specialist, symptomArea);

        result.put("specialist", specialist);
        result.put("urgency", urgency);
        result.put("description", description);
        result.put("symptomArea", symptomArea);
        result.put("departmentLink", "/departments/" + specialist.toLowerCase().replace(" / ", "_").replace(" ", ""));

        return result;
    }

    private String getDescription(String specialist, String symptom) {
        switch (specialist) {
            case "Cardiology":
                return "Our cardiology department specializes in heart and vascular conditions. They can perform ECG, echocardiograms, and stress tests to diagnose your condition.";
            case "Neurology":
                return "Neurologists diagnose and treat disorders of the nervous system including headaches, epilepsy, and movement disorders. They may recommend EEG or MRI imaging.";
            case "Orthopaedics":
                return "Orthopaedic specialists treat bone, joint, and muscle conditions. They offer both surgical and non-surgical treatments including physiotherapy.";
            default:
                return "Our general medicine department provides comprehensive care and can refer you to a specialist if needed. They perform initial assessments and routine check-ups.";
        }
    }

    private Map<String, String> getSymptomCategories() {
        Map<String, String> categories = new HashMap<>();
        categories.put("chest_pain", "Chest pain or discomfort");
        categories.put("palpitations", "Heart palpitations");
        categories.put("shortness_breath", "Shortness of breath");
        categories.put("severe_headache", "Severe or persistent headache");
        categories.put("dizziness", "Dizziness or vertigo");
        categories.put("joint_pain", "Joint or muscle pain");
        categories.put("back_pain", "Back pain");
        categories.put("abdominal_pain", "Abdominal pain");
        categories.put("fever", "Fever or high temperature");
        categories.put("skin_rash", "Skin rash or irritation");
        categories.put("vision_problems", "Vision problems");
        categories.put("anxiety", "Anxiety or panic attacks");
        return categories;
    }
}