// Update DepartmentController.java
package com.nvivx.vixhealthsystem.controllers.site;

import com.nvivx.vixhealthsystem.model.facility.Department;
import com.nvivx.vixhealthsystem.service.core.DepartmentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public String listDepartments(Model model) {
        var departments = departmentService.getAllDepartments().stream()
                .filter(d -> !d.getName().equalsIgnoreCase("Administration"))
                .toList();
        model.addAttribute("departments", departments);
        model.addAttribute("pageTitle", "All Departments");
        return "departments/list";
    }

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