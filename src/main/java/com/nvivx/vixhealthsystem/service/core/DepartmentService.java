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

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Department getDepartmentById(String id) {
        try {
            Long departmentId = Long.parseLong(id);
            return departmentRepository.findById(departmentId).orElse(null);
        } catch (NumberFormatException e) {
            // If ID is not a number (e.g., "cardiology"), return null to trigger fallback
            return null;
        }
    }

    public List<MedicalSpecialist> getDoctorsByDepartment(String departmentId) {
        try {
            Long id = Long.parseLong(departmentId);
            Department department = departmentRepository.findById(id).orElse(null);
            if (department != null) {
                return department.getEmployees().stream()
                        .filter(e -> e instanceof MedicalSpecialist)
                        .map(e -> (MedicalSpecialist) e)
                        .collect(Collectors.toList());
            }
        } catch (NumberFormatException e) {
            // Handle case where departmentId is a name string
        }
        return List.of(); // Return empty list if no doctors found
    }

    public List<String> getServicesByDepartment(String departmentId) {
        // This would typically come from a database table
        // For now, return department-specific services
        try {
            Long id = Long.parseLong(departmentId);
            Department department = departmentRepository.findById(id).orElse(null);
            if (department != null) {
                return getServicesForDepartment(department.getName());
            }
        } catch (NumberFormatException e) {
            // Handle name-based lookup
            return getServicesForDepartment(departmentId);
        }
        return Arrays.asList("Consultation", "Diagnostic Tests", "Follow-up Care");
    }

    private List<String> getServicesForDepartment(String deptName) {
        if (deptName == null) return List.of();

        String name = deptName.toLowerCase();
        if (name.contains("cardio")) {
            return Arrays.asList("ECG/EKG", "Echocardiogram", "Stress Test", "Holter Monitoring", "Cardiac Consultation");
        } else if (name.contains("neuro")) {
            return Arrays.asList("EEG", "EMG", "Neurological Examination", "Stroke Management", "Memory Clinic");
        } else if (name.contains("ortho")) {
            return Arrays.asList("Joint Replacement", "Fracture Care", "Sports Medicine", "Arthroscopy", "Physical Therapy");
        } else if (name.contains("oncol")) {
            return Arrays.asList("Chemotherapy", "Radiation Therapy", "Immunotherapy", "Palliative Care", "Screening");
        } else if (name.contains("radio")) {
            return Arrays.asList("X-Ray", "MRI", "CT Scan", "Ultrasound", "Mammography");
        } else if (name.contains("gyn")) {
            return Arrays.asList("Prenatal Care", "Obstetrics", "Gynecological Surgery", "Fertility Services", "Menopause Management");
        } else if (name.contains("paed") || name.contains("ped")) {
            return Arrays.asList("Well-child Visits", "Vaccinations", "Developmental Screening", "Pediatric Surgery", "Adolescent Medicine");
        } else {
            return Arrays.asList("General Consultation", "Preventive Care", "Health Screening", "Follow-up Visits");
        }
    }
}