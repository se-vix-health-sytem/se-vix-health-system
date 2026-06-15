package com.nvivx.vixhealthsystem.controllers.site;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @brief Controller for the public symptom-triage questionnaire — base URL {@code /questionnaire}.
 *
 * Guides prospective patients through a lightweight symptom assessment and
 * recommends the appropriate specialist or department.  The mapping from
 * symptom area to specialist type, and the urgency calculation, are both
 * handled in-process without any external service dependency, keeping the
 * feature usable even when the database is unavailable.
 */
@Controller
@RequestMapping("/questionnaire")
public class QuestionnaireController {

    // =========================================================
    // GET HANDLERS
    // =========================================================

    /**
     * GET /questionnaire — render the symptom-input questionnaire form.
     *
     * @param model  Receives {@code pageTitle} and {@code symptomCategories}
     *               (a map of symptom keys to human-readable labels).
     * @return       Thymeleaf template {@code site/questionnaire/index}.
     */
    @GetMapping
    public String showQuestionnaire(Model model) {
        model.addAttribute("pageTitle", "Find the Right Specialist");
        model.addAttribute("symptomCategories", getSymptomCategories());
        return "site/questionnaire/index";
    }

    // =========================================================
    // POST HANDLERS
    // =========================================================

    /**
     * POST /questionnaire/result — evaluate the submitted symptoms and display a recommendation.
     *
     * Passes the symptom inputs to {@link #analyzeSymptoms} to determine the
     * appropriate specialist type and urgency level, then forwards the result
     * map to the result template.
     *
     * @param symptomArea  Key identifying the primary symptom (e.g., {@code "chest_pain"}).
     * @param severity     Optional severity level — {@code "high"}, {@code "medium"}, or
     *                     {@code "low"}; affects the urgency calculation.
     * @param duration     Optional duration hint — e.g., {@code "weeks"}; affects urgency.
     * @param model        Receives {@code recommendation} (the analysis result map)
     *                     and {@code pageTitle}.
     * @param redirectAttributes  Unused; retained for future flash-message needs.
     * @return             Thymeleaf template {@code site/questionnaire/result}.
     */
    @PostMapping("/result")
    public String processQuestionnaire(@RequestParam String symptomArea,
                                       @RequestParam(required = false) String severity,
                                       @RequestParam(required = false) String duration,
                                       Model model,
                                       RedirectAttributes redirectAttributes) {

        Map<String, Object> recommendation = analyzeSymptoms(symptomArea, severity, duration);

        model.addAttribute("recommendation", recommendation);
        model.addAttribute("pageTitle", "Specialist Recommendation");
        return "site/questionnaire/result";
    }

    // =========================================================
    // HELPERS
    // =========================================================

    /**
     * Map the submitted symptom inputs to a specialist recommendation and urgency level.
     *
     * High severity or cardiopulmonary symptoms always produce a HIGH urgency
     * rating regardless of duration.  The result map includes a pre-built
     * {@code departmentLink} so the template can render a direct link to the
     * recommended department page.
     *
     * @param symptomArea  Key from the {@link #getSymptomCategories()} map.
     * @param severity     {@code "high"}, {@code "medium"}, {@code "low"}, or {@code null}.
     * @param duration     {@code "weeks"} triggers a MEDIUM urgency bump; otherwise ignored.
     * @return             Map containing {@code specialist}, {@code urgency},
     *                     {@code description}, {@code symptomArea}, and {@code departmentLink}.
     */
    // Maps symptom key → [specialist label, department id (Long or null)]
    private static final Map<String, Object[]> SYMPTOM_MAP = new HashMap<>();
    static {
        SYMPTOM_MAP.put("chest_pain",       new Object[]{"Cardiology",    1L});
        SYMPTOM_MAP.put("palpitations",     new Object[]{"Cardiology",    1L});
        SYMPTOM_MAP.put("shortness_breath", new Object[]{"Cardiology",    1L});
        SYMPTOM_MAP.put("severe_headache",  new Object[]{"Neurology",     2L});
        SYMPTOM_MAP.put("dizziness",        new Object[]{"Neurology",     2L});
        SYMPTOM_MAP.put("joint_pain",       new Object[]{"Orthopedics",   7L});
        SYMPTOM_MAP.put("back_pain",        new Object[]{"Orthopedics",   7L});
        SYMPTOM_MAP.put("skin_rash",        new Object[]{"Dermatology",  10L});
        SYMPTOM_MAP.put("abdominal_pain",   new Object[]{"General Medicine", null});
        SYMPTOM_MAP.put("fever",            new Object[]{"General Medicine", null});
    }

    private Map<String, Object> analyzeSymptoms(String symptomArea, String severity, String duration) {
        Map<String, Object> result = new HashMap<>();

        Object[] info = SYMPTOM_MAP.getOrDefault(symptomArea, new Object[]{"General Medicine", null});
        String specialist = (String) info[0];
        Long departmentId = (Long) info[1];

        String urgency;
        if ("high".equals(severity) || "SEVERE".equals(severity)) {
            urgency = "HIGH - Please seek medical attention within 24 hours";
        } else if ("chest_pain".equals(symptomArea) || "shortness_breath".equals(symptomArea) || "palpitations".equals(symptomArea)) {
            urgency = "HIGH - Seek immediate attention";
        } else if ("medium".equals(severity) || "MODERATE".equals(severity) || "WEEKS".equals(duration) || "MONTHS".equals(duration)) {
            urgency = "MEDIUM - Schedule an appointment within 1-2 weeks";
        } else {
            urgency = "LOW - Can schedule a routine appointment";
        }

        String description = getDescription(specialist, symptomArea);

        result.put("specialist", specialist);
        result.put("urgency", urgency);
        result.put("description", description);
        result.put("symptomArea", symptomArea);
        result.put("departmentId", departmentId);
        result.put("departmentLink", departmentId != null ? "/departments/" + departmentId : null);

        return result;
    }

    /**
     * Return a patient-friendly description of the recommended specialist's services.
     *
     * @param specialist  Specialist type string returned by {@link #analyzeSymptoms}.
     * @param symptom     Original symptom key (currently unused but kept for future expansion).
     * @return            Short paragraph describing the department's capabilities.
     */
    private String getDescription(String specialist, String symptom) {
        switch (specialist) {
            case "Cardiology":
                return "Our cardiology department specialises in heart and vascular conditions. They can perform ECG, echocardiograms, and stress tests to diagnose your condition.";
            case "Neurology":
                return "Neurologists diagnose and treat disorders of the nervous system including headaches, epilepsy, and movement disorders. They may recommend EEG or MRI imaging.";
            case "Orthopedics":
                return "Our orthopaedic specialists treat bone, joint, and muscle conditions. They offer both surgical and non-surgical treatments including physiotherapy referrals.";
            case "Dermatology":
                return "Our dermatology department treats conditions affecting the skin, hair, and nails. They offer both diagnostic and therapeutic services including dermatoscopy and biopsies.";
            default:
                return "Our general medicine team provides comprehensive initial assessments and can refer you to the appropriate specialist if needed.";
        }
    }

    /**
     * Build the symptom key-to-label map used to populate the questionnaire form.
     *
     * @return Map of symptom identifier strings to their human-readable descriptions.
     */
    private Map<String, String> getSymptomCategories() {
        Map<String, String> categories = new LinkedHashMap<>();
        categories.put("chest_pain",       "Chest pain or discomfort");
        categories.put("palpitations",     "Heart palpitations");
        categories.put("shortness_breath", "Shortness of breath");
        categories.put("severe_headache",  "Severe or persistent headache");
        categories.put("dizziness",        "Dizziness or vertigo");
        categories.put("joint_pain",       "Joint or muscle pain");
        categories.put("back_pain",        "Back pain");
        categories.put("abdominal_pain",   "Abdominal pain");
        categories.put("fever",            "Fever or high temperature");
        categories.put("skin_rash",        "Skin rash or irritation");
        return categories;
    }
}
