package com.nvivx.vixhealthsystem.controllers.site;

import com.nvivx.vixhealthsystem.model.facility.Department;
import com.nvivx.vixhealthsystem.service.core.DepartmentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @brief Controller for the public department directory — base URL {@code /departments}.
 *
 * Exposes a browseable listing of all clinical departments and a detail page
 * for each one, showing the assigned doctors, the services offered, and a
 * profile image map.  The Administration department is hidden from the public
 * list because it has no patient-facing services.
 *
 * @see DepartmentService
 */
@Controller
@RequestMapping("/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    // =========================================================
    // GET HANDLERS
    // =========================================================

    /**
     * GET /departments — list all patient-facing clinical departments.
     *
     * The Administration department is excluded because it is internal-only
     * and should not appear in the patient-facing section of the site.
     *
     * @param model  Receives {@code departments} (filtered list) and {@code pageTitle}.
     * @return       Thymeleaf template {@code departments/list}.
     */
    @GetMapping
    public String listDepartments(Model model) {
        var departments = departmentService.getAllDepartments().stream()
                .filter(d -> !d.getName().equalsIgnoreCase("Administration"))
                .toList();
        model.addAttribute("departments", departments);
        model.addAttribute("pageTitle", "All Departments");
        return "departments/list";
    }

    /**
     * GET /departments/{id} — display the detail page for a single department.
     *
     * Loads the department by its string identifier, then separately queries
     * the doctors, services, and doctor image map that belong to it.  Using
     * the department's numeric {@code Long} ID for the service calls avoids
     * exposing the surrogate key in the URL path.
     *
     * @param id     String identifier of the department (as used in URLs).
     * @param model  Receives {@code department}, {@code doctors}, {@code services},
     *               {@code doctorImages}, and {@code pageTitle} attributes.
     * @return       Thymeleaf template {@code departments/detail}, or
     *               {@code redirect:/departments} when the identifier is not found.
     */
    @GetMapping("/{id}")
    public String departmentDetail(@PathVariable String id, Model model) {
        Department department = departmentService.getDepartmentById(id);
        if (department == null) {
            return "redirect:/departments";
        }
        Long deptId = department.getId();
        model.addAttribute("department", department);
        model.addAttribute("doctors", departmentService.getDoctorsByDepartment(deptId));
        model.addAttribute("services", departmentService.getServicesByDepartment(deptId));
        model.addAttribute("doctorImages", departmentService.getDoctorImageMap(deptId));
        model.addAttribute("pageTitle", department.getName());
        return "departments/detail";
    }
}
