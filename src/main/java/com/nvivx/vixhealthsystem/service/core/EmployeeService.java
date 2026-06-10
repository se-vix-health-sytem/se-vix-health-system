package com.nvivx.vixhealthsystem.service.core;

import com.nvivx.vixhealthsystem.model.person.employee.Employee;
import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import com.nvivx.vixhealthsystem.model.person.employee.Secretary;
import com.nvivx.vixhealthsystem.model.person.employee.Technician;
import com.nvivx.vixhealthsystem.model.person.employee.Buyer;
import com.nvivx.vixhealthsystem.model.person.employee.StaffManager;
import com.nvivx.vixhealthsystem.repository.EmployeeRepository;
import com.nvivx.vixhealthsystem.service.AuditService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final AuditService auditService;

    public EmployeeService(EmployeeRepository employeeRepository, AuditService auditService) {
        this.employeeRepository = employeeRepository;
        this.auditService = auditService;
    }

    public Employee findById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
    }

    public Optional<Employee> findByIdOptional(Long id) {
        return employeeRepository.findById(id);
    }

    public List<Employee> findAllEmployees() {
        return employeeRepository.findAll();
    }

    /**
     * Find employee by email (unique in database)
     */
    public Employee findByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }

    /**
     * Find employees by surname
     */
    public List<Employee> findBySurname(String surname) {
        return employeeRepository.findBySurname(surname);
    }

    /**
     * Find employees by department ID
     */
    public List<Employee> findByDepartmentId(Long departmentId) {
        return employeeRepository.findByDepartmentId(departmentId);
    }

    /**
     * Find employees by role using instanceof checks
     * Since database uses discriminator column 'type', we filter in Java
     */
    public List<Employee> findByRole(Class<? extends Employee> employeeType) {
        return employeeRepository.findAll().stream()
                .filter(employeeType::isInstance)
                .collect(Collectors.toList());
    }

    /**
     * Convenience methods for specific employee types
     */
    public List<MedicalSpecialist> findAllMedicalSpecialists() {
        return employeeRepository.findAll().stream()
                .filter(e -> e instanceof MedicalSpecialist)
                .map(e -> (MedicalSpecialist) e)
                .collect(Collectors.toList());
    }

    public List<Secretary> findAllSecretaries() {
        return employeeRepository.findAll().stream()
                .filter(e -> e instanceof Secretary)
                .map(e -> (Secretary) e)
                .collect(Collectors.toList());
    }

    public List<Technician> findAllTechnicians() {
        return employeeRepository.findAll().stream()
                .filter(e -> e instanceof Technician)
                .map(e -> (Technician) e)
                .collect(Collectors.toList());
    }

    public List<Buyer> findAllBuyers() {
        return employeeRepository.findAll().stream()
                .filter(e -> e instanceof Buyer)
                .map(e -> (Buyer) e)
                .collect(Collectors.toList());
    }

    public List<StaffManager> findAllStaffManagers() {
        return employeeRepository.findAll().stream()
                .filter(e -> e instanceof StaffManager)
                .map(e -> (StaffManager) e)
                .collect(Collectors.toList());
    }

    /**
     * Get total employee count
     */
    public long getTotalEmployeeCount() {
        return employeeRepository.count();
    }

    /**
     * Note: 'active' field does not exist in database.
     * All employees are considered active.
     * If needed in the future, add 'active' column to Employees table.
     */
    public long getActiveEmployeeCount() {
        // Since no 'active' field, return total count
        return employeeRepository.count();
    }

    @Transactional
    public Employee createEmployee(Employee employee) {
        Employee saved = employeeRepository.save(employee);
        auditService.log("CREATE_EMPLOYEE", "Employee", String.valueOf(saved.getId()),
                "Created employee: " + employee.getName() + " " + employee.getSurname() +
                        " (Type: " + employee.getClass().getSimpleName() + ")");
        return saved;
    }

    @Transactional
    public Employee updateEmployee(Long id, Employee updatedData) {
        Employee employee = findById(id);

        if (updatedData.getName() != null) employee.setName(updatedData.getName());
        if (updatedData.getSurname() != null) employee.setSurname(updatedData.getSurname());
        if (updatedData.getEmail() != null) employee.setEmail(updatedData.getEmail());
        if (updatedData.getPhoneNumber() != null) employee.setPhoneNumber(updatedData.getPhoneNumber());
        if (updatedData.getBirthDate() != null) employee.setBirthDate(updatedData.getBirthDate());
        if (updatedData.getBirthPlace() != null) employee.setBirthPlace(updatedData.getBirthPlace());
        if (updatedData.getGender() != '\0') employee.setGender(updatedData.getGender());
        if (updatedData.getHireDate() != null) employee.setHireDate(updatedData.getHireDate());
        if (updatedData.getDepartment() != null) employee.setDepartment(updatedData.getDepartment());

        Employee saved = employeeRepository.save(employee);
        auditService.log("UPDATE_EMPLOYEE", "Employee", String.valueOf(id), "Updated employee details");
        return saved;
    }

    /**
     * Note: Role change is handled by changing the employee's department
     * and potentially creating a new employee record with different type.
     * Due to Single Table Inheritance, type cannot be changed easily.
     * For role changes, consider creating a new employee record.
     */
    @Transactional
    public void changeDepartment(Long id, Long newDepartmentId) {
        Employee employee = findById(id);
        // Department will be fetched by DepartmentService
        // For now, we just log that department change is requested
        auditService.log("REQUEST_DEPARTMENT_CHANGE", "Employee", String.valueOf(id),
                "Department change requested to ID: " + newDepartmentId);
    }

    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = findById(id);
        String employeeInfo = employee.getName() + " " + employee.getSurname();
        employeeRepository.delete(employee);
        auditService.log("DELETE_EMPLOYEE", "Employee", String.valueOf(id),
                "Deleted employee: " + employeeInfo);
    }
}