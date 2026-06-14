package com.nvivx.vixhealthsystem.repository;

import com.nvivx.vixhealthsystem.model.facility.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @brief JPA repository for {@link Room} entities.
 *
 * Used by the technician and secretary workflows to inspect or assign rooms
 * within a medical facility.
 *
 * @see com.nvivx.vixhealthsystem.model.facility.Room
 * @see MedicalFacilityRepository
 */
@Repository
public interface RoomRepository
        extends JpaRepository<Room, Long> {

    /**
     * Returns all rooms belonging to a facility.
     *
     * @param facilityId medical facility id
     * @return facility rooms
     */
    List<Room> findByMedicalFacilityId(Long facilityId);

    /**
     * Finds a room by its number.
     *
     * @param number room number
     * @return matching rooms
     */
    List<Room> findByNumber(String number);
}