package com.nvivx.vixhealthsystem.repository;

import com.nvivx.vixhealthsystem.model.resource.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository
        extends JpaRepository<Resource, Long> {

    /**
     * Finds resources by name.
     *
     * @param name resource name
     * @return matching resources
     */
    List<Resource> findByName(String name);

    /**
     * Finds resources whose name contains the specified text.
     *
     * @param name partial resource name
     * @return matching resources
     */
    List<Resource> findByNameContainingIgnoreCase(String name);
}