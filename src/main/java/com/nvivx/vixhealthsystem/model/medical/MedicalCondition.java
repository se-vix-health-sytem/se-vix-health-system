package com.nvivx.vixhealthsystem.model.medical;

import java.time.LocalDate;

public class MedicalCondition {
    private String name;
    private LocalDate dateOfDiagnosis;
    private String type;
    private String description;
    private String treatment;

    public MedicalCondition(String name, LocalDate dateOfDiagnosis, String type, String description, String treatment) {
        this.name = name;
        this.dateOfDiagnosis = dateOfDiagnosis;
        this.type = type;
        this.description = description;
        this.treatment = treatment;
    }


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }


    public LocalDate getDateOfDiagnosis() {
        return dateOfDiagnosis;
    }
    public void setDateOfDiagnosis(LocalDate dateOfDiagnosis) {
        this.dateOfDiagnosis = dateOfDiagnosis;
    }


    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }


    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }


    public String getTreatment() {
        return treatment;
    }
    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }
}
