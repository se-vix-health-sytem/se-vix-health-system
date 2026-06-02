package com.nvivx.vixhealthsystem.model.facility;

import com.nvivx.vixhealthsystem.model.person.employee.Employee;

public class Office extends Room{
    private Employee employee;

    public Office(String number, Employee employee) {
        super(number);
        this.employee = employee;
    }

    public void assignEmployee(Employee e) {
        employee = e;
    }

    public Employee getEmployee() {
        return employee;
    }
}
