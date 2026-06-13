package com.nvivx.vixhealthsystem.controllers.site;

import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import com.nvivx.vixhealthsystem.service.core.DepartmentService;
import com.nvivx.vixhealthsystem.service.core.EmployeeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/specialists")
public class SpecialistController {

    private final EmployeeService employeeService;
    private final DepartmentService departmentService;

    public SpecialistController(EmployeeService employeeService, DepartmentService departmentService) {
        this.employeeService = employeeService;
        this.departmentService = departmentService;
    }

    @GetMapping
    public String listSpecialists(Model model) {
        List<MedicalSpecialist> specialists = employeeService.findAllMedicalSpecialists();
        long departmentCount = specialists.stream()
                .map(MedicalSpecialist::getDepartment)
                .filter(d -> d != null)
                .map(d -> d.getId())
                .distinct()
                .count();
        model.addAttribute("specialists", specialists);
        model.addAttribute("departmentCount", departmentCount);
        model.addAttribute("doctorImages", departmentService.getAllDoctorImageMap());
        model.addAttribute("pageTitle", "Our Medical Specialists");
        return "site/specialists/list";
    }

    @GetMapping("/{id}")
    public String specialistDetail(@PathVariable Long id, Model model) {
        try {
            var employee = employeeService.findById(id);
            if (employee instanceof MedicalSpecialist specialist) {
                model.addAttribute("specialist", specialist);
                model.addAttribute("doctorImages", departmentService.getAllDoctorImageMap());
                model.addAttribute("pageTitle", "Dr. " + specialist.getName() + " " + specialist.getSurname());
                return "site/specialists/detail";
            }
        } catch (Exception e) {
            // Specialist not found
        }
        return "redirect:/specialists";
    }
}