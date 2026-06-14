package com.nvivx.vixhealthsystem.controllers.site;

import com.nvivx.vixhealthsystem.service.core.DepartmentService;
import com.nvivx.vixhealthsystem.service.core.EmployeeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @brief Controller for the public About page — base URL {@code /about}.
 *
 * Populates the About page with live statistics: the number of medical
 * specialists, the count of distinct clinical departments, and the total
 * headcount across all employee types.  These figures let visitors gauge the
 * size and breadth of the VIX Health System without navigating elsewhere.
 *
 * @see EmployeeService
 * @see DepartmentService
 */
@Controller
public class AboutController {

    private final EmployeeService employeeService;
    private final DepartmentService departmentService;

    public AboutController(EmployeeService employeeService, DepartmentService departmentService) {
        this.employeeService = employeeService;
        this.departmentService = departmentService;
    }

    // =========================================================
    // GET HANDLERS
    // =========================================================

    /**
     * GET /about — render the public About page.
     *
     * Department count is computed by de-duplicating on lowercase name so that
     * departments sharing a name across different facilities are not double-counted
     * in the displayed statistic.
     *
     * @param model  Receives {@code totalSpecialists}, {@code totalDepartments},
     *               {@code totalEmployees}, and {@code pageTitle} attributes.
     * @return       Thymeleaf template {@code site/about}.
     */
    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("totalSpecialists", employeeService.findAllMedicalSpecialists().size());
        long distinctDepts = departmentService.getAllDepartments().stream()
                .map(d -> d.getName().toLowerCase())
                .distinct()
                .count();
        model.addAttribute("totalDepartments", distinctDepts);
        model.addAttribute("totalEmployees", employeeService.getTotalEmployeeCount());
        model.addAttribute("pageTitle", "About VIX Health System");
        return "site/about";
    }
}
