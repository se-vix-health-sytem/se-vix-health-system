package com.nvivx.vixhealthsystem.repository;

import com.nvivx.vixhealthsystem.model.person.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findBySurname(String surname);

    List<Employee> findByNameAndSurname(String name, String surname);

    Employee findByEmail(String email);

    List<Employee> findByDepartmentId(Long departmentId);
}