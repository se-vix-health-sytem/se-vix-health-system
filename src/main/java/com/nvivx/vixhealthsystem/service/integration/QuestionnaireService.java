package com.nvivx.vixhealthsystem.service.integration;

import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

/**
 * Implements the public symptom-triage questionnaire (UC15).
 *
 * Given a symptom area selected by a site visitor, this service returns a recommended
 * specialist type and an urgency level.  The mapping is hardcoded rather than database-driven
 * because the questionnaire content is owned by the medical team and changes infrequently.
 *
 * @see com.nvivx.vixhealthsystem.controllers.site.QuestionnaireController
 */
@Service
public class QuestionnaireService {

    // =========================================================
    // PUBLIC API
    // =========================================================

    /**
     * Maps a symptom area to a specialist recommendation and urgency flag.
     *
     * @param symptomArea  Key identifying the selected symptom (e.g., {@code "chest_pain"}).
     * @return             Map with keys: {@code specialist} (String), {@code message} (String),
     *                     {@code urgency} (String).
     */
    public Map<String, Object> analyzeSymptoms(String symptomArea) {
        Map<String, Object> result = new HashMap<>();

        Map<String, String> symptomToSpecialist = getStringStringMap();

        String specialist = symptomToSpecialist.getOrDefault(symptomArea, "General Medicine");
        result.put("specialist", specialist);
        result.put("message", "Based on your symptoms, we recommend consulting a " + specialist + " specialist.");
        result.put("urgency", determineUrgency(symptomArea));

        return result;
    }

    // =========================================================
    // HELPERS
    // =========================================================

    /** Returns the static symptom-to-specialist lookup table. */
    private static @NonNull Map<String, String> getStringStringMap() {
        Map<String, String> symptomToSpecialist = new HashMap<>();
        symptomToSpecialist.put("chest_pain", "Cardiology");
        symptomToSpecialist.put("shortness_breath", "Cardiology");
        symptomToSpecialist.put("headache", "Neurology");
        symptomToSpecialist.put("joint_pain", "Orthopaedics");
        symptomToSpecialist.put("abdominal_pain", "Gynaecology/General Medicine");
        symptomToSpecialist.put("fever", "General Medicine");
        symptomToSpecialist.put("skin_rash", "Dermatology");
        return symptomToSpecialist;
    }

    /**
     * Returns a human-readable urgency string for the given symptom.
     *
     * Symptoms not in the urgency table default to MEDIUM priority.
     *
     * @param symptom  Symptom key.
     * @return         Urgency label (e.g., {@code "HIGH - Seek immediate attention"}).
     */
    private String determineUrgency(String symptom) {
        Map<String, String> urgency = new HashMap<>();
        urgency.put("chest_pain", "HIGH - Seek immediate attention");
        urgency.put("shortness_breath", "HIGH - Seek immediate attention");
        urgency.put("headache", "MEDIUM - Schedule an appointment soon");
        urgency.put("joint_pain", "LOW - Can schedule regular appointment");
        return urgency.getOrDefault(symptom, "MEDIUM - Consultation recommended");
    }
}