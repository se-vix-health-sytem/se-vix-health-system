package com.nvivx.vixhealthsystem.repository;

import com.nvivx.vixhealthsystem.model.resource.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @brief JPA repository for {@link Resource} entities (medical supplies and consumables).
 *
 * Used by the buyer and technician workflows to search the catalogue and check
 * stock levels.  For aggregated quantity data across storages, see
 * {@link StorageRepository#findTotalQuantityPerResource()}.
 *
 * @see com.nvivx.vixhealthsystem.model.resource.Resource
 * @see StorageRepository
 */
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