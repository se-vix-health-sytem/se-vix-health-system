package com.nvivx.vixhealthsystem.service.core;

import com.nvivx.vixhealthsystem.model.facility.Department;
import com.nvivx.vixhealthsystem.model.person.employee.*;
import com.nvivx.vixhealthsystem.repository.DepartmentRepository;
import com.nvivx.vixhealthsystem.repository.EmployeeRepository;
import com.nvivx.vixhealthsystem.service.AuditService;
import com.nvivx.vixhealthsystem.service.integration.FirebaseAuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Arrange = prepare fake data and mock behavior
 * Act = call the method being tested
 * Assert = check the result
 * Verify = check that mocks were called correctly
 */
@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private AuditService auditService;

    @Mock
    private FirebaseAuthService firebaseAuthService;

    @InjectMocks
    private EmployeeService service;

    @Test
    void shouldReturnAllDepartments() {
        // Arrange
        Department d1 = new Department();
        d1.setId(1L);
        d1.setName("Cardiology");

        Department d2 = new Department();
        d2.setId(2L);
        d2.setName("Neurology");

        when(departmentRepository.findAll())
                .thenReturn(List.of(d1, d2));

        // Act
        List<Department> result = service.findAllDepartments();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Cardiology", result.get(0).getName());

        verify(departmentRepository).findAll();
    }

    @Test
    void shouldFindDepartmentById() {
        // Arrange
        Department department = new Department();
        department.setId(1L);
        department.setName("Cardiology");

        when(departmentRepository.findById(1L))
                .thenReturn(Optional.of(department));

        // Act
        Department result = service.findDepartmentById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Cardiology", result.getName());

        verify(departmentRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDepartmentNotFound() {
        // Arrange
        when(departmentRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act + Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.findDepartmentById(99L)
        );

        assertTrue(exception.getMessage().contains("Department not found"));
    }

    @Test
    void shouldFindEmployeeById() {
        // Arrange
        MedicalSpecialist employee = new MedicalSpecialist();
        employee.setId(1L);
        employee.setName("Marco");

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(employee));

        // Act
        Employee result = service.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Marco", result.getName());

        verify(employeeRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenEmployeeNotFound() {
        // Arrange
        when(employeeRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act + Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.findById(99L)
        );

        assertTrue(exception.getMessage().contains("Employee not found"));
    }

    @Test
    void shouldFindEmployeeByIdOptional() {
        // Arrange
        Secretary secretary = new Secretary();
        secretary.setId(2L);

        when(employeeRepository.findById(2L))
                .thenReturn(Optional.of(secretary));

        // Act
        Optional<Employee> result = service.findByIdOptional(2L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(2L, result.get().getId());

        verify(employeeRepository).findById(2L);
    }

    @Test
    void shouldReturnAllEmployees() {
        // Arrange
        MedicalSpecialist doctor = new MedicalSpecialist();
        Secretary secretary = new Secretary();

        when(employeeRepository.findAll())
                .thenReturn(List.of(doctor, secretary));

        // Act
        List<Employee> result = service.findAllEmployees();

        // Assert
        assertEquals(2, result.size());

        verify(employeeRepository).findAll();
    }

    @Test
    void shouldFindEmployeeByEmail() {
        // Arrange
        MedicalSpecialist employee = new MedicalSpecialist();
        employee.setEmail("doctor@test.com");

        when(employeeRepository.findByEmail("doctor@test.com"))
                .thenReturn(employee);

        // Act
        Employee result = service.findByEmail("doctor@test.com");

        // Assert
        assertNotNull(result);
        assertEquals("doctor@test.com", result.getEmail());

        verify(employeeRepository).findByEmail("doctor@test.com");
    }

    @Test
    void shouldFindEmployeeByFirebaseUid() {
        // Arrange
        MedicalSpecialist employee = new MedicalSpecialist();
        employee.setFirebaseUid("firebase123");

        when(employeeRepository.findByFirebaseUid("firebase123"))
                .thenReturn(employee);

        // Act
        Employee result = service.findByFirebaseUid("firebase123");

        // Assert
        assertNotNull(result);
        assertEquals("firebase123", result.getFirebaseUid());

        verify(employeeRepository).findByFirebaseUid("firebase123");
    }

    @Test
    void shouldFindEmployeesBySurname() {
        // Arrange
        MedicalSpecialist employee = new MedicalSpecialist();
        employee.setSurname("Rossi");

        when(employeeRepository.findBySurname("Rossi"))
                .thenReturn(List.of(employee));

        // Act
        List<Employee> result = service.findBySurname("Rossi");

        // Assert
        assertEquals(1, result.size());
        assertEquals("Rossi", result.get(0).getSurname());

        verify(employeeRepository).findBySurname("Rossi");
    }

    @Test
    void shouldFindEmployeesByDepartmentId() {
        // Arrange
        Department department = new Department();
        department.setId(1L);

        MedicalSpecialist employee = new MedicalSpecialist();
        employee.setDepartment(department);

        when(employeeRepository.findByDepartmentId(1L))
                .thenReturn(List.of(employee));

        // Act
        List<Employee> result = service.findByDepartmentId(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getDepartment().getId());

        verify(employeeRepository).findByDepartmentId(1L);
    }

    @Test
    void shouldFindEmployeesByRole() {
        // Arrange
        MedicalSpecialist doctor = new MedicalSpecialist();
        Secretary secretary = new Secretary();

        when(employeeRepository.findAll())
                .thenReturn(List.of(doctor, secretary));

        // Act
        List<Employee> result = service.findByRole(MedicalSpecialist.class);

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.get(0) instanceof MedicalSpecialist);

        verify(employeeRepository).findAll();
    }

    @Test
    void shouldReturnAllMedicalSpecialists() {
        // Arrange
        MedicalSpecialist doctor = new MedicalSpecialist();
        Secretary secretary = new Secretary();

        when(employeeRepository.findAll())
                .thenReturn(List.of(doctor, secretary));

        // Act
        List<MedicalSpecialist> result = service.findAllMedicalSpecialists();

        // Assert
        assertEquals(1, result.size());

        verify(employeeRepository).findAll();
    }

    @Test
    void shouldReturnAllSecretaries() {
        // Arrange
        MedicalSpecialist doctor = new MedicalSpecialist();
        Secretary secretary = new Secretary();

        when(employeeRepository.findAll())
                .thenReturn(List.of(doctor, secretary));

        // Act
        List<Secretary> result = service.findAllSecretaries();

        // Assert
        assertEquals(1, result.size());

        verify(employeeRepository).findAll();
    }

    @Test
    void shouldReturnAllTechnicians() {
        // Arrange
        Technician technician = new Technician();
        Buyer buyer = new Buyer();

        when(employeeRepository.findAll())
                .thenReturn(List.of(technician, buyer));

        // Act
        List<Technician> result = service.findAllTechnicians();

        // Assert
        assertEquals(1, result.size());

        verify(employeeRepository).findAll();
    }

    @Test
    void shouldReturnAllBuyers() {
        // Arrange
        Technician technician = new Technician();
        Buyer buyer = new Buyer();

        when(employeeRepository.findAll())
                .thenReturn(List.of(technician, buyer));

        // Act
        List<Buyer> result = service.findAllBuyers();

        // Assert
        assertEquals(1, result.size());

        verify(employeeRepository).findAll();
    }

    @Test
    void shouldReturnAllStaffManagers() {
        // Arrange
        StaffManager manager = new StaffManager();
        Buyer buyer = new Buyer();

        when(employeeRepository.findAll())
                .thenReturn(List.of(manager, buyer));

        // Act
        List<StaffManager> result = service.findAllStaffManagers();

        // Assert
        assertEquals(1, result.size());

        verify(employeeRepository).findAll();
    }

    @Test
    void shouldReturnTotalEmployeeCount() {
        // Arrange
        when(employeeRepository.count())
                .thenReturn(5L);

        // Act
        long result = service.getTotalEmployeeCount();

        // Assert
        assertEquals(5L, result);

        verify(employeeRepository).count();
    }

    @Test
    void shouldReturnActiveEmployeeCount() {
        // Arrange
        when(employeeRepository.count())
                .thenReturn(5L);

        // Act
        long result = service.getActiveEmployeeCount();

        // Assert
        assertEquals(5L, result);

        verify(employeeRepository).count();
    }

    @Test
    void shouldCreateEmployeeSuccessfully() throws Exception {
        // Arrange
        MedicalSpecialist employee = new MedicalSpecialist();
        employee.setId(1L);
        employee.setName("Marco");
        employee.setSurname("Rossi");
        employee.setEmail("marco@test.com");
        employee.setHireDate(LocalDate.now());

        when(firebaseAuthService.createUser("marco@test.com", "ChangeMe123!"))
                .thenReturn("firebase123");

        when(employeeRepository.save(employee))
                .thenReturn(employee);

        // Act
        Employee result = service.createEmployee(employee);

        // Assert
        assertNotNull(result);
        assertEquals("firebase123", result.getFirebaseUid());

        verify(firebaseAuthService).createUser("marco@test.com", "ChangeMe123!");
        verify(employeeRepository).save(employee);
        verify(auditService).log(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void shouldThrowExceptionWhenCreatingEmployeeWithoutEmail() {
        // Arrange
        MedicalSpecialist employee = new MedicalSpecialist();
        employee.setName("NoEmail");

        // Act + Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.createEmployee(employee)
        );

        assertTrue(exception.getMessage().contains("Employee email is required"));

        verifyNoInteractions(firebaseAuthService);
        verifyNoInteractions(employeeRepository);
    }

    @Test
    void shouldThrowExceptionWhenFirebaseCreateFails() throws Exception {
        // Arrange
        MedicalSpecialist employee = new MedicalSpecialist();
        employee.setEmail("fail@test.com");

        when(firebaseAuthService.createUser("fail@test.com", "ChangeMe123!"))
                .thenThrow(new RuntimeException("Firebase failed"));

        // Act + Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.createEmployee(employee)
        );

        assertTrue(exception.getMessage().contains("Firebase error"));

        verify(firebaseAuthService).createUser("fail@test.com", "ChangeMe123!");
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void shouldUpdateEmployee() {
        // Arrange
        MedicalSpecialist existing = new MedicalSpecialist();
        existing.setId(1L);
        existing.setName("Old");
        existing.setSurname("Name");

        MedicalSpecialist updatedData = new MedicalSpecialist();
        updatedData.setName("New");
        updatedData.setSurname("Surname");
        updatedData.setEmail("new@test.com");

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(existing));

        when(employeeRepository.save(existing))
                .thenReturn(existing);

        // Act
        Employee result = service.updateEmployee(1L, updatedData);

        // Assert
        assertEquals("New", result.getName());
        assertEquals("Surname", result.getSurname());
        assertEquals("new@test.com", result.getEmail());

        verify(employeeRepository).findById(1L);
        verify(employeeRepository).save(existing);
        verify(auditService).log(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void shouldRequestEmployeePasswordReset() throws Exception {
        // Arrange
        MedicalSpecialist employee = new MedicalSpecialist();
        employee.setId(1L);
        employee.setName("Marco");
        employee.setSurname("Rossi");
        employee.setEmail("marco@test.com");

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(employee));

        when(firebaseAuthService.generatePasswordResetLink("marco@test.com"))
                .thenReturn("reset-link");

        // Act
        service.requestEmployeePasswordReset(1L);

        // Assert
        verify(employeeRepository).findById(1L);
        verify(firebaseAuthService).generatePasswordResetLink("marco@test.com");
        verify(auditService).log(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void shouldThrowExceptionWhenPasswordResetEmployeeHasNoEmail() {
        // Arrange
        MedicalSpecialist employee = new MedicalSpecialist();
        employee.setId(1L);
        employee.setEmail("");

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(employee));

        // Act + Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.requestEmployeePasswordReset(1L)
        );

        assertTrue(exception.getMessage().contains("Employee does not have an email address"));

        verify(employeeRepository).findById(1L);
        verifyNoInteractions(firebaseAuthService);
    }

    @Test
    void shouldChangeDepartment() {
        // Arrange
        MedicalSpecialist employee = new MedicalSpecialist();
        employee.setId(1L);

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(employee));

        // Act
        service.changeDepartment(1L, 2L);

        // Assert
        verify(employeeRepository).findById(1L);
        verify(auditService).log(
                eq("REQUEST_DEPARTMENT_CHANGE"),
                eq("Employee"),
                eq("1"),
                eq("Department change requested to ID: 2")
        );
    }

    @Test
    void shouldDeleteEmployeeWithoutFirebaseUid() throws Exception {
        // Arrange
        MedicalSpecialist employee = new MedicalSpecialist();
        employee.setId(1L);
        employee.setName("Marco");
        employee.setSurname("Rossi");

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(employee));

        // Act
        service.deleteEmployee(1L);

        // Assert
        verify(employeeRepository).findById(1L);
        verify(firebaseAuthService, never()).deleteUser(anyString());
        verify(employeeRepository).delete(employee);
        verify(auditService).log(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void shouldDeleteEmployeeWithFirebaseUid() throws Exception {
        // Arrange
        MedicalSpecialist employee = new MedicalSpecialist();
        employee.setId(1L);
        employee.setName("Marco");
        employee.setSurname("Rossi");
        employee.setFirebaseUid("firebase123");

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(employee));

        // Act
        service.deleteEmployee(1L);

        // Assert
        verify(employeeRepository).findById(1L);
        verify(firebaseAuthService).deleteUser("firebase123");
        verify(employeeRepository).delete(employee);
        verify(auditService).log(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void shouldThrowExceptionWhenFirebaseDeleteFails() throws Exception {
        // Arrange
        MedicalSpecialist employee = new MedicalSpecialist();
        employee.setId(1L);
        employee.setFirebaseUid("firebase123");

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(employee));

        doThrow(new RuntimeException("Firebase delete failed"))
                .when(firebaseAuthService).deleteUser("firebase123");

        // Act + Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.deleteEmployee(1L)
        );

        assertTrue(exception.getMessage().contains("Unable to delete Firebase account"));

        verify(firebaseAuthService).deleteUser("firebase123");
        verify(employeeRepository, never()).delete(any());
    }
}