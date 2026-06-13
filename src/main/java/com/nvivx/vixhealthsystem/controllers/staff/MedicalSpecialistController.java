package com.nvivx.vixhealthsystem.controllers.staff;

import com.nvivx.vixhealthsystem.model.facility.Room;
import com.nvivx.vixhealthsystem.model.facility.SpecializedRoom;
import com.nvivx.vixhealthsystem.model.medical.Appointment;
import com.nvivx.vixhealthsystem.model.medical.Surgery;
import com.nvivx.vixhealthsystem.model.person.Patient;
import com.nvivx.vixhealthsystem.model.person.employee.Employee;
import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import com.nvivx.vixhealthsystem.model.staff.Shift;
import com.nvivx.vixhealthsystem.repository.JsonAppointmentRepository;
import com.nvivx.vixhealthsystem.repository.RoomRepository;
import com.nvivx.vixhealthsystem.repository.SurgeryRepository;
import com.nvivx.vixhealthsystem.service.AuditService;
import com.nvivx.vixhealthsystem.service.medical.MedicalRecordService;
import com.nvivx.vixhealthsystem.service.core.PatientService;
import com.nvivx.vixhealthsystem.service.scheduling.ShiftService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/medical-specialist")
public class MedicalSpecialistController {

    private final MedicalRecordService medicalRecordService;
    private final PatientService patientService;
    private final ShiftService shiftService;
    private final SurgeryRepository surgeryRepository;
    private final RoomRepository roomRepository;
    private final JsonAppointmentRepository appointmentRepository;
    private final AuditService auditService;

    public MedicalSpecialistController(MedicalRecordService medicalRecordService,
                                       PatientService patientService,
                                       ShiftService shiftService,
                                       SurgeryRepository surgeryRepository,
                                       RoomRepository roomRepository,
                                       JsonAppointmentRepository appointmentRepository,
                                       AuditService auditService) {
        this.medicalRecordService = medicalRecordService;
        this.patientService = patientService;
        this.shiftService = shiftService;
        this.surgeryRepository = surgeryRepository;
        this.roomRepository = roomRepository;
        this.appointmentRepository = appointmentRepository;
        this.auditService = auditService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "Medical Specialist Dashboard");
        model.addAttribute("currentPage", "dashboard");
        return "medical-specialist/dashboard";
    }

    @GetMapping("/patients/search")
    public String searchPatientsForm(Model model) {
        model.addAttribute("pageTitle", "Search Patients");
        model.addAttribute("currentPage", "patients");
        return "medical-specialist/patient-search";
    }

    @PostMapping("/patients/search")
    public String searchPatients(@RequestParam String query, Model model) {
        var patients = patientService.searchPatients(query);
        model.addAttribute("pageTitle", "Search Results");
        model.addAttribute("currentPage", "patients");
        model.addAttribute("patients", patients);
        model.addAttribute("query", query);
        return "medical-specialist/search-results";
    }

    @GetMapping("/patients/{patientId}/record")
    public String viewMedicalRecord(@PathVariable Long patientId, Model model) {
        Patient patient = medicalRecordService.getPatientWithMedicalRecord(patientId);
        List<SpecializedRoom> specializedRooms = roomRepository.findAll().stream()
            .filter(r -> r instanceof SpecializedRoom)
            .map(r -> (SpecializedRoom) r)
            .toList();
        model.addAttribute("pageTitle", "Medical Record - " + patient.getName() + " " + patient.getSurname());
        model.addAttribute("currentPage", "patients");
        model.addAttribute("patient", patient);
        model.addAttribute("medicalRecord", patient.getMedicalRecord());
        model.addAttribute("patientId", patientId);
        model.addAttribute("specializedRooms", specializedRooms);
        return "medical-specialist/medical-record";
    }

    @PostMapping("/patients/{patientId}/add-surgery")
    public String addSurgery(@PathVariable Long patientId,
                             @RequestParam String surgeryName,
                             @RequestParam(required = false) String surgeryDescription,
                             @RequestParam String surgeryDateTime,
                             @RequestParam Long roomId,
                             HttpSession session,
                             Model model) {
        try {
            Patient patient = medicalRecordService.getPatientWithMedicalRecord(patientId);
            if (patient.getMedicalRecord() == null) {
                throw new RuntimeException("Patient has no medical record");
            }
            Room roomEntity = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found: " + roomId));
            if (!(roomEntity instanceof SpecializedRoom)) {
                throw new RuntimeException("Selected room is not a specialized operating room");
            }
            Employee user = (Employee) session.getAttribute("user");
            MedicalSpecialist specialist = (user instanceof MedicalSpecialist ms) ? ms : null;

            Surgery surgery = new Surgery();
            surgery.setName(surgeryName);
            surgery.setDescription(surgeryDescription);
            surgery.setDateTime(LocalDateTime.parse(surgeryDateTime));
            surgery.setSpecializedRoom((SpecializedRoom) roomEntity);
            surgery.setMedicalSpecialist(specialist);
            surgery.setMedicalRecord(patient.getMedicalRecord());
            surgeryRepository.save(surgery);
            auditService.log("ADD_SURGERY", "MedicalRecord",
                String.valueOf(patient.getMedicalRecord().getId()),
                "Scheduled surgery '" + surgeryName + "' for patient " + patientId);

            model.addAttribute("message", "✅ Surgery scheduled successfully for " + patient.getName() + " " + patient.getSurname() + "!");
        } catch (Exception e) {
            model.addAttribute("message", "❌ Error scheduling surgery: " + e.getMessage());
        }
        model.addAttribute("pageTitle", "Surgery Scheduled");
        model.addAttribute("currentPage", "patients");
        return "medical-specialist/result";
    }

    @PostMapping("/patients/{patientId}/add-diagnosis")
    public String addDiagnosis(@PathVariable Long patientId,
                               @RequestParam String diagnosisName,
                               @RequestParam String description,
                               @RequestParam String severity,
                               Model model) {
        try {
            medicalRecordService.addDiagnosis(patientId, diagnosisName, description, severity);
            model.addAttribute("pageTitle", "Diagnosis Added");
            model.addAttribute("message", "✅ Diagnosis added successfully!");
        } catch (Exception e) {
            model.addAttribute("pageTitle", "Error");
            model.addAttribute("message", "❌ Error: " + e.getMessage());
        }
        model.addAttribute("currentPage", "patients");
        return "medical-specialist/result";
    }

    @PostMapping("/patients/{patientId}/add-prescription")
    public String addPrescription(@PathVariable Long patientId,
                                  @RequestParam String medication,
                                  @RequestParam String dosage,
                                  HttpSession session,
                                  Model model) {
        try {
            com.nvivx.vixhealthsystem.model.person.employee.Employee user =
                    (com.nvivx.vixhealthsystem.model.person.employee.Employee) session.getAttribute("user");
            Long specialistId = (user != null) ? user.getId() : 1L;
            String fullMedication = medication + " - " + dosage;
            medicalRecordService.addPrescription(patientId, specialistId, fullMedication);
            model.addAttribute("pageTitle", "Prescription Added");
            model.addAttribute("message", "✅ Prescription added successfully!");
        } catch (Exception e) {
            model.addAttribute("pageTitle", "Error");
            model.addAttribute("message", "❌ Error: " + e.getMessage());
        }
        model.addAttribute("currentPage", "patients");
        return "medical-specialist/result";
    }

    @PostMapping("/patients/{patientId}/add-exam-result")
    public String addExamResult(@PathVariable Long patientId,
                                @RequestParam String examType,
                                @RequestParam String result,
                                @RequestParam(required = false) String notes,
                                Model model) {
        try {
            medicalRecordService.addExamResult(patientId, examType, result, notes);
            model.addAttribute("pageTitle", "Exam Result Added");
            model.addAttribute("message", "✅ Exam result added successfully!");
        } catch (Exception e) {
            model.addAttribute("pageTitle", "Error");
            model.addAttribute("message", "❌ Error: " + e.getMessage());
        }
        model.addAttribute("currentPage", "patients");
        return "medical-specialist/result";
    }

    @GetMapping("/appointments")
    public String viewAppointments(HttpSession session, Model model) {
        Employee user = (Employee) session.getAttribute("user");
        List<Appointment> myAppointments = new java.util.ArrayList<>();
        if (user != null) {
            try {
                myAppointments = appointmentRepository.findAll().stream()
                    .filter(a -> a.getMedicalSpecialist() != null
                             && a.getMedicalSpecialist().getId() != null
                             && a.getMedicalSpecialist().getId().equals(user.getId()))
                    .sorted(java.util.Comparator.comparing(
                        a -> a.getDateTime() != null ? a.getDateTime() : java.time.LocalDateTime.MAX))
                    .collect(Collectors.toList());
            } catch (Exception ignored) {}
        }
        model.addAttribute("appointments", myAppointments);
        model.addAttribute("pageTitle", "My Appointments");
        model.addAttribute("currentPage", "appointments");
        return "medical-specialist/appointments";
    }

    @GetMapping("/calendar")
    public String viewCalendar(HttpSession session, Model model) {
        com.nvivx.vixhealthsystem.model.person.employee.Employee user =
                (com.nvivx.vixhealthsystem.model.person.employee.Employee) session.getAttribute("user");
        if (user != null) {
            LocalDate today = LocalDate.now();
            List<Shift> upcoming = shiftService.getShiftsForEmployee(user.getId()).stream()
                    .filter(s -> s.getDate() != null && !s.getDate().isBefore(today))
                    .sorted(java.util.Comparator.comparing(Shift::getDate))
                    .collect(Collectors.toList());
            // Group by "MMMM yyyy" label for section headers
            Map<String, List<Shift>> shiftsByMonth = upcoming.stream()
                    .collect(Collectors.groupingBy(
                            s -> s.getDate().getMonth().getDisplayName(
                                    java.time.format.TextStyle.FULL, java.util.Locale.ENGLISH)
                                    + " " + s.getDate().getYear(),
                            LinkedHashMap::new,
                            Collectors.toList()));
            model.addAttribute("shiftsByMonth", shiftsByMonth);
        }
        model.addAttribute("pageTitle", "My Calendar");
        model.addAttribute("currentPage", "calendar");
        return "medical-specialist/calendar";
    }

    @GetMapping("/my-schedule")
    public String viewMySchedule(HttpSession session, Model model) {
        Employee user = (Employee) session.getAttribute("user");
        if (user != null) {
            List<Shift> shifts = shiftService.getShiftsForEmployee(user.getId());
            model.addAttribute("shifts", shifts);
        }
        model.addAttribute("pageTitle", "My Schedule");
        model.addAttribute("currentPage", "schedule");
        return "medical-specialist/schedule";
    }

    @GetMapping("/surgeries")
    public String viewSurgeries(HttpSession session, Model model) {
        Employee user = (Employee) session.getAttribute("user");
        List<Surgery> surgeries = new java.util.ArrayList<>();
        if (user != null) {
            surgeries = surgeryRepository.findByMedicalSpecialistId(user.getId());
        }
        model.addAttribute("surgeries", surgeries);
        model.addAttribute("pageTitle", "My Surgeries");
        model.addAttribute("currentPage", "surgeries");
        return "medical-specialist/surgeries";
    }
}
