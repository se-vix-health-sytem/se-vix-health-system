package com.nvivx.vixhealthsystem.model.person.employee;

import com.nvivx.vixhealthsystem.model.enums.EmployeeType;
import com.nvivx.vixhealthsystem.model.enums.Role;
import com.nvivx.vixhealthsystem.model.facility.Department;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StaffManagerTest {
    private StaffManager staffManager;
    private MedicalSpecialist employee;
    private Department department;

    @BeforeEach
    void setUp() {
        staffManager = new StaffManager();
        staffManager.setId(1L);
        staffManager.setName("Alessia");
        staffManager.setSurname("Moretti");

        department = new Department();
        department.setId(10L);
        department.setName("Human Resources");
        staffManager.setDepartment(department);

        employee = new MedicalSpecialist();
        employee.setId(100L);
        employee.setName("Marco");
        employee.setSurname("Rossi");
        employee.setEmail("marco.rossi@vixhealth.com");
    }

    @Test
    void getSystemRole_ShouldReturnStaffManagerRole() {
        assertEquals(Role.ROLE_STAFF_MANAGER, staffManager.getSystemRole());
    }

    @Test
    void getEmployeeType_ShouldReturnStaffManagerType() {
        assertEquals(EmployeeType.STAFF_MANAGER, staffManager.getEmployeeType());
    }

    @Test
    void createAccountForEmployee_ShouldSucceedWhenValid() {
        // Should not throw exception
        staffManager.createAccountForEmployee(employee);
    }

    @Test
    void createAccountForEmployee_ShouldSetDepartmentFromManagerWhenMissing() {
        employee.setDepartment(null);

        staffManager.createAccountForEmployee(employee);

        assertEquals(department, employee.getDepartment());
    }

    @Test
    void createAccountForEmployee_ShouldNotOverrideExistingDepartment() {
        Department otherDept = new Department();
        otherDept.setId(20L);
        otherDept.setName("Cardiology");
        employee.setDepartment(otherDept);

        staffManager.createAccountForEmployee(employee);

        assertEquals(otherDept, employee.getDepartment());
        assertNotEquals(department, employee.getDepartment());
    }

    @Test
    void createAccountForEmployee_ShouldThrowExceptionWhenNameMissing() {
        employee.setName(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            staffManager.createAccountForEmployee(employee);
        });

        assertTrue(exception.getMessage().contains("name is required"));
    }

    @Test
    void createAccountForEmployee_ShouldThrowExceptionWhenEmailMissing() {
        employee.setEmail(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            staffManager.createAccountForEmployee(employee);
        });

        assertTrue(exception.getMessage().contains("email is required"));
    }

    @Test
    void deleteEmployeeAccount_ShouldSucceedWhenDeletingOtherEmployee() {
        // Should not throw exception
        staffManager.deleteEmployeeAccount(employee);
    }

    @Test
    void deleteEmployeeAccount_ShouldThrowExceptionWhenDeletingSelf() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            staffManager.deleteEmployeeAccount(staffManager);
        });

        assertTrue(exception.getMessage().contains("cannot delete their own account"));
    }

    @Test
    void credentialsRecovery_ShouldSucceedWhenEmailExists() {
        // Should not throw exception
        staffManager.credentialsRecovery(employee);
    }

    @Test
    void credentialsRecovery_ShouldThrowExceptionWhenEmailMissing() {
        employee.setEmail(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            staffManager.credentialsRecovery(employee);
        });

        assertTrue(exception.getMessage().contains("no email address"));
    }

    @Test
    void credentialsRecovery_ShouldThrowExceptionWhenEmailBlank() {
        employee.setEmail("");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            staffManager.credentialsRecovery(employee);
        });

        assertTrue(exception.getMessage().contains("no email address"));
    }
}