package com.nvivx.vixhealthsystem.service.integration;

import com.nvivx.vixhealthsystem.model.medical.Appointment;
import com.nvivx.vixhealthsystem.model.person.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    // Remove the mailSender dependency for now
    public NotificationService() {
    }

    public void sendAppointmentConfirmation(Patient patient, Appointment appointment) {
        // Just log to console for demo
        System.out.println("=== EMAIL NOTIFICATION (DEMO MODE) ===");
        System.out.println("To: " + (patient.getEmail() != null ? patient.getEmail() : "No email"));
        System.out.println("Subject: Appointment Confirmation");
        System.out.println("Body: Your appointment with Dr. " +
                appointment.getMedicalSpecialist().getName() + " " +
                appointment.getMedicalSpecialist().getSurname() +
                " on " + appointment.getDateTime() + " is confirmed.");
        System.out.println("=====================================");
    }

    public void sendAppointmentReminder(Patient patient, Appointment appointment) {
        System.out.println("=== EMAIL NOTIFICATION (DEMO MODE) ===");
        System.out.println("To: " + (patient.getEmail() != null ? patient.getEmail() : "No email"));
        System.out.println("Subject: Appointment Reminder");
        System.out.println("Body: Reminder: Your appointment with Dr. " +
                appointment.getMedicalSpecialist().getName() + " " +
                appointment.getMedicalSpecialist().getSurname() +
                " is tomorrow at " + appointment.getDateTime());
        System.out.println("=====================================");
    }

    public void sendExamResultsAvailable(Patient patient, String examType) {
        System.out.println("=== EMAIL NOTIFICATION (DEMO MODE) ===");
        System.out.println("To: " + (patient.getEmail() != null ? patient.getEmail() : "No email"));
        System.out.println("Subject: Exam Results Available");
        System.out.println("Body: Your " + examType + " results are now available.");
        System.out.println("=====================================");
    }

    public void sendAppointmentCancellation(Patient patient, Appointment appointment) {
        System.out.println("=== EMAIL NOTIFICATION (DEMO MODE) ===");
        System.out.println("To: " + (patient.getEmail() != null ? patient.getEmail() : "No email"));
        System.out.println("Subject: Appointment Cancelled");
        System.out.println("Body: Your appointment on " + appointment.getDateTime() + " has been cancelled.");
        System.out.println("=====================================");
    }
}