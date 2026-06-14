package com.nvivx.vixhealthsystem.service.core;

import com.nvivx.vixhealthsystem.model.facility.Department;
import com.nvivx.vixhealthsystem.model.person.employee.Employee;
import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import com.nvivx.vixhealthsystem.repository.DepartmentRepository;
import com.nvivx.vixhealthsystem.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @brief Unit tests for DepartmentService using Mockito mocks for DepartmentRepository and EmployeeRepository.
 * Covers getAllDepartments, getDepartmentById (Long and String), getDoctorsByDepartment,
 * getServicesByDepartment, and doctor image-map generation for single and all departments.
 */
@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private DepartmentService service;

    /**
     * Tests that getAllDepartments() returns all departments
     * provided by the mocked DepartmentRepository.
     */
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
        List<Department> result = service.getAllDepartments();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Cardiology", result.get(0).getName());
        assertEquals("Neurology", result.get(1).getName());

        //Verify
        verify(departmentRepository).findAll();
    }

    /**
     * Tests that getDepartmentById(Long) returns the correct department
     * when the department exists.
     */
    @Test
    void shouldFindDepartmentById() {
        // Arrange
        Department department = new Department();
        department.setId(1L);
        department.setName("Cardiology");

        when(departmentRepository.findById(1L))
                .thenReturn(Optional.of(department));

        // Act
        Department result = service.getDepartmentById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Cardiology", result.getName());

        //Verify
        verify(departmentRepository).findById(1L);
    }

    /**
     * Tests that getDepartmentById(Long) returns null
     * when the department does not exist.
     */
    @Test
    void shouldReturnNullWhenDepartmentDoesNotExist() {
        // Arrange
        when(departmentRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act
        Department result = service.getDepartmentById(99L);

        // Assert
        assertNull(result);

        //Verify
        verify(departmentRepository).findById(99L);
    }

    /**
     * Tests that getDepartmentById(String) correctly converts
     * a valid String ID into a Long and returns the department.
     */
    @Test
    void shouldFindDepartmentByStringId() {
        // Arrange
        Department department = new Department();
        department.setId(2L);
        department.setName("Neurology");

        when(departmentRepository.findById(2L))
                .thenReturn(Optional.of(department));

        // Act
        Department result = service.getDepartmentById("2");

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("Neurology", result.getName());

        //Verify
        verify(departmentRepository).findById(2L);
    }

    /**
     * Tests that getDepartmentById(String) returns null
     * when the String cannot be converted to a number.
     */
    @Test
    void shouldReturnNullWhenStringIdIsInvalid() {
        // Act
        Department result = service.getDepartmentById("abc");

        // Assert
        assertNull(result);


        verifyNoInteractions(departmentRepository);
    }

    /**
     * Tests that getDoctorsByDepartment(Long) returns only
     * MedicalSpecialists from the requested department.
     */
    @Test
    void shouldReturnDoctorsByDepartment() {
        // Arrange
        Department cardiology = new Department();
        cardiology.setId(1L);

        Department neurology = new Department();
        neurology.setId(2L);

        MedicalSpecialist doctor1 = new MedicalSpecialist();
        doctor1.setId(10L);
        doctor1.setName("Marco");
        doctor1.setEmail("marco.rossi@vixhealth.com");
        doctor1.setDepartment(cardiology);

        MedicalSpecialist doctor2 = new MedicalSpecialist();
        doctor2.setId(20L);
        doctor2.setName("Elena");
        doctor2.setDepartment(neurology);

        Employee normalEmployee = mock(Employee.class);

        when(employeeRepository.findAll())
                .thenReturn(List.of(doctor1, doctor2, normalEmployee));

        // Act
        List<MedicalSpecialist> result =
                service.getDoctorsByDepartment(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Marco", result.get(0).getName());
        assertEquals(1L, result.get(0).getDepartment().getId());

        //Verify
        verify(employeeRepository).findAll();
    }

    /**
     * Tests that an empty list is returned when no doctors
     * belong to the requested department.
     */
    @Test
    void shouldReturnEmptyDoctorsListWhenDepartmentHasNoDoctors() {
        // Arrange
        Department neurology = new Department();
        neurology.setId(2L);

        MedicalSpecialist doctor = new MedicalSpecialist();
        doctor.setId(20L);
        doctor.setDepartment(neurology);

        when(employeeRepository.findAll())
                .thenReturn(List.of(doctor));

        // Act
        List<MedicalSpecialist> result =
                service.getDoctorsByDepartment(1L);

        // Assert
        assertTrue(result.isEmpty());

        //Verify
        verify(employeeRepository).findAll();
    }

    /**
     * Tests that getDoctorsByDepartment(String) returns an empty list
     * when the String ID is invalid.
     */
    @Test
    void shouldReturnEmptyDoctorsListWhenStringIdIsInvalid() {
        // Act
        List<MedicalSpecialist> result =
                service.getDoctorsByDepartment("invalid");

        // Assert
        assertTrue(result.isEmpty());

        verifyNoInteractions(employeeRepository);
    }

    /**
     * Tests that Cardiology services are returned correctly.
     */
    @Test
    void shouldReturnCardiologyServices() {
        // Arrange
        Department department = new Department();
        department.setId(1L);
        department.setName("Cardiology");

        when(departmentRepository.findById(1L))
                .thenReturn(Optional.of(department));

        // Act
        List<String> services =
                service.getServicesByDepartment(1L);

        // Assert
        assertEquals(5, services.size());
        assertTrue(services.contains("ECG/EKG - Electrocardiogram"));
        assertTrue(services.contains("Cardiac Consultation"));

        //Verify
        verify(departmentRepository).findById(1L);
    }

    /**
     * Tests service lookup using a department name instead of an ID.
     */
    @Test
    void shouldReturnNeurologyServicesFromStringName() {
        // Act
        List<String> services =
                service.getServicesByDepartment("neurology");

        // Assert
        assertEquals(5, services.size());
        assertTrue(services.contains("EEG - Electroencephalogram"));
        assertTrue(services.contains("Memory Clinic"));

        verifyNoInteractions(departmentRepository);
    }

    /**
     * Tests that unknown department names return default services.
     */
    @Test
    void shouldReturnDefaultServicesForUnknownDepartmentName() {
        // Act
        List<String> services =
                service.getServicesByDepartment("unknown");

        // Assert
        assertEquals(4, services.size());
        assertTrue(services.contains("General Consultation"));
    }

    /**
     * Tests that an empty service list is returned when
     * a department ID does not exist.
     */
    @Test
    void shouldReturnEmptyServicesWhenDepartmentDoesNotExist() {
        // Arrange
        when(departmentRepository.findById(999L))
                .thenReturn(Optional.empty());

        // Act
        List<String> services =
                service.getServicesByDepartment(999L);

        // Assert
        assertNotNull(services);
        assertTrue(services.isEmpty());

        //Verify
        verify(departmentRepository).findById(999L);
    }

    /**
     * Tests that image paths are generated correctly for doctors
     * inside one department.
     */
    @Test
    void shouldReturnDoctorImageMapForDepartment() {
        // Arrange
        Department cardiology = new Department();
        cardiology.setId(1L);
        cardiology.setName("Cardiology");

        MedicalSpecialist doctor1 = new MedicalSpecialist();
        doctor1.setId(10L);
        doctor1.setGender('M');
        doctor1.setDepartment(cardiology);

        MedicalSpecialist doctor2 = new MedicalSpecialist();
        doctor2.setId(20L);
        doctor2.setGender('F');
        doctor2.setDepartment(cardiology);

        when(departmentRepository.findById(1L))
                .thenReturn(Optional.of(cardiology));

        when(employeeRepository.findAll())
                .thenReturn(List.of(doctor2, doctor1));

        // Act
        Map<Long, String> imageMap =
                service.getDoctorImageMap(1L);

        // Assert
        assertEquals(2, imageMap.size());
        assertEquals("/images/doctors/cardiology_m1.jpg", imageMap.get(10L));
        assertEquals("/images/doctors/cardiology_f1.jpg", imageMap.get(20L));

        verify(departmentRepository).findById(1L);
        verify(employeeRepository).findAll();
    }

    /**
     * Tests that an empty image map is returned when
     * the department does not exist.
     */
    @Test
    void shouldReturnEmptyImageMapWhenDepartmentDoesNotExist() {
        // Arrange
        when(departmentRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act
        Map<Long, String> imageMap =
                service.getDoctorImageMap(99L);

        // Assert
        assertTrue(imageMap.isEmpty());

        verify(departmentRepository).findById(99L);
        verifyNoInteractions(employeeRepository);
    }

    /**
     * Tests that image paths are generated for doctors
     * across all departments.
     */
    @Test
    void shouldReturnAllDoctorImageMap() {
        // Arrange
        Department cardiology = new Department();
        cardiology.setId(1L);
        cardiology.setName("Cardiology");

        Department neurology = new Department();
        neurology.setId(2L);
        neurology.setName("Neurology");

        MedicalSpecialist doctor1 = new MedicalSpecialist();
        doctor1.setId(10L);
        doctor1.setGender('M');
        doctor1.setDepartment(cardiology);

        MedicalSpecialist doctor2 = new MedicalSpecialist();
        doctor2.setId(20L);
        doctor2.setGender('F');
        doctor2.setDepartment(neurology);

        when(departmentRepository.findAll())
                .thenReturn(List.of(cardiology, neurology));

        when(employeeRepository.findAll())
                .thenReturn(List.of(doctor1, doctor2));

        // Act
        Map<Long, String> imageMap =
                service.getAllDoctorImageMap();

        // Assert
        assertEquals(2, imageMap.size());
        assertEquals("/images/doctors/cardiology_m1.jpg", imageMap.get(10L));
        assertEquals("/images/doctors/neurology_f1.jpg", imageMap.get(20L));

        verify(departmentRepository).findAll();
        verify(employeeRepository).findAll();
    }
}