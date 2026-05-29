package com.nvivx.vixhealthsystem.model.person.employee;

import com.nvivx.vixhealthsystem.model.person.Person;
import com.nvivx.vixhealthsystem.model.resource.Resource;

import java.time.LocalDate;

public class Employee extends Person {
    private int id;
    private LocalDate hireDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    protected Resource[] viewResources() {
        return null;
    }
    protected void takeResource(Resource r){

    }
}
