package com.nvivx.vixhealthsystem.service.integration;

import com.nvivx.vixhealthsystem.model.medical.Appointment;
import com.nvivx.vixhealthsystem.model.person.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendAppointmentConfirmation(Patient patient, Appointment appointment) {
        String subject = "Appointment Confirmation - VIX Health System";
        String body = String.format(
                """
                Dear %s %s,
                
                Your appointment has been confirmed!
                
                📅 Date: %s
                🏥 Doctor: Dr. %s %s
                📍 Location: VIX Health System
                
                You can view or cancel your appointment at any time from your patient dashboard.
                
                Best regards,
                VIX Health System
                """,
                patient.getName(), patient.getSurname(),
                appointment.getDateTime(),
                appointment.getMedicalSpecialist().getName(), appointment.getMedicalSpecialist().getSurname()
        );

        sendEmail(patient.getEmail(), subject, body);
    }

    public void sendAppointmentReminder(Patient patient, Appointment appointment) {
        String subject = "Appointment Reminder - VIX Health System";
        String body = String.format(
                """
                Dear %s %s,
                
                This is a reminder that you have an appointment tomorrow:
                
                📅 Date: %s
                🏥 Doctor: Dr. %s %s
                
                Please arrive 10 minutes before your scheduled time.
                
                Best regards,
                VIX Health System
                """,
                patient.getName(), patient.getSurname(),
                appointment.getDateTime(),
                appointment.getMedicalSpecialist().getName(), appointment.getMedicalSpecialist().getSurname()
        );

        sendEmail(patient.getEmail(), subject, body);
    }

    public void sendExamResultsAvailable(Patient patient, String examType) {
        String subject = "New Exam Results Available - VIX Health System";
        String body = String.format(
                """
                Dear %s %s,
                
                Your %s exam results are now available.
                
                Please log in to your patient portal to view them.
                
                Best regards,
                VIX Health System
                """,
                patient.getName(), patient.getSurname(), examType
        );

        sendEmail(patient.getEmail(), subject, body);
    }

    public void sendAppointmentCancellation(Patient patient, Appointment appointment) {
        String subject = "Appointment Cancellation Confirmation - VIX Health System";
        String body = String.format(
                """
                Dear %s %s,
                
                Your appointment scheduled for %s has been cancelled as requested.
                
                If you did not request this cancellation, please contact us immediately.
                
                Best regards,
                VIX Health System
                """,
                patient.getName(), patient.getSurname(),
                appointment.getDateTime()
        );

        sendEmail(patient.getEmail(), subject, body);
    }

    private void sendEmail(String to, String subject, String body) {
        if (to == null || to.isBlank()) {
            log.warn("No email address provided for notification: {}", subject);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, false); // false for plain text, true for HTML

            mailSender.send(message);
            log.info("Email sent to {}: {}", to, subject);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            // Fallback to console logging for demo
            System.out.println("=== EMAIL NOTIFICATION (DEMO MODE) ===");
            System.out.println("To: " + to);
            System.out.println("Subject: " + subject);
            System.out.println("Body: " + body);
            System.out.println("=====================================");
        }
    }
}