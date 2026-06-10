// src/main/java/com/nvivx/vixhealthsystem/dto/PaymentStatus.java
package com.nvivx.vixhealthsystem.dto;

import lombok.Data;

@Data
public class PaymentStatus {
    private String status;
    private String timestamp;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}