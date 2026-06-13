package com.nvivx.vixhealthsystem.service.core;

import com.nvivx.vixhealthsystem.model.facility.Department;
import com.nvivx.vixhealthsystem.model.person.employee.Employee;
import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import com.nvivx.vixhealthsystem.repository.DepartmentRepository;
import com.nvivx.vixhealthsystem.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DepartmentService {

    // Departments that start with a male image (others start female)
    private static final Map<String, Boolean> DEPT_STARTS_MALE = Map.of(
        "Cardiology",   true,
        "Neurology",    false,
        "Radiology",    true,
        "Orthopedics",  false,
        "Oncology",     true,
        "Pediatrics",   false,
        "Dermatology",  false,
        "Administration", true
    );

    private static final String[] MALE_FIRST   = {"m1", "f1", "m2", "f2"};
    private static final String[] FEMALE_FIRST = {"f1", "m1", "f2", "m2"};

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
     * Returns image map for every specialist across all departments.
     * Uses 2 queries total (all departments + all employees) instead of N×2.
     */
    public Map<Long, String> getAllDoctorImageMap() {
        List<Department> departments = departmentRepository.findAll();
        List<Employee> allEmployees = employeeRepository.findAll();

        Map<Long, List<MedicalSpecialist>> byDept = allEmployees.stream()
                .filter(e -> e instanceof MedicalSpecialist)
                .map(e -> (MedicalSpecialist) e)
                .filter(ms -> ms.getDepartment() != null)
                .collect(Collectors.groupingBy(ms -> ms.getDepartment().getId()));

        Map<Long, String> result = new LinkedHashMap<>();
        for (Department dept : departments) {
            List<MedicalSpecialist> docs = new ArrayList<>(byDept.getOrDefault(dept.getId(), List.of()));
            docs.sort(Comparator.comparing(MedicalSpecialist::getId));
            String deptKey = dept.getName().toLowerCase().replace(" ", "");
            boolean startsMale = DEPT_STARTS_MALE.getOrDefault(dept.getName(), true);
            String[] pattern = startsMale ? MALE_FIRST : FEMALE_FIRST;
            for (int i = 0; i < docs.size(); i++) {
                result.put(docs.get(i).getId(),
                        "/images/doctors/" + deptKey + "_" + pattern[i % 4] + ".jpg");
            }
        }
        return result;
    }

    /**
     * Returns a stable map of specialistId → image path for the given department.
     * Doctors are sorted by ID so adding/removing staff never reshuffles existing photos.
     * Pattern alternates male/female with a department-specific starting gender.
     */
    public Map<Long, String> getDoctorImageMap(Long departmentId) {
        Department dept = getDepartmentById(departmentId);
        if (dept == null) return Map.of();

        List<MedicalSpecialist> doctors = getDoctorsByDepartment(departmentId);
        doctors.sort(Comparator.comparing(d -> d.getId()));

        String deptKey = dept.getName().toLowerCase().replace(" ", "");
        boolean startsMale = DEPT_STARTS_MALE.getOrDefault(dept.getName(), true);
        String[] pattern = startsMale ? MALE_FIRST : FEMALE_FIRST;

        Map<Long, String> imageMap = new LinkedHashMap<>();
        for (int i = 0; i < doctors.size(); i++) {
            String suffix = pattern[i % 4];
            imageMap.put(doctors.get(i).getId(),
                "/images/doctors/" + deptKey + "_" + suffix + ".jpg");
        }
        return imageMap;
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
        else if (name.contains("ortho")) {
            return Arrays.asList(
                    "Fracture Diagnosis & Treatment",
                    "Joint Replacement Surgery",
                    "Arthroscopy - Minimally Invasive Joint Surgery",
                    "Sports Injury Rehabilitation",
                    "Spine Surgery & Disc Management",
                    "Bone Density Scan (DEXA)"
            );
        }
        else if (name.contains("onco")) {
            return Arrays.asList(
                    "Tumor Biopsy & Pathology",
                    "Chemotherapy Administration",
                    "Radiation Therapy (LINAC)",
                    "Cancer Screening & Early Detection",
                    "Palliative & Supportive Care",
                    "Oncology Follow-up & Remission Monitoring"
            );
        }
        else if (name.contains("pediatr")) {
            return Arrays.asList(
                    "Newborn & Neonatal Care",
                    "Child Health & Development Checkup",
                    "Vaccination Program",
                    "Growth & Nutrition Monitoring",
                    "Pediatric Acute Illness Management",
                    "Adolescent Medicine"
            );
        }
        else if (name.contains("derma")) {
            return Arrays.asList(
                    "Skin Condition Diagnosis & Treatment",
                    "Dermatoscopy - Mole & Lesion Examination",
                    "Laser Therapy for Skin Conditions",
                    "Acne & Rosacea Management",
                    "Allergy Patch Testing",
                    "Hair & Nail Disorder Treatment"
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
            return Arrays.asList(
                    "General Consultation",
                    "Preventive Care",
                    "Health Screening",
                    "Follow-up Visits"
            );
        }
    }
}