package com.nvivx.vixhealthsystem.controllers;

import com.nvivx.vixhealthsystem.dto.CreateAppointmentRequest;
import com.nvivx.vixhealthsystem.model.medical.Appointment;
import com.nvivx.vixhealthsystem.service.AppointmentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService service;

    public AppointmentController(AppointmentService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Appointment book(@RequestBody CreateAppointmentRequest req) {
        return service.bookAppointment(req);
    }

    @GetMapping
    public List<Appointment> getAll() {
        return service.getAllAppointments();
    }
}