package com.nvivx.vixhealthsystem.service.core;

import com.nvivx.vixhealthsystem.model.person.employee.Employee;
import com.nvivx.vixhealthsystem.model.enums.Role;
import com.nvivx.vixhealthsystem.repository.EmployeeRepository;
import com.nvivx.vixhealthsystem.service.AuditService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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

    public Optional<Employee> findByUsername(String username) {
        // Note: Your Employee entity needs username field if not present
        return employeeRepository.findByUsername(username);
    }

    public List<Employee> findAllEmployees() {
        return employeeRepository.findAll();
    }

    public List<Employee> findByRole(Role role) {
        return employeeRepository.findByRole(role);
    }

    public List<Employee> findByDepartment(Long departmentId) {
        return employeeRepository.findByDepartmentId(departmentId);
    }

    @Transactional
    public Employee createEmployee(Employee employee) {
        Employee saved = employeeRepository.save(employee);
        auditService.log("CREATE_EMPLOYEE", "Employee", String.valueOf(saved.getId()),
                "Created employee: " + employee.getName() + " " + employee.getSurname());
        return saved;
    }

    @Transactional
    public Employee updateEmployee(Long id, Employee updatedData) {
        Employee employee = findById(id);

        if (updatedData.getName() != null) employee.setName(updatedData.getName());
        if (updatedData.getSurname() != null) employee.setSurname(updatedData.getSurname());
        if (updatedData.getEmail() != null) employee.setEmail(updatedData.getEmail());
        if (updatedData.getPhoneNumber() != null) employee.setPhoneNumber(updatedData.getPhoneNumber());

        Employee saved = employeeRepository.save(employee);
        auditService.log("UPDATE_EMPLOYEE", "Employee", String.valueOf(id), "Updated employee details");
        return saved;
    }

    @Transactional
    public void changeRole(Long id, Role newRole) {
        Employee employee = findById(id);
        employee.setRole(newRole);
        employeeRepository.save(employee);
        auditService.log("CHANGE_ROLE", "Employee", String.valueOf(id), "Role changed to: " + newRole);
    }

    @Transactional
    public void deactivateEmployee(Long id) {
        Employee employee = findById(id);
        employee.setActive(false);
        employeeRepository.save(employee);
        auditService.log("DEACTIVATE_EMPLOYEE", "Employee", String.valueOf(id), "Employee deactivated");
    }

    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = findById(id);
        employeeRepository.delete(employee);
        auditService.log("DELETE_EMPLOYEE", "Employee", String.valueOf(id), "Employee deleted");
    }

    public long getActiveEmployeeCount() {
        return employeeRepository.countByActiveTrue();
    }

    public long getTotalEmployeeCount() {
        return employeeRepository.count();
    }
}