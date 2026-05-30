package com.nvivx.vixhealthsystem.model.medical;

public class MedicalRecord {
    private float height;
    private float weight;
    private String bloodType;
    private String[] allergies;
    private String[] vaccines;

    public MedicalRecord(float height, float weight,String bloodType, String[] allergies, String[] vaccines){
        this.height = height;
        this.weight = weight;
        this.bloodType = bloodType;
        this.allergies = allergies;
        this.vaccines = vaccines;
    }


    public float getHeight() {
        return height;
    }
    public void setHeight(float height) {
        this.height = height;
    }


    public float getWeight() {
        return weight;
    }
    public void setWeight(float weight) {
        this.weight = weight;
    }


    public String getBloodType() {
        return bloodType;
    }
    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }


    public String[] getAllergies() {
        return allergies;
    }
    public void setAllergies(String[] allergies) {
        this.allergies = allergies;
    }


    public String[] getVaccines() {
        return vaccines;
    }
    public void setVaccines(String[] vaccines) {
        this.vaccines = vaccines;
    }
}
