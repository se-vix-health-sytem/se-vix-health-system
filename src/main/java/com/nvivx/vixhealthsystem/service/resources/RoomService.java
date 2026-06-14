package com.nvivx.vixhealthsystem.service.resources;

import com.nvivx.vixhealthsystem.model.facility.Room;
import com.nvivx.vixhealthsystem.model.facility.InternationRoom;
import com.nvivx.vixhealthsystem.model.person.Patient;
import com.nvivx.vixhealthsystem.model.person.employee.Secretary;
import com.nvivx.vixhealthsystem.repository.RoomRepository;
import com.nvivx.vixhealthsystem.repository.PatientRepository;
import com.nvivx.vixhealthsystem.service.AuditService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @brief Manages patient admission and dismissal across inpatient rooms (UC22 — Triage,
 *        UC23 — Patient Dismissal) and provides bed-availability statistics for the dashboard.
 *
 * Annotated {@code @Transactional(readOnly=true)} at the class level; write methods override
 * with {@code @Transactional}.
 *
 * Room polymorphism: the repository stores all {@link Room} subtypes; only
 * {@link InternationRoom} instances support patient admission.  Type-guard checks are applied
 * in {@link #admitPatient} and {@link #dismissPatient} to provide clear error messages.
 *
 * @see com.nvivx.vixhealthsystem.model.facility.InternationRoom
 * @see AuditService
 */
@Service
@Transactional(readOnly = true)
public class RoomService {

    // =========================================================
    // FIELDS
    // =========================================================

    private final RoomRepository roomRepository;
    private final PatientRepository patientRepository;
    private final AuditService auditService;

    // =========================================================
    // CONSTRUCTORS
    // =========================================================

    /**
     * Constructs the service with all required repositories.
     *
     * @param roomRepository     Persistence layer for all {@link Room} subtypes.
     * @param patientRepository  Needed to load patients by ID during admission/dismissal.
     * @param auditService       Records every bed assignment change (NFR02 — traceability).
     */
    public RoomService(RoomRepository roomRepository,
                       PatientRepository patientRepository,
                       AuditService auditService) {
        this.roomRepository = roomRepository;
        this.patientRepository = patientRepository;
        this.auditService = auditService;
    }

    // =========================================================
    // READ OPERATIONS
    // =========================================================

    /** @brief Returns all rooms of all types in the facility. */
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    /**
     * Looks up a room by primary key, throwing when absent.
     *
     * @param id  Room primary key.
     * @return    The matching {@link Room}; never {@code null}.
     * @throws RuntimeException When no room with the given ID exists.
     */
    public Room findById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + id));
    }

    /** @brief Returns only the {@link InternationRoom} instances (i.e., rooms that have beds). */
    public List<InternationRoom> getAllInpatientRooms() {
        return roomRepository.findAll().stream()
                .filter(room -> room instanceof InternationRoom)
                .map(room -> (InternationRoom) room)
                .collect(Collectors.toList());
    }

    /**
     * Get available inpatient rooms (rooms with at least one free bed)
     */
    public List<InternationRoom> getAvailableRooms() {
        return getAllInpatientRooms().stream()
                .filter(room -> room.getNFreeBeds() > 0)
                .collect(Collectors.toList());
    }

    /**
     * Get total number of available beds across all inpatient rooms
     */
    public int getTotalAvailableBeds() {
        return getAllInpatientRooms().stream()
                .mapToInt(InternationRoom::getNFreeBeds)
                .sum();
    }

    /**
     * Get occupied beds count
     */
    public int getOccupiedBedsCount() {
        return getAllInpatientRooms().stream()
                .mapToInt(room -> room.getPatients().size())
                .sum();
    }

    /**
     * Get total beds across all inpatient rooms
     */
    public int getTotalBeds() {
        return getAllInpatientRooms().stream()
                .mapToInt(InternationRoom::getTotalNBeds)
                .sum();
    }

    /**
     * Admit a patient to a room via a secretary (UC22 - Triage).
     * Uses the Secretary domain method, which delegates to InternationRoom domain logic.
     */
    @Transactional
    public void admitPatient(Secretary secretary, Long patientId, Long roomId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + patientId));

        Room room = findById(roomId);

        if (!(room instanceof InternationRoom)) {
            throw new RuntimeException("Room " + roomId + " is not an inpatient room");
        }

        InternationRoom inpatientRoom = (InternationRoom) room;

        if (inpatientRoom.getNFreeBeds() <= 0) {
            throw new RuntimeException("No available beds in room " + inpatientRoom.getNumber());
        }

        try {
            // Domain: secretary assigns patient to room via model methods
            secretary.setPatientInRoom(inpatientRoom, patient);
            roomRepository.save(inpatientRoom);

            auditService.log("ADMIT_PATIENT", "Patient", String.valueOf(patientId),
                    "Patient admitted to room: " + inpatientRoom.getNumber());
        } catch (Exception e) {
            throw new RuntimeException("Failed to admit patient: " + e.getMessage(), e);
        }
    }

    /**
     * Dismiss a patient from a room via a secretary (UC23 - Patient Dismissal).
     * Uses the Secretary domain method, which delegates to InternationRoom domain logic.
     */
    @Transactional
    public void dismissPatient(Secretary secretary, Long patientId, Long roomId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + patientId));

        Room room = findById(roomId);

        if (!(room instanceof InternationRoom)) {
            throw new RuntimeException("Room " + roomId + " is not an inpatient room");
        }

        InternationRoom inpatientRoom = (InternationRoom) room;

        if (!inpatientRoom.hasPatient(patient)) {
            throw new RuntimeException("Patient " + patientId + " is not in room " + inpatientRoom.getNumber());
        }

        try {
            // Domain: secretary dismisses patient from room via model methods
            secretary.dismissPatient(inpatientRoom, patient);
            roomRepository.save(inpatientRoom);

            auditService.log("DISMISS_PATIENT", "Patient", String.valueOf(patientId),
                    "Patient dismissed from room: " + inpatientRoom.getNumber());
        } catch (Exception e) {
            throw new RuntimeException("Failed to dismiss patient: " + e.getMessage(), e);
        }
    }

    /**
     * Find which room a patient is currently in
     */
    public InternationRoom findPatientRoom(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found: " + patientId));

        return getAllInpatientRooms().stream()
                .filter(room -> room.hasPatient(patient))
                .findFirst()
                .orElse(null);
    }

    /**
     * Check if a patient is currently admitted
     */
    public boolean isPatientAdmitted(Long patientId) {
        return findPatientRoom(patientId) != null;
    }
}