package com.nvivx.vixhealthsystem.repository;

import com.nvivx.vixhealthsystem.model.person.Patient;
import com.nvivx.vixhealthsystem.model.person.employee.Employee;
import com.nvivx.vixhealthsystem.model.resource.Resource;
import com.nvivx.vixhealthsystem.model.facility.MedicalFacility;
import com.nvivx.vixhealthsystem.model.facility.Room;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RepositoryTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private MedicalFacilityRepository medicalFacilityRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Test
    void shouldConnectToDatabase() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            assertNotNull(connection);
            assertFalse(connection.isClosed());

            System.out.println(
                    "Connected to: "
                            + connection.getMetaData().getURL()
            );
        }
    }

    @Test
    void shouldPrintAllPatients() {
        List<Patient> patients = patientRepository.findAll();

        assertFalse(patients.isEmpty());

        System.out.println("===== PATIENTS =====");
        for (Patient p : patients) {
            System.out.println(
                    p.getId() + " - "
                            + p.getName() + " "
                            + p.getSurname() + " | Fiscal code: "
                            + p.getFiscalCode()
            );
        }
    }

    @Test
    void shouldPrintAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();

        assertFalse(employees.isEmpty());

        System.out.println("===== EMPLOYEES =====");
        for (Employee e : employees) {
            System.out.println(
                    e.getId() + " - "
                            + e.getName() + " "
                            + e.getSurname() + " | Class: "
                            + e.getClass().getSimpleName()
            );
        }
    }

    @Test
    void shouldPrintAllResources() {
        List<Resource> resources = resourceRepository.findAll();

        assertFalse(resources.isEmpty());

        System.out.println("===== RESOURCES =====");
        for (Resource r : resources) {
            System.out.println(
                    r.getId() + " - "
                            + r.getName() + " | Price: "
                            + r.getPrice()
            );
        }
    }

    @Test
    void shouldPrintAllMedicalFacilities() {
        List<MedicalFacility> facilities =
                medicalFacilityRepository.findAll();

        assertFalse(facilities.isEmpty());

        System.out.println("===== MEDICAL FACILITIES =====");
        for (MedicalFacility f : facilities) {
            System.out.println(
                    f.getId() + " - "
                            + f.getName() + " | Class: "
                            + f.getClass().getSimpleName()
            );
        }
    }

    @Test
    void shouldPrintAllRooms() {
        List<Room> rooms = roomRepository.findAll();

        assertFalse(rooms.isEmpty());

        System.out.println("===== ROOMS =====");
        for (Room r : rooms) {
            System.out.println(
                    r.getId() + " - "
                            + r.getNumber() + " | Class: "
                            + r.getClass().getSimpleName()
            );
        }
    }
}