package com.nvivx.vixhealthsystem.controllers;

import com.nvivx.vixhealthsystem.controllers.staff.MedicalSpecialistController;
import com.nvivx.vixhealthsystem.model.person.Patient;
import com.nvivx.vixhealthsystem.model.medical.MedicalRecord;
import com.nvivx.vixhealthsystem.service.core.PatientService;
import com.nvivx.vixhealthsystem.service.medical.MedicalRecordService;
import com.nvivx.vixhealthsystem.service.scheduling.ShiftService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MedicalSpecialistControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MedicalRecordService medicalRecordService;

    @Mock
    private PatientService patientService;

    @Mock
    private ShiftService shiftService;

    @InjectMocks
    private MedicalSpecialistController controller;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void testDashboard() throws Exception {
        mockMvc.perform(get("/medical-specialist/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("medical-specialist/dashboard"))
                .andExpect(model().attributeExists("pageTitle"));
    }

    @Test
    void testSearchForm() throws Exception {
        mockMvc.perform(get("/medical-specialist/patients/search"))
                .andExpect(status().isOk())
                .andExpect(view().name("medical-specialist/patient-search"));
    }

    @Test
    void testSearchPatients() throws Exception {
        when(patientService.searchPatients("john")).thenReturn(List.of());

        mockMvc.perform(post("/medical-specialist/patients/search")
                        .param("query", "john"))
                .andExpect(status().isOk())
                .andExpect(view().name("medical-specialist/search-results"))
                .andExpect(model().attributeExists("patients"))
                .andExpect(model().attribute("query", "john"));

        verify(patientService).searchPatients("john");
    }

    @Test
    void testViewMedicalRecord() throws Exception {
        Patient patient = mock(Patient.class);
        MedicalRecord record = mock(MedicalRecord.class);

        when(patient.getName()).thenReturn("John");
        when(patient.getSurname()).thenReturn("Doe");
        when(patient.getMedicalRecord()).thenReturn(record);

        when(medicalRecordService.getPatientWithMedicalRecord(1L))
                .thenReturn(patient);

        mockMvc.perform(get("/medical-specialist/patients/1/record"))
                .andExpect(status().isOk())
                .andExpect(view().name("medical-specialist/medical-record"))
                .andExpect(model().attributeExists("patient"))
                .andExpect(model().attributeExists("medicalRecord"))
                .andExpect(model().attributeExists("patientId"));

        verify(medicalRecordService).getPatientWithMedicalRecord(1L);
    }

    @Test
    void testAddDiagnosis() throws Exception {
        mockMvc.perform(post("/medical-specialist/patients/1/add-diagnosis")
                        .param("diagnosisName", "Flu")
                        .param("description", "Viral infection")
                        .param("severity", "LOW"))
                .andExpect(status().isOk())
                .andExpect(view().name("medical-specialist/result"))
                .andExpect(model().attributeExists("message"));

        verify(medicalRecordService)
                .addDiagnosis(1L, "Flu", "Viral infection", "LOW");
    }

    @Test
    void testAddPrescription() throws Exception {
        mockMvc.perform(post("/medical-specialist/patients/1/add-prescription")
                        .param("medication", "Paracetamol")
                        .param("dosage", "500mg"))
                .andExpect(status().isOk())
                .andExpect(view().name("medical-specialist/result"))
                .andExpect(model().attributeExists("message"));

        verify(medicalRecordService)
                .addPrescription(1L, 1L, "Paracetamol - 500mg");
    }

    @Test
    void testAddExamResult() throws Exception {
        mockMvc.perform(post("/medical-specialist/patients/1/add-exam-result")
                        .param("examType", "Blood Test")
                        .param("result", "Normal")
                        .param("notes", "All good"))
                .andExpect(status().isOk())
                .andExpect(view().name("medical-specialist/result"))
                .andExpect(model().attributeExists("message"));

        verify(medicalRecordService)
                .addExamResult(1L, "Blood Test", "Normal", "All good");
    }

    @Test
    void testAppointments() throws Exception {
        mockMvc.perform(get("/medical-specialist/appointments"))
                .andExpect(status().isOk())
                .andExpect(view().name("medical-specialist/appointments"));
    }

    @Test
    void testCalendar() throws Exception {
        mockMvc.perform(get("/medical-specialist/calendar"))
                .andExpect(status().isOk())
                .andExpect(view().name("medical-specialist/calendar"));
    }

    @Test
    void testSchedule() throws Exception {
        mockMvc.perform(get("/medical-specialist/my-schedule"))
                .andExpect(status().isOk())
                .andExpect(view().name("medical-specialist/schedule"));
    }
}