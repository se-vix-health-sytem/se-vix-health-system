package com.nvivx.vixhealthsystem.repository;

import com.nvivx.vixhealthsystem.model.person.employee.Buyer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuyerRepository extends JpaRepository<Buyer, Long> {

    Buyer findByEmail(String email);

    Buyer findBySurname(String surname);
}