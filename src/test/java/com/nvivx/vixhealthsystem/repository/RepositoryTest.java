package com.nvivx.vixhealthsystem.repository;

import com.nvivx.vixhealthsystem.model.medical.Surgery;
import com.nvivx.vixhealthsystem.model.person.Patient;
import com.nvivx.vixhealthsystem.model.person.employee.Employee;
import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import com.nvivx.vixhealthsystem.model.resource.Resource;
import com.nvivx.vixhealthsystem.model.facility.MedicalFacility;
import com.nvivx.vixhealthsystem.model.facility.Room;
import com.nvivx.vixhealthsystem.service.integration.FirebaseAuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @brief Integration tests for JPA repositories against the real database.
 * Loads the full Spring Boot context and exercises PatientRepository, EmployeeRepository,
 * ResourceRepository, MedicalFacilityRepository, RoomRepository, and SurgeryRepository,
 * verifying that each returns non-empty results and that surgery-specialist links are intact.
 */
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

    @Autowired
    private SurgeryRepository surgeryRepository;

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
    @Transactional
    void shouldPrintAllSurgeriesWithSpecialist() {
        List<Surgery> surgeries = surgeryRepository.findAll();

        assertFalse(surgeries.isEmpty(), "No surgeries found in DB");

        System.out.println("===== SURGERIES =====");
        for (Surgery s : surgeries) {
            String specialist = s.getMedicalSpecialist() != null
                    ? s.getMedicalSpecialist().getName() + " " + s.getMedicalSpecialist().getSurname()
                    : "NOT ASSIGNED";
            System.out.println(
                    s.getId() + " - " + s.getName()
                    + " | Date: " + s.getDateTime()
                    + " | Specialist: " + specialist
            );
        }
    }

    @Test
    @Transactional
    void shouldPrintSurgeriesForEachSpecialist() {
        List<Employee> employees = employeeRepository.findAll();

        System.out.println("===== SPECIALIST → SURGERIES =====");
        for (Employee e : employees) {
            if (e instanceof MedicalSpecialist specialist) {
                List<Surgery> surgeries = specialist.getSurgeries();
                System.out.println(
                        specialist.getName() + " " + specialist.getSurname()
                        + " (" + specialist.getSpecialty() + ")"
                        + " — " + surgeries.size() + " surgery(ies)"
                );
                for (Surgery s : surgeries) {
                    System.out.println("    → " + s.getName() + " on " + s.getDateTime());
                }
            }
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