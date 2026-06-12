package com.nvivx.vixhealthsystem.service.core;

import com.nvivx.vixhealthsystem.model.facility.Department;
import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DepartmentServiceTest {

    @Autowired
    private DepartmentService departmentService;

    @Test
    void shouldReturnAllDepartmentsFromDatabase() {
        List<Department> departments = departmentService.getAllDepartments();

        assertNotNull(departments);
        assertFalse(departments.isEmpty());
        assertTrue(departments.size() >= 6);
    }

    @Test
    void shouldFindCardiologyDepartmentById() {
        Department department = departmentService.getDepartmentById(1L);

        assertNotNull(department);
        assertEquals(1L, department.getId());
        assertEquals("Cardiology", department.getName());
        assertEquals("cardiology@vixhealth.com", department.getEmail());
    }

    @Test
    void shouldReturnNullWhenDepartmentIdDoesNotExist() {
        Department department = departmentService.getDepartmentById(999L);

        assertNull(department);
    }

    @Test
    void shouldFindNeurologyDepartmentByStringId() {
        Department department = departmentService.getDepartmentById("2");

        assertNotNull(department);
        assertEquals(2L, department.getId());
        assertEquals("Neurology", department.getName());
    }

    @Test
    void shouldReturnNullWhenStringIdIsInvalid() {
        Department department = departmentService.getDepartmentById("abc");

        assertNull(department);
    }

    @Test
    void shouldReturnDoctorsByDepartmentId() {
        List<MedicalSpecialist> doctors =
                departmentService.getDoctorsByDepartment(1L);

        assertNotNull(doctors);
        assertFalse(doctors.isEmpty());

        assertTrue(doctors.stream()
                .allMatch(d -> d.getDepartment() != null
                        && d.getDepartment().getId().equals(1L)));

        assertTrue(doctors.stream()
                .anyMatch(d -> d.getEmail().equals("marco.rossi@vixhealth.com")));
    }

    @Test
    void shouldReturnDoctorsByStringDepartmentId() {
        List<MedicalSpecialist> doctors =
                departmentService.getDoctorsByDepartment("2");

        assertNotNull(doctors);
        assertFalse(doctors.isEmpty());

        assertTrue(doctors.stream()
                .allMatch(d -> d.getDepartment() != null
                        && d.getDepartment().getId().equals(2L)));

        assertTrue(doctors.stream()
                .anyMatch(d -> d.getEmail().equals("elena.bianchi@vixhealth.com")));
    }

    @Test
    void shouldReturnEmptyDoctorListWhenDepartmentHasNoMedicalSpecialists() {
        List<MedicalSpecialist> doctors =
                departmentService.getDoctorsByDepartment(4L);

        assertNotNull(doctors);
        assertTrue(doctors.isEmpty());
    }

    @Test
    void shouldReturnEmptyDoctorListWhenStringDepartmentIdIsInvalid() {
        List<MedicalSpecialist> doctors =
                departmentService.getDoctorsByDepartment("invalid");

        assertNotNull(doctors);
        assertTrue(doctors.isEmpty());
    }

    @Test
    void shouldReturnCardiologyServices() {
        List<String> services =
                departmentService.getServicesByDepartment(1L);

        assertNotNull(services);
        assertEquals(5, services.size());
        assertTrue(services.contains("ECG/EKG - Electrocardiogram"));
        assertTrue(services.contains("Cardiac Consultation"));
    }

    @Test
    void shouldReturnNeurologyServices() {
        List<String> services =
                departmentService.getServicesByDepartment(2L);

        assertNotNull(services);
        assertEquals(5, services.size());
        assertTrue(services.contains("EEG - Electroencephalogram"));
        assertTrue(services.contains("Memory Clinic"));
    }

    @Test
    void shouldReturnRadiologyServices() {
        List<String> services =
                departmentService.getServicesByDepartment(3L);

        assertNotNull(services);
        assertEquals(5, services.size());
        assertTrue(services.contains("MRI - Magnetic Resonance Imaging"));
        assertTrue(services.contains("CT Scan - Computed Tomography"));
    }

    @Test
    void shouldReturnAdministrationServices() {
        List<String> services =
                departmentService.getServicesByDepartment(4L);

        assertNotNull(services);
        assertEquals(4, services.size());
        assertTrue(services.contains("Patient Registration"));
        assertTrue(services.contains("Appointment Scheduling"));
    }

    @Test
    void shouldReturnEmptyServicesWhenDepartmentDoesNotExist() {
        List<String> services =
                departmentService.getServicesByDepartment(999L);

        assertNotNull(services);
        assertTrue(services.isEmpty());
    }

    @Test
    void shouldReturnServicesWhenDepartmentStringIsName() {
        List<String> services =
                departmentService.getServicesByDepartment("cardiology");

        assertNotNull(services);
        assertEquals(5, services.size());
        assertTrue(services.contains("Echocardiogram - Heart Ultrasound"));
    }
}
