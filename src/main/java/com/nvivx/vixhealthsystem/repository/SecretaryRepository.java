package com.nvivx.vixhealthsystem.repository;

import com.nvivx.vixhealthsystem.model.person.employee.Secretary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecretaryRepository extends JpaRepository<Secretary, Long> {

    Secretary findByEmail(String email);

    Secretary findBySurname(String surname);
}