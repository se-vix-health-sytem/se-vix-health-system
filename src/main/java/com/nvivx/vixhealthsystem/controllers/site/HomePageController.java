package com.nvivx.vixhealthsystem.controllers.site;

import com.nvivx.vixhealthsystem.model.facility.Department;
import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import com.nvivx.vixhealthsystem.service.core.DepartmentService;
import com.nvivx.vixhealthsystem.service.core.EmployeeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @brief Controller for the public-facing home page — base URL {@code /}.
 *
 * Assembles the data needed for the landing page: a curated selection of
 * featured specialists (first four in the database), the list of clinical
 * departments excluding Administration, and headline statistics (total
 * specialists, total departments) that appear in the hero and stats sections.
 *
 * @see DepartmentService
 * @see EmployeeService
 */
@Controller
public class HomePageController {

    private final EmployeeService employeeService;
    private final DepartmentService departmentService;

    public HomePageController(EmployeeService employeeService, DepartmentService departmentService) {
        this.employeeService = employeeService;
        this.departmentService = departmentService;
    }

    // =========================================================
    // GET HANDLERS
    // =========================================================

    /**
     * GET / — render the public home page.
     *
     * Limits featured specialists to the first four to keep the landing page
     * uncluttered.  The Administration department is filtered out because it
     * has no patient-facing services and would look out of place in the
     * department grid.
     *
     * @param model  Receives {@code featuredSpecialists} (up to 4), {@code doctorImages},
     *               {@code departments} (non-Administration), {@code totalSpecialists},
     *               and {@code totalDepartments} attributes.
     * @return       Thymeleaf template {@code site/index}.
     */
    @GetMapping("/")
    public String home(Model model) {
        List<MedicalSpecialist> allSpecialists = employeeService.findAllMedicalSpecialists();
        List<MedicalSpecialist> featuredSpecialists = allSpecialists.stream().limit(4).toList();

        List<Department> allDepartments = departmentService.getAllDepartments();
        List<Department> departments = allDepartments.stream()
                .filter(d -> !d.getName().equalsIgnoreCase("Administration"))
                .toList();

        model.addAttribute("featuredSpecialists", featuredSpecialists);
        model.addAttribute("doctorImages", departmentService.getAllDoctorImageMap());
        model.addAttribute("departments", departments);
        model.addAttribute("totalSpecialists", allSpecialists.size());
        model.addAttribute("totalDepartments", departments.size());

        return "site/index";
    }
}
