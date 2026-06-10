package com.nvivx.vixhealthsystem.service.core;

import com.nvivx.vixhealthsystem.model.facility.Department;
import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import com.nvivx.vixhealthsystem.repository.DepartmentRepository;
import com.nvivx.vixhealthsystem.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    public DepartmentService(DepartmentRepository departmentRepository,
                             EmployeeRepository employeeRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
    }

    /**
     * Get all departments from database
     */
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    /**
     * Get department by ID from database
     */
    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id).orElse(null);
    }

    /**
     * Get department by ID (String version for controllers)
     */
    public Department getDepartmentById(String id) {
        try {
            Long departmentId = Long.parseLong(id);
            return getDepartmentById(departmentId);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Get doctors (MedicalSpecialists) by department from database
     */
    public List<MedicalSpecialist> getDoctorsByDepartment(Long departmentId) {
        return employeeRepository.findAll().stream()
                .filter(e -> e instanceof MedicalSpecialist)
                .map(e -> (MedicalSpecialist) e)
                .filter(doc -> doc.getDepartment() != null &&
                        doc.getDepartment().getId().equals(departmentId))
                .collect(Collectors.toList());
    }

    /**
     * Get doctors by department (String version)
     */
    public List<MedicalSpecialist> getDoctorsByDepartment(String departmentId) {
        try {
            Long id = Long.parseLong(departmentId);
            return getDoctorsByDepartment(id);
        } catch (NumberFormatException e) {
            return List.of();
        }
    }

    /**
     * Get services offered by department.
     *
     * Services are hardcoded based on department name since there is no
     * DepartmentServices table in the database yet.
     *
     * Departments in database: Cardiology, Neurology, Radiology, Administration
     */
    public List<String> getServicesByDepartment(Long departmentId) {
        Department department = getDepartmentById(departmentId);
        if (department == null) {
            return List.of();
        }
        return getServicesByDepartmentName(department.getName());
    }

    /**
     * Get services by department (String version)
     */
    public List<String> getServicesByDepartment(String departmentId) {
        try {
            Long id = Long.parseLong(departmentId);
            return getServicesByDepartment(id);
        } catch (NumberFormatException e) {
            // If ID is a name string (e.g., "cardiology" from URL)
            return getServicesByDepartmentName(departmentId);
        }
    }

    /**
     * Hardcoded services based on department name.
     * These match the departments actually present in the database.
     */
    private List<String> getServicesByDepartmentName(String deptName) {
        if (deptName == null) return List.of();

        String name = deptName.toLowerCase();

        // Match departments from database seed data
        if (name.contains("cardio")) {
            return Arrays.asList(
                    "ECG/EKG - Electrocardiogram",
                    "Echocardiogram - Heart Ultrasound",
                    "Stress Test - Cardiac Exercise Test",
                    "Holter Monitoring - 24h Heart Monitor",
                    "Cardiac Consultation"
            );
        }
        else if (name.contains("neuro")) {
            return Arrays.asList(
                    "EEG - Electroencephalogram",
                    "EMG - Electromyography",
                    "Neurological Examination",
                    "Stroke Management",
                    "Memory Clinic"
            );
        }
        else if (name.contains("radio")) {
            return Arrays.asList(
                    "X-Ray - Digital Radiography",
                    "MRI - Magnetic Resonance Imaging",
                    "CT Scan - Computed Tomography",
                    "Ultrasound - Sonography",
                    "Mammography - Breast Imaging"
            );
        }
        else if (name.contains("admin")) {
            return Arrays.asList(
                    "Patient Registration",
                    "Medical Records Request",
                    "Billing & Insurance",
                    "Appointment Scheduling"
            );
        }
        else {
            // Default for any other departments
            return Arrays.asList(
                    "General Consultation",
                    "Preventive Care",
                    "Health Screening",
                    "Follow-up Visits"
            );
        }
    }
}