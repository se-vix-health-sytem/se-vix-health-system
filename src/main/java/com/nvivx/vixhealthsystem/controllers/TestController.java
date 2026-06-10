package com.nvivx.vixhealthsystem.controllers;

import com.nvivx.vixhealthsystem.mock.MockDatabase;
import com.nvivx.vixhealthsystem.model.person.Patient;
import com.nvivx.vixhealthsystem.model.person.employee.Employee;
import com.nvivx.vixhealthsystem.service.MedicalSpecialistService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestController {

    private final MockDatabase mockDatabase;
    private final MedicalSpecialistService medicalSpecialistService;

    public TestController(MockDatabase mockDatabase, MedicalSpecialistService medicalSpecialistService) {
        this.mockDatabase = mockDatabase;
        this.medicalSpecialistService = medicalSpecialistService;
    }

    @GetMapping("/api/test/employees")
    public List<Employee> testGetAllEmployees() {
        return mockDatabase.findAllEmployees();
    }

    @GetMapping("/api/test/patients")
    public List<Patient> testGetAllPatients() {
        return medicalSpecialistService.getAllPatients();
    }

    @GetMapping("/api/test/patients/{id}")
    public Patient testGetPatient(@PathVariable int id) {
        return medicalSpecialistService.getPatientWithMedicalRecord(id);
    }

    @GetMapping("/api/test/current-user")
    public String getCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return "No authentication";
        return "Username: " + auth.getName() + ", Roles: " + auth.getAuthorities();
    }

}