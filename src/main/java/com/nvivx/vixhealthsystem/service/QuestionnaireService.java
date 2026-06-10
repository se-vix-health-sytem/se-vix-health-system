// src/main/java/com/nvivx/vixhealthsystem/service/QuestionnaireService.java
package com.nvivx.vixhealthsystem.service;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class QuestionnaireService {

    public Map<String, Object> analyzeSymptoms(String symptomArea) {
        Map<String, Object> result = new HashMap<>();

        Map<String, String> symptomToSpecialist = new HashMap<>();
        symptomToSpecialist.put("chest_pain", "Cardiology");
        symptomToSpecialist.put("shortness_breath", "Cardiology");
        symptomToSpecialist.put("headache", "Neurology");
        symptomToSpecialist.put("joint_pain", "Orthopaedics");
        symptomToSpecialist.put("abdominal_pain", "Gynaecology/General Medicine");
        symptomToSpecialist.put("fever", "General Medicine");
        symptomToSpecialist.put("skin_rash", "Dermatology");

        String specialist = symptomToSpecialist.getOrDefault(symptomArea, "General Medicine");
        result.put("specialist", specialist);
        result.put("message", "Based on your symptoms, we recommend consulting a " + specialist + " specialist.");
        result.put("urgency", determineUrgency(symptomArea));

        return result;
    }

    private String determineUrgency(String symptom) {
        Map<String, String> urgency = new HashMap<>();
        urgency.put("chest_pain", "HIGH - Seek immediate attention");
        urgency.put("shortness_breath", "HIGH - Seek immediate attention");
        urgency.put("headache", "MEDIUM - Schedule an appointment soon");
        urgency.put("joint_pain", "LOW - Can schedule regular appointment");
        return urgency.getOrDefault(symptom, "MEDIUM - Consultation recommended");
    }
}