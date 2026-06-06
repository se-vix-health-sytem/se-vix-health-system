package com.nvivx.vixhealthsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AppointmentResponse {

    private int id;
    private String status;
}