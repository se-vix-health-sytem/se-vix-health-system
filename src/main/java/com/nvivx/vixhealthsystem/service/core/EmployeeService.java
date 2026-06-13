package com.nvivx.vixhealthsystem.service.core;

import com.nvivx.vixhealthsystem.model.facility.Department;
import com.nvivx.vixhealthsystem.model.person.employee.Employee;
import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import com.nvivx.vixhealthsystem.model.person.employee.Secretary;
import com.nvivx.vixhealthsystem.model.person.employee.Technician;
import com.nvivx.vixhealthsystem.model.person.employee.Buyer;
import com.nvivx.vixhealthsystem.model.person.employee.StaffManager;
import com.nvivx.vixhealthsystem.repository.DepartmentRepository;
import com.nvivx.vixhealthsystem.repository.EmployeeRepository;
import com.nvivx.vixhealthsystem.service.AuditService;
import com.nvivx.vixhealthsystem.service.DevCredentialStore;
import com.nvivx.vixhealthsystem.service.integration.FirebaseAuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final AuditService auditService;
    private final FirebaseAuthService firebaseAuthService;
    private final DevCredentialStore devCredentialStore;

    public EmployeeService(
            EmployeeRepository employeeRepository,
            DepartmentRepository departmentRepository,
            AuditService auditService,
            FirebaseAuthService firebaseAuthService,
            DevCredentialStore devCredentialStore
    ) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.auditService = auditService;
        this.firebaseAuthService = firebaseAuthService;
        this.devCredentialStore = devCredentialStore;
    }

    public List<Department> findAllDepartments() {
        return departmentRepository.findAll();
    }

    public Department findDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
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

    public Employee findByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }

    public Employee findByFirebaseUid(String firebaseUid) {
        return employeeRepository.findByFirebaseUid(firebaseUid);
    }

    public List<Employee> findBySurname(String surname) {
        return employeeRepository.findBySurname(surname);
    }

    public List<Employee> findByDepartmentId(Long departmentId) {
        return employeeRepository.findByDepartmentId(departmentId);
    }

    public List<Employee> findByRole(Class<? extends Employee> employeeType) {
        return employeeRepository.findAll().stream()
                .filter(employeeType::isInstance)
                .collect(Collectors.toList());
    }

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

    public long getTotalEmployeeCount() {
        return employeeRepository.count();
    }

    public long getActiveEmployeeCount() {
        return employeeRepository.count();
    }

    @Transactional
    public Employee createEmployee(Employee employee) {
        if (employee.getEmail() == null || employee.getEmail().isBlank()) {
            throw new RuntimeException("Employee email is required to create Firebase account");
        }

        try {
            String temporaryPassword = "ChangeMe123!";

            String firebaseUid = firebaseAuthService.createUser(
                    employee.getEmail(),
                    temporaryPassword
            );

            employee.setFirebaseUid(firebaseUid);

            System.out.println("=== FIREBASE EMPLOYEE ACCOUNT CREATED ===");
            System.out.println("Employee: " + employee.getName() + " " + employee.getSurname());
            System.out.println("Email: " + employee.getEmail());
            System.out.println("Temporary password: " + temporaryPassword);
            System.out.println("Firebase UID: " + firebaseUid);
            System.out.println("=========================================");

            devCredentialStore.store(
                    employee.getEmail(),
                    employee.getName() + " " + employee.getSurname(),
                    employee.getClass().getSimpleName(),
                    temporaryPassword
            );

        } catch (Exception e) {
            String rootCause = (e.getCause() != null) ? e.getCause().getMessage() : e.getMessage();
            throw new RuntimeException("Firebase error: " + rootCause, e);
        }

        Employee saved = employeeRepository.save(employee);

        auditService.log(
                "CREATE_EMPLOYEE",
                "Employee",
                String.valueOf(saved.getId()),
                "Created employee: " + employee.getName() + " "
                        + employee.getSurname()
                        + " (Type: "
                        + employee.getClass().getSimpleName()
                        + ")"
        );

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

        auditService.log(
                "UPDATE_EMPLOYEE",
                "Employee",
                String.valueOf(id),
                "Updated employee details"
        );

        return saved;
    }

    /**
     * Generates a Firebase password-reset link for the employee (simulated email).
     * The link is printed to console and stored in the dev credential board so it
     * is never lost during a demo session.
     */
    public void requestEmployeePasswordReset(Long employeeId) {
        Employee employee = findById(employeeId);

        if (employee.getEmail() == null || employee.getEmail().isBlank()) {
            throw new RuntimeException("Employee does not have an email address");
        }

        try {
            String resetLink =
                    firebaseAuthService.generatePasswordResetLink(employee.getEmail());

            System.out.println("=== PASSWORD RESET DEMO ===");
            System.out.println("Employee: " + employee.getName() + " " + employee.getSurname());
            System.out.println("Employee email: " + employee.getEmail());
            System.out.println("Reset link: " + resetLink);
            System.out.println("===========================");

            // Mark on the dev board that this account's password is no longer the default
            devCredentialStore.markResetTriggered(employee.getEmail());

            auditService.log(
                    "REQUEST_PASSWORD_RESET",
                    "Employee",
                    String.valueOf(employee.getId()),
                    "Password reset requested for employee: "
                            + employee.getName() + " "
                            + employee.getSurname()
            );

        } catch (Exception e) {
            throw new RuntimeException(
                    "Unable to generate password reset link",
                    e
            );
        }
    }

    @Transactional
    public void changeDepartment(Long id, Long newDepartmentId) {
        Employee employee = findById(id);

        auditService.log(
                "REQUEST_DEPARTMENT_CHANGE",
                "Employee",
                String.valueOf(id),
                "Department change requested to ID: " + newDepartmentId
        );
    }

    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = findById(id);
        String employeeInfo = employee.getName() + " " + employee.getSurname();

        if (employee.getFirebaseUid() != null) {
            try {
                firebaseAuthService.deleteUser(employee.getFirebaseUid());
            } catch (Exception e) {
                throw new RuntimeException(
                        "Unable to delete Firebase account",
                        e
                );
            }
        }

        if (employee.getEmail() != null) {
            devCredentialStore.remove(employee.getEmail());
        }

        employeeRepository.delete(employee);

        auditService.log(
                "DELETE_EMPLOYEE",
                "Employee",
                String.valueOf(id),
                "Deleted employee: " + employeeInfo
        );
    }


}