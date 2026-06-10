package com.nvivx.vixhealthsystem.service.resources;

import com.nvivx.vixhealthsystem.model.facility.Room;
import com.nvivx.vixhealthsystem.model.facility.InternationRoom;
import com.nvivx.vixhealthsystem.model.person.Patient;
import com.nvivx.vixhealthsystem.repository.RoomRepository;
import com.nvivx.vixhealthsystem.repository.PatientRepository;
import com.nvivx.vixhealthsystem.service.AuditService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class RoomService {

    private final RoomRepository roomRepository;
    private final PatientRepository patientRepository;
    private final AuditService auditService;

    public RoomService(RoomRepository roomRepository, PatientRepository patientRepository, AuditService auditService) {
        this.roomRepository = roomRepository;
        this.patientRepository = patientRepository;
        this.auditService = auditService;
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Room findById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + id));
    }

    public List<InternationRoom> getAvailableRooms() {
        return roomRepository.findAvailableInpatientRooms();
    }

    public List<InternationRoom> getAllInpatientRooms() {
        return roomRepository.findInpatientRooms();
    }

    @Transactional
    public void admitPatient(Long patientId, Long roomId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        Room room = findById(roomId);

        if (!(room instanceof InternationRoom)) {
            throw new RuntimeException("Room is not an inpatient room");
        }

        InternationRoom inpatientRoom = (InternationRoom) room;

        if (inpatientRoom.getNFreeBeds() <= 0) {
            throw new RuntimeException("No available beds in this room");
        }

        inpatientRoom.addPatient(patient);
        roomRepository.save(inpatientRoom);

        auditService.log("ADMIT_PATIENT", "Patient", String.valueOf(patientId),
                "Patient admitted to room: " + room.getNumber());
    }

    @Transactional
    public void dismissPatient(Long patientId, Long roomId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        Room room = findById(roomId);

        if (!(room instanceof InternationRoom)) {
            throw new RuntimeException("Room is not an inpatient room");
        }

        InternationRoom inpatientRoom = (InternationRoom) room;
        inpatientRoom.removePatient(patient);
        roomRepository.save(inpatientRoom);

        auditService.log("DISMISS_PATIENT", "Patient", String.valueOf(patientId),
                "Patient dismissed from room: " + room.getNumber());
    }

    public int getAvailableBedCount() {
        return roomRepository.countAvailableBeds();
    }
}