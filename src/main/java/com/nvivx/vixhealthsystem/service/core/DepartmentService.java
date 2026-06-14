package com.nvivx.vixhealthsystem.service.core;

import com.nvivx.vixhealthsystem.model.facility.Department;
import com.nvivx.vixhealthsystem.model.person.employee.Employee;
import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import com.nvivx.vixhealthsystem.repository.DepartmentRepository;
import com.nvivx.vixhealthsystem.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @brief Manages hospital departments and their associated specialists. Covers department
 *        listing, doctor lookup, and department-service catalogue queries.
 *
 * Not annotated {@code @Transactional(readOnly=true)} at the class level because most
 * operations are already read-only and the service performs no writes; individual
 * write paths can opt in if added in the future.
 *
 * @see EmployeeService
 * @see com.nvivx.vixhealthsystem.model.facility.Department
 * @see com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist
 */
@Service
public class DepartmentService {

    // =========================================================
    // FIELDS
    // =========================================================

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    // =========================================================
    // CONSTRUCTORS
    // =========================================================

    /**
     * Constructs the service with its required repositories.
     *
     * @param departmentRepository  Provides persistence access to {@link Department} entities.
     * @param employeeRepository    Required for filtering specialists by department in-memory,
     *                              since JPA inheritance prevents a direct typed query.
     */
    public DepartmentService(DepartmentRepository departmentRepository,
                             EmployeeRepository employeeRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
    }

    // =========================================================
    // READ OPERATIONS — DEPARTMENTS
    // =========================================================

    /**
     * Returns every department persisted in the database.
     *
     * @return Non-null list; empty when no departments exist.
     */
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    /**
     * Looks up a department by its numeric primary key.
     *
     * @param id  Database ID of the department.
     * @return    The matching {@link Department}, or {@code null} if not found.
     */
    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id).orElse(null);
    }

    /**
     * String-typed overload for controller convenience; delegates to
     *        {@link #getDepartmentById(Long)} after parsing.
     *
     * @param id  String representation of the department's numeric ID.
     * @return    The matching {@link Department}, or {@code null} when the ID is
     *            non-numeric or does not exist.
     */
    public Department getDepartmentById(String id) {
        try {
            Long departmentId = Long.parseLong(id);
            return getDepartmentById(departmentId);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // =========================================================
    // READ OPERATIONS — SPECIALISTS
    // =========================================================

    /**
     * Returns all {@link MedicalSpecialist} employees assigned to the given department.
     *
     * Filters in-memory after loading all employees because the single-table-inheritance
     * mapping does not allow a typed repository query for subtypes.
     *
     * @param departmentId  ID of the target department.
     * @return              Non-null list of specialists; empty if none are assigned.
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
     * String-typed overload for controller convenience; delegates to
     *        {@link #getDoctorsByDepartment(Long)} after parsing.
     *
     * @param departmentId  String representation of the department ID.
     * @return              Non-null list; empty when the ID is non-numeric or has no specialists.
     */
    public List<MedicalSpecialist> getDoctorsByDepartment(String departmentId) {
        try {
            Long id = Long.parseLong(departmentId);
            return getDoctorsByDepartment(id);
        } catch (NumberFormatException e) {
            return List.of();
        }
    }

    // =========================================================
    // READ OPERATIONS — DOCTOR IMAGE MAPS
    // =========================================================

    /**
     * Builds a stable {@code specialistId → image-path} map for every specialist
     *        across all departments using only two database queries (all departments + all employees).
     *
     * Male and female counters reset per department; the image pool wraps at index 2
     * to avoid {@code null} paths when there are more than two doctors of the same gender.
     *
     * @return {@link LinkedHashMap} ordered by department iteration order; never {@code null}.
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
            int maleCount = 0, femaleCount = 0;
            for (MedicalSpecialist doc : docs) {
                boolean isMale = doc.getGender() == 'M' || doc.getGender() == 'm';
                String suffix = isMale
                        ? "m" + (++maleCount > 2 ? ((maleCount - 1) % 2) + 1 : maleCount)
                        : "f" + (++femaleCount > 2 ? ((femaleCount - 1) % 2) + 1 : femaleCount);
                result.put(doc.getId(), "/images/doctors/" + deptKey + "_" + suffix + ".jpg");
            }
        }
        return result;
    }

    /**
     * Builds a stable {@code specialistId → image-path} map for a single department.
     *
     * Doctors are sorted by ID so that adding or removing staff never reshuffles
     * existing photo assignments.  The counter wraps at 2 per gender to stay within
     * the available static asset set.
     *
     * @param departmentId  ID of the department to map.
     * @return              Ordered map of specialist ID to image path; empty map when
     *                      the department does not exist.
     */
    public Map<Long, String> getDoctorImageMap(Long departmentId) {
        Department dept = getDepartmentById(departmentId);
        if (dept == null) return Map.of();

        List<MedicalSpecialist> doctors = getDoctorsByDepartment(departmentId);
        doctors.sort(Comparator.comparing(d -> d.getId()));

        String deptKey = dept.getName().toLowerCase().replace(" ", "");
        Map<Long, String> imageMap = new LinkedHashMap<>();
        int maleCount = 0, femaleCount = 0;
        for (MedicalSpecialist doc : doctors) {
            boolean isMale = doc.getGender() == 'M' || doc.getGender() == 'm';
            String suffix = isMale
                    ? "m" + (++maleCount > 2 ? ((maleCount - 1) % 2) + 1 : maleCount)
                    : "f" + (++femaleCount > 2 ? ((femaleCount - 1) % 2) + 1 : femaleCount);
            imageMap.put(doc.getId(), "/images/doctors/" + deptKey + "_" + suffix + ".jpg");
        }
        return imageMap;
    }

    // =========================================================
    // READ OPERATIONS — SERVICE CATALOGUE
    // =========================================================

    /**
     * Returns the catalogue of clinical services offered by the given department.
     *
     * Services are resolved from the department name because no {@code DepartmentServices}
     * table exists yet.  Known departments: Cardiology, Neurology, Radiology, Administration.
     *
     * @param departmentId  ID of the target department.
     * @return              Non-null list of service descriptions; empty when the department
     *                      is not found.
     * @see #getServicesByDepartmentName(String)
     */
    public List<String> getServicesByDepartment(Long departmentId) {
        Department department = getDepartmentById(departmentId);
        if (department == null) {
            return List.of();
        }
        return getServicesByDepartmentName(department.getName());
    }

    /**
     * String-typed overload; tries numeric parse first, falls back to name-based
     *        lookup when the value is a slug (e.g., {@code "cardiology"} from a URL path).
     *
     * @param departmentId  Numeric ID or department name string.
     * @return              Non-null list of service descriptions.
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

    // =========================================================
    // HELPERS
    // =========================================================

    /**
     * Resolves the service list from a department name using substring matching.
     *
     * Covers Cardiology, Neurology, Radiology, Orthopaedics, Oncology, Paediatrics,
     * Dermatology, and Administration.  Unrecognised names fall back to a generic list.
     *
     * @param deptName  Department name (case-insensitive); may be {@code null}.
     * @return          Non-null list of service description strings.
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