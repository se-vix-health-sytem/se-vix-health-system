package com.nvivx.vixhealthsystem.controllers.site;

import com.nvivx.vixhealthsystem.service.core.DepartmentService;
import com.nvivx.vixhealthsystem.service.core.EmployeeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AboutController {

    private final EmployeeService employeeService;
    private final DepartmentService departmentService;

    public AboutController(EmployeeService employeeService, DepartmentService departmentService) {
        this.employeeService = employeeService;
        this.departmentService = departmentService;
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("totalSpecialists", employeeService.findAllMedicalSpecialists().size());
        model.addAttribute("totalDepartments", departmentService.getAllDepartments().size());
        model.addAttribute("totalEmployees", employeeService.getTotalEmployeeCount());
        model.addAttribute("pageTitle", "About VIX Health System");
        return "site/about";  // Add "site/" prefix
    }
}