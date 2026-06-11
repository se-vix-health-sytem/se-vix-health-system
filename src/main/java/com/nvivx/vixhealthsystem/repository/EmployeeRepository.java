package com.nvivx.vixhealthsystem.repository;

import com.nvivx.vixhealthsystem.model.person.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findBySurname(String surname);

    List<Employee> findByNameAndSurname(String name, String surname);

    Employee findByEmail(String email);

    List<Employee> findByDepartmentId(Long departmentId);

    Employee findByFirebaseUid(String firebaseUid);

    @Query("SELECT e FROM Employee e WHERE TYPE(e) = :type")
    List<Employee> findByEmployeeType(@Param("type") Class<?> type);
}