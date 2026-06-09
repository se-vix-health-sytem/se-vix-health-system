package com.nvivx.vixhealthsystem.repository;

import com.nvivx.vixhealthsystem.model.enums.MachineStatus;
import com.nvivx.vixhealthsystem.model.resource.Machinery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MachineryRepository
        extends JpaRepository<Machinery, Long> {

    /**
     * Returns all machines with the specified status.
     *
     * @param status machine status
     * @return matching machines
     */
    List<Machinery> findByStatus(MachineStatus status);

    /**
     * Returns all machines installed in a specialized room.
     *
     * @param roomId specialized room id
     * @return machines in the room
     */
    List<Machinery> findBySpecializedRoomId(Long roomId);

    /**
     * Returns all faulty machines.
     *
     * Equivalent to:
     * findByStatus(MachineStatus.FAULTY)
     *
     * @return faulty machines
     */
    List<Machinery> findByStatusOrderByNameAsc(MachineStatus status);
}