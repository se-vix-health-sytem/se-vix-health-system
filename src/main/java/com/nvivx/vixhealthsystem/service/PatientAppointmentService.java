// Update src/main/java/com/nvivx/vixhealthsystem/service/PatientAppointmentService.java
package com.nvivx.vixhealthsystem.service;

import com.nvivx.vixhealthsystem.exception.SlotNotAvailableException;
import com.nvivx.vixhealthsystem.mock.MockDatabase;
import com.nvivx.vixhealthsystem.model.medical.Appointment;
import com.nvivx.vixhealthsystem.model.person.Patient;
import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PatientAppointmentService {

    private final MockDatabase mockDatabase;
    private final AuditService auditService;

    public PatientAppointmentService(MockDatabase mockDatabase, AuditService auditService) {
        this.mockDatabase = mockDatabase;
        this.auditService = auditService;
    }

    public List<Appointment> getPatientAppointments(int patientId) {
        return mockDatabase.findAllAppointments().stream()
                .filter(a -> a.getPatient() != null && a.getPatient().getId() == patientId)
                .collect(Collectors.toList());
    }

    public List<MedicalSpecialist> getAvailableSpecialists() {
        return mockDatabase.findAllEmployees().stream()
                .filter(e -> e instanceof MedicalSpecialist && e.isActive())
                .map(e -> (MedicalSpecialist) e)
                .collect(Collectors.toList());
    }

    public String getDoctorName(int doctorId) {
        MedicalSpecialist doctor = mockDatabase.findMedicalSpecialistById(doctorId);
        return doctor != null ? doctor.getName() + " " + doctor.getSurname() : "Unknown";
    }

    public List<LocalDateTime> getAvailableSlots(int doctorId, LocalDate startDate, LocalDate endDate) {
        List<LocalDateTime> availableSlots = new ArrayList<>();
        List<Appointment> existingAppointments = mockDatabase.findAllAppointments().stream()
                .filter(a -> a.getDoctor() != null && a.getDoctor().getId() == doctorId)
                .collect(Collectors.toList());

        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            // Working hours: 9 AM to 5 PM, excluding weekends
            if (current.getDayOfWeek().getValue() <= 5) { // Monday to Friday
                for (int hour = 9; hour <= 16; hour++) {
                    LocalDateTime slot = LocalDateTime.of(current, LocalTime.of(hour, 0));

                    // Check if slot is available (not booked)
                    boolean isBooked = existingAppointments.stream()
                            .anyMatch(a -> a.getDateTime().equals(slot));

                    if (!isBooked) {
                        availableSlots.add(slot);
                    }
                }
            }
            current = current.plusDays(1);
        }

        return availableSlots;
    }

    public Appointment bookAppointment(int patientId, int specialistId, LocalDateTime dateTime) {
        Patient patient = mockDatabase.findPatientById(patientId);
        MedicalSpecialist specialist = mockDatabase.findMedicalSpecialistById(specialistId);

        if (patient == null || specialist == null) {
            throw new IllegalArgumentException("Patient or specialist not found");
        }

        // Check if slot is available
        boolean slotAvailable = isSlotAvailable(specialistId, dateTime);
        if (!slotAvailable) {
            throw new SlotNotAvailableException("Selected time slot is not available");
        }

        Appointment appointment = new Appointment(dateTime, 30, "Booked via patient portal", patient, specialist);
        appointment.setStatus("CONFIRMED");
        Appointment saved = mockDatabase.saveAppointment(appointment);

        auditService.log("BOOK_APPOINTMENT", "Appointment", String.valueOf(saved.getId()),
                "Patient " + patientId + " booked with specialist " + specialistId);

        return saved;
    }

    public void cancelAppointment(int appointmentId, int patientId) {
        Appointment appointment = mockDatabase.findAppointmentById(appointmentId);
        if (appointment == null || appointment.getPatient().getId() != patientId) {
            throw new IllegalArgumentException("Appointment not found or access denied");
        }

        appointment.setStatus("CANCELLED");
        mockDatabase.saveAppointment(appointment);

        auditService.log("CANCEL_APPOINTMENT", "Appointment", String.valueOf(appointmentId),
                "Cancelled by patient " + patientId);
    }

    public void rescheduleAppointment(int appointmentId, int patientId, LocalDateTime newDateTime) {
        Appointment appointment = mockDatabase.findAppointmentById(appointmentId);
        if (appointment == null || appointment.getPatient().getId() != patientId) {
            throw new IllegalArgumentException("Appointment not found or access denied");
        }

        if (!isSlotAvailable(appointment.getDoctor().getId(), newDateTime)) {
            throw new SlotNotAvailableException("New time slot is not available");
        }

        LocalDateTime oldDateTime = appointment.getDateTime();
        appointment.setDateTime(newDateTime);
        appointment.setStatus("RESCHEDULED");
        mockDatabase.saveAppointment(appointment);

        auditService.log("RESCHEDULE_APPOINTMENT", "Appointment", String.valueOf(appointmentId),
                "Rescheduled from " + oldDateTime + " to " + newDateTime + " by patient " + patientId);
    }

    private boolean isSlotAvailable(int specialistId, LocalDateTime dateTime) {
        List<Appointment> appointments = mockDatabase.findAllAppointments();
        return appointments.stream()
                .filter(a -> a.getDoctor() != null && a.getDoctor().getId() == specialistId)
                .noneMatch(a -> a.getDateTime().equals(dateTime) && !"CANCELLED".equals(a.getStatus()));
    }
}