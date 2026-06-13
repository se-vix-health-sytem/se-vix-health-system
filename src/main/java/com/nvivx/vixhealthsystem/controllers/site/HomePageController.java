package com.nvivx.vixhealthsystem.controllers.site;

import com.nvivx.vixhealthsystem.model.facility.Department;
import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import com.nvivx.vixhealthsystem.service.core.DepartmentService;
import com.nvivx.vixhealthsystem.service.core.EmployeeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomePageController {

    private final EmployeeService employeeService;
    private final DepartmentService departmentService;

    public HomePageController(EmployeeService employeeService, DepartmentService departmentService) {
        this.employeeService = employeeService;
        this.departmentService = departmentService;
    }

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