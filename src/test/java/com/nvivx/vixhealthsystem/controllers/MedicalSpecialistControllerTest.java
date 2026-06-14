package com.nvivx.vixhealthsystem.controllers.staff;

import com.nvivx.vixhealthsystem.model.facility.Room;
import com.nvivx.vixhealthsystem.model.medical.Appointment;
import com.nvivx.vixhealthsystem.model.person.Patient;
import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import com.nvivx.vixhealthsystem.repository.JsonAppointmentRepository;
import com.nvivx.vixhealthsystem.repository.RoomRepository;
import com.nvivx.vixhealthsystem.repository.SurgeryRepository;
import com.nvivx.vixhealthsystem.service.AuditService;
import com.nvivx.vixhealthsystem.service.core.EmployeeService;
import com.nvivx.vixhealthsystem.service.core.PatientService;
import com.nvivx.vixhealthsystem.service.medical.MedicalRecordService;
import com.nvivx.vixhealthsystem.service.scheduling.ShiftService;
import com.nvivx.vixhealthsystem.service.scheduling.VacationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @class MedicalSpecialistControllerTest
 * @brief Unit tests for MedicalSpecialistController (clinical workflow module).
 *
 * These tests validate key medical specialist actions such as:
 * - Dashboard access
 * - Patient search and record viewing
 * - Adding diagnoses, prescriptions, and exam results
 * - Viewing appointments and profile
 * - Authentication guard for calendar access
 *
 * The controller is tested using standalone MockMvc with mocked dependencies.
 */
@ExtendWith(MockitoExtension.class)
class MedicalSpecialistControllerTest {

    /// MockMvc instance used to simulate HTTP requests to the controller.
    private MockMvc mockMvc;

    /// Controller under test with injected mocked dependencies.
    @InjectMocks
    private MedicalSpecialistController controller;

    // ---------------- MOCKED DEPENDENCIES ----------------

    @Mock private MedicalRecordService medicalRecordService;
    @Mock private PatientService patientService;
    @Mock private ShiftService shiftService;
    @Mock private VacationService vacationService;
    @Mock private SurgeryRepository surgeryRepository;
    @Mock private RoomRepository roomRepository;
    @Mock private JsonAppointmentRepository appointmentRepository;
    @Mock private AuditService auditService;
    @Mock private EmployeeService employeeService;

    /// Mock HTTP session used to simulate logged-in medical specialist.
    private MockHttpSession session;

    /**
     * @brief Initializes MockMvc and session before each test.
     *
     * A valid MedicalSpecialist user is stored in session to simulate authentication.
     */
    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        session = new MockHttpSession();

        // Simulate logged-in medical specialist
        MedicalSpecialist specialist = new MedicalSpecialist();
        specialist.setId(1L);

        session.setAttribute("user", specialist);
    }

    // =========================================================
    // DASHBOARD
    // =========================================================

    /**
     * @brief Verifies that the dashboard loads correctly.
     */
    @Test
    void dashboard_returnsView() throws Exception {

        mockMvc.perform(get("/medical-specialist/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("medical-specialist/dashboard"))
                .andExpect(model().attributeExists("pageTitle"));
    }

    // =========================================================
    // PATIENT SEARCH
    // =========================================================

    /**
     * @brief Verifies that patient search returns correct results.
     */
    @Test
    void searchPatients_returnsResults() throws Exception {

        when(patientService.searchPatients("john"))
                .thenReturn(List.of(new Patient()));

        mockMvc.perform(post("/medical-specialist/patients/search")
                        .param("query", "john"))
                .andExpect(status().isOk())
                .andExpect(view().name("medical-specialist/search-results"))
                .andExpect(model().attributeExists("patients"));

        verify(patientService).searchPatients("john");
    }

    // =========================================================
    // MEDICAL RECORD
    // =========================================================

    /**
     * @brief Verifies that patient medical record page loads correctly.
     */
    @Test
    void viewMedicalRecord_returnsView() throws Exception {

        Patient patient = mock(Patient.class);
        when(patient.getName()).thenReturn("John");
        when(patient.getSurname()).thenReturn("Doe");

        when(medicalRecordService.getPatientWithMedicalRecord(1L))
                .thenReturn(patient);

        when(roomRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/medical-specialist/patients/1/record"))
                .andExpect(status().isOk())
                .andExpect(view().name("medical-specialist/medical-record"))
                .andExpect(model().attributeExists("patient"));
    }

    // =========================================================
    // DIAGNOSIS
    // =========================================================

    /**
     * @brief Verifies successful diagnosis creation.
     */
    @Test
    void addDiagnosis_success() throws Exception {

        mockMvc.perform(post("/medical-specialist/patients/1/add-diagnosis")
                        .param("diagnosisName", "Flu")
                        .param("severity", "LOW"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/medical-specialist/patients/1/record"));

        verify(medicalRecordService)
                .addDiagnosis(eq(1L), eq("Flu"), anyString(), eq("LOW"));
    }

    // =========================================================
    // PRESCRIPTION
    // =========================================================

    /**
     * @brief Verifies successful prescription creation.
     */
    @Test
    void addPrescription_success() throws Exception {

        mockMvc.perform(post("/medical-specialist/patients/1/add-prescription")
                        .session(session)
                        .param("medication", "Ibuprofen")
                        .param("dosage", "200mg"))
                .andExpect(status().is3xxRedirection());

        verify(medicalRecordService)
                .addPrescription(eq(1L), eq(1L), eq("Ibuprofen - 200mg"));
    }

    // =========================================================
    // EXAM RESULTS
    // =========================================================

    /**
     * @brief Verifies successful exam result submission.
     */
    @Test
    void addExamResult_success() throws Exception {

        mockMvc.perform(post("/medical-specialist/patients/1/add-exam-result")
                        .param("examType", "Blood Test")
                        .param("result", "Normal"))
                .andExpect(status().is3xxRedirection());

        verify(medicalRecordService)
                .addExamResult(eq(1L), eq("Blood Test"), eq("Normal"), isNull());
    }

    // =========================================================
    // APPOINTMENTS
    // =========================================================

    /**
     * @brief Verifies that appointments page loads correctly.
     */
    @Test
    void viewAppointments_returnsView() throws Exception {

        when(appointmentRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/medical-specialist/appointments")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("medical-specialist/appointments"))
                .andExpect(model().attributeExists("appointments"));
    }

    // =========================================================
    // PROFILE
    // =========================================================

    /**
     * @brief Verifies profile page rendering for logged-in specialist.
     */
    @Test
    void profile_returnsView() throws Exception {

        MedicalSpecialist specialist = new MedicalSpecialist();
        specialist.setId(1L);

        when(employeeService.findById(1L)).thenReturn(specialist);

        mockMvc.perform(get("/medical-specialist/profile")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("employee/profile"));
    }

    // =========================================================
    // AUTH GUARD
    // =========================================================

    /**
     * @brief Verifies redirect to login when accessing calendar without authentication.
     */
    @Test
    void calendar_redirects_when_not_logged_in() throws Exception {

        mockMvc.perform(get("/medical-specialist/calendar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}