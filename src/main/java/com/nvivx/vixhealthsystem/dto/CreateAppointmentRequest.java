package com.nvivx.vixhealthsystem.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateAppointmentRequest {

    private LocalDateTime dateTime;
    private int duration;
    private String notes;
    private Long patientId;
    private Long specialistId;
}