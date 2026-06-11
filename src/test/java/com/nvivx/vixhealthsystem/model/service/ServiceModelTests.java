package com.nvivx.vixhealthsystem.model.service;

import com.nvivx.vixhealthsystem.service.AuditService;
import com.nvivx.vixhealthsystem.service.integration.NotificationService;
import com.nvivx.vixhealthsystem.service.integration.PaymentService;
import com.nvivx.vixhealthsystem.service.integration.QuestionnaireService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ServiceModelTests {

    @Test
    void testServiceConstructorsExist() {
        // These tests verify services can be instantiated (basic smoke tests)
        // Full service tests should be in individual test files

        assertDoesNotThrow(() -> {
            new AuditService(null);
            new NotificationService();
            new PaymentService(null);
            new QuestionnaireService();
        });
    }

    @Test
    void testAuditServiceLogging() {
        // This is a minimal test - full tests in AuditServiceTest if needed
        assertNotNull(new AuditService(null));
    }

    @Test
    void testNotificationServiceConsoleOutput() {
        NotificationService ns = new NotificationService();
        // These should not throw exceptions
        ns.sendAppointmentConfirmation(null, null);
        ns.sendAppointmentReminder(null, null);
        ns.sendExamResultsAvailable(null, "X-Ray");
        ns.sendAppointmentCancellation(null, null);
    }

    @Test
    void testPaymentService() {
        PaymentService ps = new PaymentService(null);
        assertEquals(85.00f, ps.getAppointmentCost(1));
    }

    @Test
    void testQuestionnaireService() {
        QuestionnaireService qs = new QuestionnaireService();
        var result = qs.analyzeSymptoms("chest_pain");
        assertNotNull(result);
        assertTrue(result.containsKey("specialist"));
        assertTrue(result.containsKey("urgency"));
        assertTrue(result.containsKey("message"));
    }
}