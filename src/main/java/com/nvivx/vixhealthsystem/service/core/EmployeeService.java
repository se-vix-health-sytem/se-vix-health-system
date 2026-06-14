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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @brief Central service for employee lifecycle management — creation, lookup, update, deletion,
 *        and Firebase account synchronisation.
 *
 * Annotated {@code @Transactional(readOnly=true)} at the class level so that all read
 * operations benefit from connection-pool optimisations.  Individual write operations
 * override with {@code @Transactional}.
 *
 * Every mutating action is recorded via {@link AuditService} to satisfy NFR02 (traceability).
 * Firebase accounts are provisioned alongside the database record so that employees can
 * log in immediately after creation.
 *
 * @see FirebaseAuthService
 * @see AuditService
 * @see DevCredentialStore
 * @see com.nvivx.vixhealthsystem.model.person.employee.Employee
 */
@Service
@Transactional(readOnly = true)
public class EmployeeService {

    // =========================================================
    // FIELDS
    // =========================================================

    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final AuditService auditService;
    /** Manages Firebase Authentication accounts linked to each employee. */
    private final FirebaseAuthService firebaseAuthService;
    /** DEV-ONLY store that keeps plaintext credentials visible on the demo login page. */
    private final DevCredentialStore devCredentialStore;

    // =========================================================
    // CONSTRUCTORS
    // =========================================================

    /**
     * Constructs the service with all required collaborators.
     *
     * @param employeeRepository   Persistence layer for {@link Employee} entities.
     * @param departmentRepository Needed to validate and resolve department assignments.
     * @param auditService         Records every mutating action for traceability (NFR02).
     * @param firebaseAuthService  Provisions and removes Firebase Authentication accounts.
     * @param devCredentialStore   DEV-ONLY: persists temporary passwords for the demo board.
     */
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

    // =========================================================
    // READ OPERATIONS — DEPARTMENTS
    // =========================================================

    /** @brief Returns all departments; used to populate department selectors in forms. */
    public List<Department> findAllDepartments() {
        return departmentRepository.findAll();
    }

    /**
     * Looks up a department by ID, throwing when absent.
     *
     * @param id  Department primary key.
     * @return    The matching {@link Department}; never {@code null}.
     * @throws RuntimeException When no department with the given ID exists.
     */
    public Department findDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
    }

    // =========================================================
    // READ OPERATIONS — EMPLOYEES
    // =========================================================

    /**
     * Looks up an employee by primary key, throwing when absent.
     *
     * @param id  Employee primary key.
     * @return    The matching {@link Employee}; never {@code null}.
     * @throws RuntimeException When no employee with the given ID exists.
     */
    public Employee findById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
    }

    /**
     * Non-throwing alternative to {@link #findById(Long)} for callers that handle absence.
     *
     * @param id  Employee primary key.
     * @return    {@link Optional} containing the employee, or empty if absent.
     */
    public Optional<Employee> findByIdOptional(Long id) {
        return employeeRepository.findById(id);
    }

    /** @brief Returns all employees across all roles and departments. */
    public List<Employee> findAllEmployees() {
        return employeeRepository.findAll();
    }

    /**
     * Finds an employee by their login email address.
     *
     * @param email  The employee's email; must be unique in the system.
     * @return       The matching employee, or {@code null} if not found.
     */
    public Employee findByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }

    /**
     * Resolves an employee from their Firebase UID, used during authentication.
     *
     * @param firebaseUid  The UID issued by Firebase Authentication after login.
     * @return             The matching employee, or {@code null} if not found.
     */
    public Employee findByFirebaseUid(String firebaseUid) {
        return employeeRepository.findByFirebaseUid(firebaseUid);
    }

    /**
     * Returns all employees with the given surname.
     *
     * @param surname  Exact surname match (case-sensitivity is repository-dependent).
     * @return         Non-null list; empty if none found.
     */
    public List<Employee> findBySurname(String surname) {
        return employeeRepository.findBySurname(surname);
    }

    /**
     * Returns all employees assigned to the specified department.
     *
     * @param departmentId  Primary key of the target department.
     * @return              Non-null list; empty if the department has no employees.
     */
    public List<Employee> findByDepartmentId(Long departmentId) {
        return employeeRepository.findByDepartmentId(departmentId);
    }

    /**
     * Returns all employees that are instances of the given subtype.
     *
     * Used as a generic alternative to the typed {@code findAll*} helpers below when
     * the caller already holds a {@link Class} token.
     *
     * @param employeeType  Concrete employee subclass (e.g., {@code MedicalSpecialist.class}).
     * @return              Non-null list of employees that pass the instanceof check.
     */
    public List<Employee> findByRole(Class<? extends Employee> employeeType) {
        return employeeRepository.findAll().stream()
                .filter(employeeType::isInstance)
                .collect(Collectors.toList());
    }

    /** @brief Returns all {@link MedicalSpecialist} employees in the system. */
    public List<MedicalSpecialist> findAllMedicalSpecialists() {
        return employeeRepository.findAll().stream()
                .filter(e -> e instanceof MedicalSpecialist)
                .map(e -> (MedicalSpecialist) e)
                .collect(Collectors.toList());
    }

    /** @brief Returns all {@link Secretary} employees in the system. */
    public List<Secretary> findAllSecretaries() {
        return employeeRepository.findAll().stream()
                .filter(e -> e instanceof Secretary)
                .map(e -> (Secretary) e)
                .collect(Collectors.toList());
    }

    /** @brief Returns all {@link Technician} employees in the system. */
    public List<Technician> findAllTechnicians() {
        return employeeRepository.findAll().stream()
                .filter(e -> e instanceof Technician)
                .map(e -> (Technician) e)
                .collect(Collectors.toList());
    }

    /** @brief Returns all {@link Buyer} employees in the system. */
    public List<Buyer> findAllBuyers() {
        return employeeRepository.findAll().stream()
                .filter(e -> e instanceof Buyer)
                .map(e -> (Buyer) e)
                .collect(Collectors.toList());
    }

    /** @brief Returns all {@link StaffManager} employees in the system. */
    public List<StaffManager> findAllStaffManagers() {
        return employeeRepository.findAll().stream()
                .filter(e -> e instanceof StaffManager)
                .map(e -> (StaffManager) e)
                .collect(Collectors.toList());
    }

    /** @brief Returns the total number of employee records (all roles). */
    public long getTotalEmployeeCount() {
        return employeeRepository.count();
    }

    /**
     * Returns the count of active employees.
     *
     * Currently equivalent to total count because no soft-delete / status flag is
     * implemented; retained as a named method so callers need not change when
     * an active/inactive distinction is introduced.
     */
    public long getActiveEmployeeCount() {
        return employeeRepository.count();
    }

    // =========================================================
    // WRITE OPERATIONS
    // =========================================================

    /**
     * Creates a new employee record and a corresponding Firebase Authentication account.
     *
     * A temporary password ({@code ChangeMe123!}) is issued and stored in {@link DevCredentialStore}
     * for demo visibility.  The Firebase UID is written back to the entity before persisting.
     *
     * @param employee  Transient employee entity with at least {@code email} set.
     * @return          The persisted {@link Employee} with a generated ID and Firebase UID.
     * @throws RuntimeException When the employee has no email or the Firebase API call fails.
     */
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

            log.info("[DEMO] Firebase account created — Employee: {} {} | Email: {} | UID: {}",
                    employee.getName(), employee.getSurname(), employee.getEmail(), firebaseUid);

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

    /**
     * Applies a partial update to an existing employee, ignoring {@code null} fields.
     *
     * Only non-null fields in {@code updatedData} overwrite the persisted values, making
     * it safe to call with sparse payloads (e.g., updating only a phone number).
     *
     * @param id           ID of the employee to update.
     * @param updatedData  Carrier object; {@code null} fields are ignored.
     * @return             The updated and re-persisted {@link Employee}.
     * @throws RuntimeException When no employee with {@code id} exists.
     */
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
     * Generates a Firebase password-reset link for the employee and logs it to console.
     *
     * The link is also recorded in {@link DevCredentialStore} so it is not lost between
     * demo page refreshes.  The employee's email must be set for this to succeed.
     *
     * @param employeeId  ID of the employee whose password should be reset.
     * @throws RuntimeException When the employee has no email or the Firebase API call fails.
     */
    public void requestEmployeePasswordReset(Long employeeId) {
        Employee employee = findById(employeeId);

        if (employee.getEmail() == null || employee.getEmail().isBlank()) {
            throw new RuntimeException("Employee does not have an email address");
        }

        try {
            String resetLink =
                    firebaseAuthService.generatePasswordResetLink(employee.getEmail());

            log.info("[DEMO] Password reset link generated — Employee: {} {} | Email: {} | Link: {}",
                    employee.getName(), employee.getSurname(), employee.getEmail(), resetLink);

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

    /**
     * Logs a department-change request for the given employee.
     *
     * The actual department field is not updated here; this method records the intent
     * in the audit log and is intended as a placeholder for a future approval workflow.
     *
     * @param id              ID of the employee requesting the change.
     * @param newDepartmentId ID of the target department.
     * @throws RuntimeException When no employee with {@code id} exists.
     */
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

    /**
     * Deletes an employee from both the database and Firebase Authentication.
     *
     * The Firebase account is removed first; if that fails the database record is not
     * deleted to avoid orphaned auth entries.  The dev credential entry is cleaned up
     * regardless of Firebase success to keep the demo board tidy.
     *
     * @param id  ID of the employee to delete.
     * @throws RuntimeException When no employee exists with {@code id}, or when the
     *                          Firebase deletion fails.
     */
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