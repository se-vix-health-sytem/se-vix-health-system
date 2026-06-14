package com.nvivx.vixhealthsystem.repository;

import com.nvivx.vixhealthsystem.model.person.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @brief JPA repository for {@link Employee} entities.
 *
 * Supports staff management use cases including lookup by name, email, Firebase UID,
 * department, and concrete sub-type (e.g. MedicalSpecialist, Buyer).
 *
 * @see com.nvivx.vixhealthsystem.model.person.employee.Employee
 * @see DepartmentRepository
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /** @brief Finds all employees with the given surname. */
    List<Employee> findBySurname(String surname);

    /** @brief Finds employees matching both given name and surname exactly. */
    List<Employee> findByNameAndSurname(String name, String surname);

    /**
     * Finds an employee by their email address.
     *
     * @param email unique email; returns {@code null} if not found
     * @return the matching employee, or {@code null}
     */
    Employee findByEmail(String email);

    /** @brief Returns all employees assigned to the specified department. */
    List<Employee> findByDepartmentId(Long departmentId);

    /**
     * Finds an employee by their Firebase Authentication UID.
     *
     * Used during login to map a Firebase token to an application user.
     *
     * @param firebaseUid the UID from the Firebase ID token
     * @return the matching employee, or {@code null} if the UID is unknown
     */
    Employee findByFirebaseUid(String firebaseUid);

    /**
     * Returns all employees whose JPA discriminator matches the given sub-type.
     *
     * Allows filtering the polymorphic Employee hierarchy without casting in service code,
     * e.g. {@code findByEmployeeType(MedicalSpecialist.class)}.
     *
     * @param type the concrete sub-class to filter by
     * @return employees of the requested sub-type
     */
    @Query("SELECT e FROM Employee e WHERE TYPE(e) = :type")
    List<Employee> findByEmployeeType(@Param("type") Class<?> type);
}