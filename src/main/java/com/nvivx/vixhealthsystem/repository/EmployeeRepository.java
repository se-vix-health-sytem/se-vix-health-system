package com.nvivx.vixhealthsystem.repository;

import com.nvivx.vixhealthsystem.database.DBManager;
import com.nvivx.vixhealthsystem.model.person.employee.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRepository {

    private static final String TABLE_NAME = "Employees";

    public Employee findById(long id) {

        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";

        try (
                Connection conn = DBManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setLong(1, id);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapEmployee(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Employee> findAll() {

        List<Employee> employees = new ArrayList<>();

        String sql = "SELECT * FROM " + TABLE_NAME;

        try (
                Connection conn = DBManager.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
        ) {

            while (rs.next()) {
                employees.add(mapEmployee(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return employees;
    }

    public void delete(long id) {

        String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";

        try (
                Connection conn = DBManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Employee mapEmployee(ResultSet rs) throws SQLException {

        String employeeType = rs.getString("type");

        Employee employee;

        switch (employeeType) {

            case "MEDICAL_SPECIALIST":
                MedicalSpecialist ms = new MedicalSpecialist();
                ms.setSpecialty(rs.getString("specialty"));
                ms.setLicenseNumber(rs.getString("license_number"));
                employee = ms;
                break;

            case "SECRETARY":
                Secretary secretary = new Secretary();
                secretary.setRole(rs.getString("secretary_type"));
                employee = secretary;
                break;

            case "TECHNICIAN":
                employee = new Technician();
                break;

            case "BUYER":
                employee = new Buyer();
                break;

            case "STAFF_MANAGER":
                employee = new StaffManager();
                break;

            default:
                employee = new Employee();
        }

        employee.setId(rs.getInt("id"));

        employee.setName(rs.getString("name"));
        employee.setSurname(rs.getString("surname"));

        employee.setBirthDate(
                rs.getDate("birth_date") != null
                        ? rs.getDate("birth_date").toLocalDate()
                        : null
        );

        employee.setBirthPlace(rs.getString("birth_place"));
        employee.setGender(rs.getString("gender").charAt(0));

        employee.setEmail(rs.getString("email"));
        employee.setPhoneNumber(rs.getString("phone"));

        employee.setHireDate(
                rs.getDate("hire_date") != null
                        ? rs.getDate("hire_date").toLocalDate()
                        : null
        );

        return employee;
    }
}