package com.nvivx.vixhealthsystem.service.integration;

import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

/**
 * No mocks are needed because this service has
 * no external dependencies.
 *
 * Arrange = prepare test data
 * Act = call the method being tested
 * Assert = verify the returned result
 * Verify = not needed because there are no mocks
 */

public class QuestionaireServiceTest {

    private final QuestionnaireService service =
            new QuestionnaireService();

    /**
     * Tests that chest pain is mapped to Cardiology
     * and marked as HIGH urgency.
     */
    @Test
    void shouldRecommendCardiologyForChestPain() {

        // Arrange
        String symptom = "chest_pain";

        // Act
        Map<String, Object> result =
                service.analyzeSymptoms(symptom);

        // Assert
        assertNotNull(result);
        assertEquals("Cardiology", result.get("specialist"));
        assertEquals(
                "HIGH - Seek immediate attention",
                result.get("urgency")
        );

        assertTrue(
                result.get("message")
                        .toString()
                        .contains("Cardiology")
        );
    }

    /**
     * Tests that headache is mapped to Neurology
     * and marked as MEDIUM urgency.
     */
    @Test
    void shouldRecommendNeurologyForHeadache() {

        // Arrange
        String symptom = "headache";

        // Act
        Map<String, Object> result =
                service.analyzeSymptoms(symptom);

        // Assert
        assertNotNull(result);
        assertEquals("Neurology", result.get("specialist"));
        assertEquals(
                "MEDIUM - Schedule an appointment soon",
                result.get("urgency")
        );
    }

    /**
     * Tests that joint pain is mapped to Orthopaedics
     * and marked as LOW urgency.
     */
    @Test
    void shouldRecommendOrthopaedicsForJointPain() {

        // Arrange
        String symptom = "joint_pain";

        // Act
        Map<String, Object> result =
                service.analyzeSymptoms(symptom);

        // Assert
        assertNotNull(result);
        assertEquals("Orthopaedics", result.get("specialist"));
        assertEquals(
                "LOW - Can schedule regular appointment",
                result.get("urgency")
        );
    }

    /**
     * Tests that skin rash is mapped to Dermatology.
     * Since there is no custom urgency configured,
     * the default urgency should be returned.
     */
    @Test
    void shouldRecommendDermatologyForSkinRash() {

        // Arrange
        String symptom = "skin_rash";

        // Act
        Map<String, Object> result =
                service.analyzeSymptoms(symptom);

        // Assert
        assertNotNull(result);
        assertEquals("Dermatology", result.get("specialist"));
        assertEquals(
                "MEDIUM - Consultation recommended",
                result.get("urgency")
        );
    }

    /**
     * Tests that an unknown symptom falls back to
     * General Medicine and default urgency.
     */
    @Test
    void shouldUseDefaultValuesForUnknownSymptom() {

        // Arrange
        String symptom = "random_symptom";

        // Act
        Map<String, Object> result =
                service.analyzeSymptoms(symptom);

        // Assert
        assertNotNull(result);
        assertEquals(
                "General Medicine",
                result.get("specialist")
        );
        assertEquals(
                "MEDIUM - Consultation recommended",
                result.get("urgency")
        );
    }

    /**
     * Tests another mapped symptom:
     * shortness of breath should be handled
     * by Cardiology with HIGH urgency.
     */
    @Test
    void shouldRecommendCardiologyForShortnessOfBreath() {

        // Arrange
        String symptom = "shortness_breath";

        // Act
        Map<String, Object> result =
                service.analyzeSymptoms(symptom);

        // Assert
        assertNotNull(result);
        assertEquals("Cardiology", result.get("specialist"));
        assertEquals(
                "HIGH - Seek immediate attention",
                result.get("urgency")
        );
    }
}
