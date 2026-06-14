package com.nvivx.vixhealthsystem.service.integration;

import com.nvivx.vixhealthsystem.model.medical.Appointment;
import com.nvivx.vixhealthsystem.model.person.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Simulates outbound patient notifications — appointment confirmations, reminders,
 * exam result alerts, and cancellations.
 *
 * No real email is sent; each method logs a {@code [DEMO EMAIL]} line so the
 * behaviour is visible in the console during development. Replacing the log calls
 * with an actual mail sender (SMTP / SendGrid / etc.) is all that's needed to make
 * these live.
 *
 * @see com.nvivx.vixhealthsystem.service.medical.AppointmentService
 */
@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    public NotificationService() {}

    // =========================================================
    // NOTIFICATION METHODS
    // =========================================================

    /**
     * Sends (simulates) a booking confirmation to the patient.
     *
     * @param patient      The patient who booked; may be {@code null} (skipped with a warning).
     * @param appointment  The confirmed appointment; may be {@code null} (skipped with a warning).
     */
    public void sendAppointmentConfirmation(Patient patient, Appointment appointment) {
        if (patient == null || appointment == null) {
            log.warn("sendAppointmentConfirmation skipped — missing patient or appointment");
            return;
        }
        String email = patient.getEmail() != null ? patient.getEmail() : "no-email";
        log.info("[DEMO EMAIL] To: {} | Subject: Appointment Confirmation | Dr. {} {} on {}",
                email,
                appointment.getMedicalSpecialist().getName(),
                appointment.getMedicalSpecialist().getSurname(),
                appointment.getDateTime());
    }

    /**
     * Sends (simulates) a day-before reminder to the patient.
     *
     * @param patient      The patient to remind; {@code null} skips with a warning.
     * @param appointment  The upcoming appointment; {@code null} skips with a warning.
     */
    public void sendAppointmentReminder(Patient patient, Appointment appointment) {
        if (patient == null || appointment == null) {
            log.warn("sendAppointmentReminder skipped — missing patient or appointment");
            return;
        }
        String email = patient.getEmail() != null ? patient.getEmail() : "no-email";
        log.info("[DEMO EMAIL] To: {} | Subject: Appointment Reminder | Dr. {} {} tomorrow at {}",
                email,
                appointment.getMedicalSpecialist().getName(),
                appointment.getMedicalSpecialist().getSurname(),
                appointment.getDateTime());
    }

    /**
     * Notifies (simulates) a patient that their exam results are available.
     *
     * @param patient   The patient to notify; {@code null} skips with a warning.
     * @param examType  Short description of the exam (e.g., {@code "Blood Panel"}).
     */
    public void sendExamResultsAvailable(Patient patient, String examType) {
        if (patient == null) {
            log.warn("sendExamResultsAvailable skipped — missing patient");
            return;
        }
        String email = patient.getEmail() != null ? patient.getEmail() : "no-email";
        log.info("[DEMO EMAIL] To: {} | Subject: Exam Results Available | Type: {}", email, examType);
    }

    /**
     * Notifies (simulates) a patient that their appointment has been cancelled.
     *
     * @param patient      The affected patient; {@code null} skips with a warning.
     * @param appointment  The cancelled appointment; {@code null} skips with a warning.
     */
    public void sendAppointmentCancellation(Patient patient, Appointment appointment) {
        if (patient == null || appointment == null) {
            log.warn("sendAppointmentCancellation skipped — missing patient or appointment");
            return;
        }
        String email = patient.getEmail() != null ? patient.getEmail() : "no-email";
        log.info("[DEMO EMAIL] To: {} | Subject: Appointment Cancelled | Date: {}",
                email, appointment.getDateTime());
    }
}
