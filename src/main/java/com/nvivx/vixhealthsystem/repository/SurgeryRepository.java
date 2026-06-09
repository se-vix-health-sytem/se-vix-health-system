package com.nvivx.vixhealthsystem.repository;

import com.nvivx.vixhealthsystem.model.medical.Surgery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurgeryRepository
        extends JpaRepository<Surgery, Long> {

    /**
     * Returns all surgeries associated with a medical record.
     *
     * @param medicalRecordId medical record id
     * @return surgeries of the medical record
     */
    List<Surgery> findByMedicalRecordId(Long medicalRecordId);

    /**
     * Returns all surgeries performed in a specialized room.
     *
     * @param specializedRoomId room id
     * @return surgeries performed in the room
     */
    List<Surgery> findBySpecializedRoomId(Long specializedRoomId);

    /**
     * Finds surgeries by name.
     *
     * @param name surgery name
     * @return matching surgeries
     */
    List<Surgery> findByName(String name);
}