package com.nvivx.vixhealthsystem.service.integration;

import com.nvivx.vixhealthsystem.model.medical.Appointment;
import com.nvivx.vixhealthsystem.model.person.Patient;
import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class NotificationServiceTest {

    // Service that is under test
    private final NotificationService service =
            new NotificationService();

    /**
     * Helper method that creates a valid patient
     */
    private Patient createPatient() {
        Patient patient = new Patient();
        patient.setName("Mario");
        patient.setSurname("Rossi");
        patient.setEmail("mario@test.com");
        return patient;
    }

    /**
     * Helper method that creates a valid doctor
     */
    private MedicalSpecialist createDoctor() {
        MedicalSpecialist doctor = new MedicalSpecialist();
        doctor.setName("Luigi");
        doctor.setSurname("Bianchi");
        return doctor;
    }

    /**
     * Helper method that creates a valid appointment
     */
    private Appointment createAppointment() {
        Appointment appointment =
                new Appointment(
                        1,
                        LocalDateTime.of(2026, 6, 10, 10, 0),
                        30,
                        "Checkup"
                );

        appointment.setMedicalSpecialist(createDoctor());

        return appointment;
    }

    /**
     * Tests that sending an appointment confirmation
     * does not throw any exception when valid data is provided.
     */
    @Test
    void shouldSendAppointmentConfirmation() {

        Patient patient = createPatient();
        Appointment appointment = createAppointment();

        assertDoesNotThrow(() ->
                service.sendAppointmentConfirmation(
                        patient,
                        appointment
                )
        );
    }

    /**
     * Tests that sending an appointment reminder
     * works correctly with valid data.
     */
    @Test
    void shouldSendAppointmentReminder() {

        Patient patient = createPatient();
        Appointment appointment = createAppointment();

        assertDoesNotThrow(() ->
                service.sendAppointmentReminder(
                        patient,
                        appointment
                )
        );
    }

    /**
     * Tests that sending exam results
     * works correctly for a valid patient.
     */
    @Test
    void shouldSendExamResultsAvailable() {

        Patient patient = createPatient();

        assertDoesNotThrow(() ->
                service.sendExamResultsAvailable(
                        patient,
                        "Blood Test"
                )
        );
    }

    /**
     * Tests that sending an appointment cancellation
     * works correctly.
     */
    @Test
    void shouldSendAppointmentCancellation() {

        Patient patient = createPatient();
        Appointment appointment = createAppointment();

        assertDoesNotThrow(() ->
                service.sendAppointmentCancellation(
                        patient,
                        appointment
                )
        );
    }

    /**
     * Tests the null-patient branch of
     * sendAppointmentConfirmation().
     *
     * The method should simply print a message
     * and return without crashing.
     */
    @Test
    void shouldHandleNullPatientInConfirmation() {

        Appointment appointment = createAppointment();

        assertDoesNotThrow(() ->
                service.sendAppointmentConfirmation(
                        null,
                        appointment
                )
        );
    }

    /**
     * Tests the null-appointment branch of
     * sendAppointmentReminder().
     */
    @Test
    void shouldHandleNullAppointmentInReminder() {

        Patient patient = createPatient();

        assertDoesNotThrow(() ->
                service.sendAppointmentReminder(
                        patient,
                        null
                )
        );
    }

    /**
     * Tests the null-patient branch of
     * sendExamResultsAvailable().
     */
    @Test
    void shouldHandleNullPatientInExamResults() {

        assertDoesNotThrow(() ->
                service.sendExamResultsAvailable(
                        null,
                        "MRI"
                )
        );
    }

    /**
     * Tests the null-appointment branch of
     * sendAppointmentCancellation().
     */
    @Test
    void shouldHandleNullAppointmentInCancellation() {

        Patient patient = createPatient();

        assertDoesNotThrow(() ->
                service.sendAppointmentCancellation(
                        patient,
                        null
                )
        );
    }
}