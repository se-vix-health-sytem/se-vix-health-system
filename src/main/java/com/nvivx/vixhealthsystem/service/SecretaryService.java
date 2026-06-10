package com.nvivx.vixhealthsystem.service;

import com.nvivx.vixhealthsystem.mock.MockDatabase;
import com.nvivx.vixhealthsystem.model.facility.Room;
import com.nvivx.vixhealthsystem.model.medical.Appointment;
import com.nvivx.vixhealthsystem.model.person.Patient;
import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SecretaryService {

    private final MockDatabase mockDatabase;
    private final AuditService auditService;


    public SecretaryService(MockDatabase mockDatabase, AuditService auditService) {
        this.mockDatabase = mockDatabase;
        this.auditService = auditService;
    }

    // ========== UC21: Check Bed and Room Availability ==========
    public List<Room> getAllRooms() {
        return mockDatabase.findAllRooms();
    }

    public List<Room> getAvailableRooms() {
        return mockDatabase.findAllRooms().stream()
                .filter(Room::hasAvailableBeds)
                .toList();
    }

    // ========== UC22: Admit Patient (Assign bed/room) ==========
    public Room admitPatient(int patientId, int roomId) {
        Patient patient = mockDatabase.findPatientById(patientId);
        Room room = mockDatabase.findRoomById(roomId);

        if (patient == null) throw new IllegalArgumentException("Patient not found");
        if (room == null) throw new IllegalArgumentException("Room not found");
        if (!room.hasAvailableBeds()) throw new IllegalArgumentException("No available beds in this room");

        room.setOccupiedBeds(room.getOccupiedBeds() + 1);
        mockDatabase.saveRoom(room);

        if (auditService != null) {
            auditService.log("ADMIT_PATIENT", "Patient", String.valueOf(patientId),
                    "Admitted to room " + room.getRoomNumber());
        }

        return room;
    }

    // ========== UC23: Dismiss Patient ==========
    public Room dismissPatient(int patientId, int roomId) {
        Room room = mockDatabase.findRoomById(roomId);
        if (room == null) throw new IllegalArgumentException("Room not found");
        if (room.getOccupiedBeds() <= 0) throw new IllegalArgumentException("No patients in this room");

        room.setOccupiedBeds(room.getOccupiedBeds() - 1);
        mockDatabase.saveRoom(room);

        if (auditService != null) {
            auditService.log("DISMISS_PATIENT", "Patient", String.valueOf(patientId),
                    "Dismissed from room " + room.getRoomNumber());
        }

        return room;
    }

    // ========== UC14: Book Appointment (simplified) ==========
    public Appointment bookAppointment(int patientId, int doctorId, LocalDateTime dateTime, String notes) {
        Patient patient = mockDatabase.findPatientById(patientId);
        MedicalSpecialist doctor = mockDatabase.findMedicalSpecialistById(doctorId);

        if (patient == null) throw new IllegalArgumentException("Patient not found");
        if (doctor == null) throw new IllegalArgumentException("Doctor not found");

        Appointment appointment = new Appointment(dateTime, 30, notes, patient, doctor);
        appointment.setStatus("CONFIRMED");

        Appointment saved = mockDatabase.saveAppointment(appointment);

        if (auditService != null) {
            auditService.log("BOOK_APPOINTMENT", "Appointment", String.valueOf(saved.getId()),
                    "Booked for patient " + patientId + " with doctor " + doctorId);
        }

        return saved;
    }

    // Add this method to SecretaryService
    public List<Patient> searchPatients(String query) {
        String lowerQuery = query.toLowerCase();
        return mockDatabase.findAllPatients().stream()
                .filter(p -> p.getName().toLowerCase().contains(lowerQuery) ||
                        p.getSurname().toLowerCase().contains(lowerQuery) ||
                        (p.getFiscalCode() != null && p.getFiscalCode().toLowerCase().contains(lowerQuery)))
                .collect(Collectors.toList());
    }

    // ========== NEW METHODS FOR APPOINTMENT MANAGEMENT ==========

    public List<Appointment> getAllAppointments() {
        // In MockDatabase, we need to add this method
        return mockDatabase.findAllAppointments();
    }

    public List<Patient> getAllPatients() {
        return mockDatabase.findAllPatients();
    }

    public List<MedicalSpecialist> getAllMedicalSpecialists() {
        return mockDatabase.findAllEmployees().stream()
                .filter(e -> e instanceof MedicalSpecialist && e.isActive())
                .map(e -> (MedicalSpecialist) e)
                .collect(Collectors.toList());
    }

    public Appointment bookAppointmentForPatient(int patientId, int specialistId, LocalDateTime dateTime) {
        Patient patient = mockDatabase.findPatientById(patientId);
        MedicalSpecialist specialist = mockDatabase.findMedicalSpecialistById(specialistId);

        if (patient == null) throw new IllegalArgumentException("Patient not found");
        if (specialist == null) throw new IllegalArgumentException("Specialist not found");

        // Check if slot is available
        boolean slotAvailable = mockDatabase.findAllAppointments().stream()
                .noneMatch(a -> a.getDoctor() != null &&
                        a.getDoctor().getId() == specialistId &&
                        a.getDateTime().equals(dateTime));

        if (!slotAvailable) {
            throw new IllegalArgumentException("Time slot not available");
        }

        Appointment appointment = new Appointment(dateTime, 30, "Booked by secretary", patient, specialist);
        appointment.setStatus("CONFIRMED");

        Appointment saved = mockDatabase.saveAppointment(appointment);

        auditService.log("BOOK_APPOINTMENT_BY_SECRETARY", "Appointment", String.valueOf(saved.getId()),
                "Secretary booked for patient " + patientId + " with doctor " + specialistId);

        return saved;
    }

    public void cancelAppointment(int appointmentId) {
        Appointment appointment = mockDatabase.findAppointmentById(appointmentId);
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment not found");
        }
        appointment.setStatus("CANCELLED");
        mockDatabase.saveAppointment(appointment);

        auditService.log("CANCEL_APPOINTMENT_BY_SECRETARY", "Appointment", String.valueOf(appointmentId),
                "Cancelled by secretary");
    }

    public List<Appointment> getPatientAppointments(int patientId) {
        return mockDatabase.findAllAppointments().stream()
                .filter(a -> a.getPatient() != null && a.getPatient().getId() == patientId)
                .collect(Collectors.toList());
    }

    public void rescheduleAppointment(int appointmentId, LocalDateTime newDateTime) {
        Appointment appointment = mockDatabase.findAppointmentById(appointmentId);
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment not found");
        }

        // Check if new slot is available
        boolean slotAvailable = mockDatabase.findAllAppointments().stream()
                .noneMatch(a -> a.getDoctor() != null &&
                        a.getDoctor().getId() == appointment.getDoctor().getId() &&
                        a.getDateTime().equals(newDateTime) &&
                        a.getId() != appointmentId);

        if (!slotAvailable) {
            throw new IllegalArgumentException("Selected time slot is not available");
        }

        LocalDateTime oldDateTime = appointment.getDateTime();
        appointment.setDateTime(newDateTime);
        appointment.setStatus("RESCHEDULED");
        mockDatabase.saveAppointment(appointment);

        auditService.log("RESCHEDULE_APPOINTMENT_BY_SECRETARY", "Appointment", String.valueOf(appointmentId),
                "Rescheduled from " + oldDateTime + " to " + newDateTime);
    }


}


