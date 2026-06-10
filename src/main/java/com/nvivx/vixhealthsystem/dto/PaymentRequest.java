// src/main/java/com/nvivx/vixhealthsystem/dto/PaymentRequest.java
package com.nvivx.vixhealthsystem.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private int appointmentId;
    private String cardNumber;
    private String expiryDate;
    private String cvv;
    private float amount;

    // Getters and setters
    public int getAppointmentId() { return appointmentId; }
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }
    public float getAmount() { return amount; }
    public void setAmount(float amount) { this.amount = amount; }
}