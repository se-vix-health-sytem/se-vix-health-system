package com.nvivx.vixhealthsystem.repository;

import com.nvivx.vixhealthsystem.model.person.employee.Technician;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TechnicianRepository extends JpaRepository<Technician, Long> {

    Technician findByEmail(String email);

    Technician findBySurname(String surname);
}