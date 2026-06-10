package com.nvivx.vixhealthsystem.model.staff;

import java.time.LocalDate;

public class Shift {

    private Long id;           // Changed from long to Long
    private Long employeeId;   // Changed from long to Long
    private LocalDate date;
    private String shiftType;
    private String notes;

    public Shift() {}

    public Shift(Long id, Long employeeId, LocalDate date, String shiftType, String notes) {
        this.id = id;
        this.employeeId = employeeId;
        this.date = date;
        this.shiftType = shiftType;
        this.notes = notes;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getShiftType() { return shiftType; }
    public void setShiftType(String shiftType) { this.shiftType = shiftType; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}