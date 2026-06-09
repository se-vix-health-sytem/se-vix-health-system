package com.nvivx.vixhealthsystem.repository;

import com.nvivx.vixhealthsystem.model.facility.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

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