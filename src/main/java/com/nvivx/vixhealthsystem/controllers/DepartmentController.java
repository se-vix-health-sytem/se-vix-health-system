// Update DepartmentController.java
package com.nvivx.vixhealthsystem.controllers;

import com.nvivx.vixhealthsystem.model.facility.Department;
import com.nvivx.vixhealthsystem.service.DepartmentService;
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
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("pageTitle", "All Departments");
        return "departments/list";
    }

    @GetMapping("/{id}")
    public String departmentDetail(@PathVariable String id, Model model) {
        Department department = departmentService.getDepartmentById(id);
        if (department == null) {
            return "redirect:/departments";
        }
        model.addAttribute("department", department);
        model.addAttribute("doctors", departmentService.getDoctorsByDepartment(id));
        model.addAttribute("services", departmentService.getServicesByDepartment(id));
        model.addAttribute("pageTitle", department.getName());
        return "departments/detail";
    }
}