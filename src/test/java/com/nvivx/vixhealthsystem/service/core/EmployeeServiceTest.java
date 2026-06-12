package com.nvivx.vixhealthsystem.service.core;

import com.nvivx.vixhealthsystem.model.person.employee.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EmployeeServiceTest {

    @Autowired
    private EmployeeService employeeService;

    @Test
    void shouldFindEmployeeById() {
        Employee employee = employeeService.findById(1L);

        assertNotNull(employee);
        assertEquals(1L, employee.getId());
        assertEquals("Marco", employee.getName());
        assertEquals("Rossi", employee.getSurname());
        assertTrue(employee instanceof MedicalSpecialist);
    }

    @Test
    void shouldThrowExceptionWhenEmployeeIdDoesNotExist() {
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> employeeService.findById(999L)
        );

        assertTrue(exception.getMessage().contains("Employee not found"));
    }

    @Test
    void shouldFindEmployeeByIdOptional() {
        Optional<Employee> employee = employeeService.findByIdOptional(2L);

        assertTrue(employee.isPresent());
        assertEquals("Elena", employee.get().getName());
        assertTrue(employee.get() instanceof MedicalSpecialist);
    }

    @Test
    void shouldReturnEmptyOptionalWhenEmployeeDoesNotExist() {
        Optional<Employee> employee = employeeService.findByIdOptional(999L);

        assertTrue(employee.isEmpty());
    }

    @Test
    void shouldReturnAllEmployeesFromDatabase() {
        List<Employee> employees = employeeService.findAllEmployees();

        assertNotNull(employees);
        assertEquals(11, employees.size());
    }

    @Test
    void shouldFindEmployeeByEmail() {
        Employee employee = employeeService.findByEmail("marco.rossi@vixhealth.com");

        assertNotNull(employee);
        assertEquals("Marco", employee.getName());
        assertEquals("Rossi", employee.getSurname());
        assertTrue(employee instanceof MedicalSpecialist);
    }

    @Test
    void shouldReturnNullWhenEmailDoesNotExist() {
        Employee employee = employeeService.findByEmail("notfound@vixhealth.com");

        assertNull(employee);
    }

    @Test
    void shouldFindEmployeesBySurname() {
        List<Employee> employees = employeeService.findBySurname("Rossi");

        assertNotNull(employees);
        assertFalse(employees.isEmpty());

        assertTrue(employees.stream()
                .anyMatch(e -> e.getEmail().equals("marco.rossi@vixhealth.com")));
    }

    @Test
    void shouldFindEmployeesByDepartmentId() {
        List<Employee> employees = employeeService.findByDepartmentId(1L);

        assertNotNull(employees);
        assertFalse(employees.isEmpty());

        assertTrue(employees.stream()
                .allMatch(e -> e.getDepartment() != null
                        && e.getDepartment().getId().equals(1L)));
    }

    @Test
    void shouldFindEmployeesByRole() {
        List<Employee> specialists =
                employeeService.findByRole(MedicalSpecialist.class);

        assertNotNull(specialists);
        assertEquals(3, specialists.size());
        assertTrue(specialists.stream()
                .allMatch(e -> e instanceof MedicalSpecialist));
    }

    @Test
    void shouldReturnAllMedicalSpecialists() {
        List<MedicalSpecialist> specialists =
                employeeService.findAllMedicalSpecialists();

        assertNotNull(specialists);
        assertEquals(3, specialists.size());
    }

    @Test
    void shouldReturnAllSecretaries() {
        List<Secretary> secretaries =
                employeeService.findAllSecretaries();

        assertNotNull(secretaries);
        assertEquals(2, secretaries.size());
    }

    @Test
    void shouldReturnAllTechnicians() {
        List<Technician> technicians =
                employeeService.findAllTechnicians();

        assertNotNull(technicians);
        assertEquals(2, technicians.size());
    }

    @Test
    void shouldReturnAllBuyers() {
        List<Buyer> buyers =
                employeeService.findAllBuyers();

        assertNotNull(buyers);
        assertEquals(2, buyers.size());
    }

    @Test
    void shouldReturnAllStaffManagers() {
        List<StaffManager> staffManagers =
                employeeService.findAllStaffManagers();

        assertNotNull(staffManagers);
        assertEquals(2, staffManagers.size());
    }

    @Test
    void shouldReturnTotalEmployeeCount() {
        long count = employeeService.getTotalEmployeeCount();

        assertEquals(11, count);
    }

    @Test
    void shouldReturnActiveEmployeeCount() {
        long count = employeeService.getActiveEmployeeCount();

        assertEquals(11, count);
    }

    @Test
    void shouldUpdateEmployee() {
        Employee updatedData = new MedicalSpecialist();
        updatedData.setName("UpdatedName");
        updatedData.setSurname("UpdatedSurname");
        updatedData.setEmail("updated.employee@vixhealth.com");

        Employee updated = employeeService.updateEmployee(1L, updatedData);

        assertNotNull(updated);
        assertEquals("UpdatedName", updated.getName());
        assertEquals("UpdatedSurname", updated.getSurname());
        assertEquals("updated.employee@vixhealth.com", updated.getEmail());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingEmployeeThatDoesNotExist() {
        Employee updatedData = new MedicalSpecialist();
        updatedData.setName("Nobody");

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> employeeService.updateEmployee(999L, updatedData)
        );

        assertTrue(exception.getMessage().contains("Employee not found"));
    }

    /*@Test
    void shouldCreateEmployeeWithFirebaseAccount() {
        MedicalSpecialist employee = new MedicalSpecialist();

        long unique = System.currentTimeMillis();

        employee.setName("Test");
        employee.setSurname("Doctor");
        employee.setEmail("test.doctor." + unique + "@vixhealth.com");
        employee.setHireDate(LocalDate.now());
        employee.setSpecialty("Test Specialty");
        employee.setLicenseNumber("TEST-LIC-" + unique);

        Employee saved = employeeService.createEmployee(employee);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertNotNull(saved.getFirebaseUid());
        assertEquals("Test", saved.getName());
        assertEquals("Doctor", saved.getSurname());
    }*/

    @Test
    void shouldThrowExceptionWhenCreatingEmployeeWithoutEmail() {
        MedicalSpecialist employee = new MedicalSpecialist();

        employee.setName("No");
        employee.setSurname("Email");
        employee.setHireDate(LocalDate.now());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> employeeService.createEmployee(employee)
        );

        assertTrue(exception.getMessage().contains("Employee email is required"));
    }

    @Test
    void shouldRequestEmployeePasswordReset() {
        assertDoesNotThrow(() ->
                employeeService.requestEmployeePasswordReset(1L)
        );
    }

    @Test
    void shouldChangeDepartment() {
        assertDoesNotThrow(() ->
                employeeService.changeDepartment(1L, 2L)
        );
    }

    /*@Test
    void shouldDeleteEmployee() {
        MedicalSpecialist employee = new MedicalSpecialist();

        long unique = System.currentTimeMillis();

        employee.setName("Delete");
        employee.setSurname("Me");
        employee.setEmail("delete.me." + unique + "@vixhealth.com");
        employee.setHireDate(LocalDate.now());
        employee.setSpecialty("Temporary");
        employee.setLicenseNumber("DEL-LIC-" + unique);

        Employee saved = employeeService.createEmployee(employee);

        assertNotNull(saved.getId());

        assertDoesNotThrow(() ->
                employeeService.deleteEmployee(saved.getId())
        );

        assertTrue(employeeService.findByIdOptional(saved.getId()).isEmpty());
    }*/

    @Test
    void shouldThrowExceptionWhenDeletingEmployeeThatDoesNotExist() {
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> employeeService.deleteEmployee(999L)
        );

        assertTrue(exception.getMessage().contains("Employee not found"));
    }
}