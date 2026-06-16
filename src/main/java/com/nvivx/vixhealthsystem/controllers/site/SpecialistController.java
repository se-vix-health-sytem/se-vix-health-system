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

/**
 * @brief Controller for the public-facing specialist directory : base URL {@code /specialists}.
 *
 * Lets site visitors browse the full list of medical specialists and drill
 * into individual specialist profiles.  The list view also computes a count
 * of distinct departments represented so the page header can advertise the
 * breadth of specialties available.
 *
 * @see EmployeeService
 * @see DepartmentService
 */
@Controller
@RequestMapping("/specialists")
public class SpecialistController {

    private final EmployeeService employeeService;
    private final DepartmentService departmentService;

    public SpecialistController(EmployeeService employeeService, DepartmentService departmentService) {
        this.employeeService = employeeService;
        this.departmentService = departmentService;
    }

    // =========================================================
    // GET HANDLERS
    // =========================================================

    /**
     * GET /specialists : list all medical specialists.
     *
     * Counts distinct departments from the specialists' own department
     * references rather than from the department table, so the count
     * reflects only departments that currently have active specialists.
     *
     * @param model  Receives {@code specialists}, {@code departmentCount},
     *               {@code doctorImages}, and {@code pageTitle} attributes.
     * @return       Thymeleaf template {@code site/specialists/list}.
     */
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

    /**
     * GET /specialists/{id} : display a single specialist's profile page.
     *
     * Falls back to the specialist list with a redirect when the given ID does
     * not correspond to a MedicalSpecialist (either the ID is unknown or the
     * employee is of a different type).
     *
     * @param id     Database ID of the specialist to display.
     * @param model  Receives {@code specialist}, {@code doctorImages}, and {@code pageTitle}.
     * @return       Thymeleaf template {@code site/specialists/detail}, or
     *               {@code redirect:/specialists} when the specialist is not found.
     */
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
            // Specialist not found : fall through to redirect
        }
        return "redirect:/specialists";
    }
}
