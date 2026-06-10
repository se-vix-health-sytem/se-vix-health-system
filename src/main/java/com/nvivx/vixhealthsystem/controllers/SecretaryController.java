package com.nvivx.vixhealthsystem.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

// @Controller means this class handles HTTP requests and returns HTML page names (Thymeleaf views).
// @RequestMapping("/secretary") means ALL routes in this class start with /secretary
// e.g. @GetMapping("/dashboard") becomes the full URL:  GET /secretary/dashboard
@Controller
@RequestMapping("/secretary")
public class SecretaryController {

    // GET /secretary/dashboard  →  shows the secretary's home page
    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        model.addAttribute("pageTitle", "Secretary Dashboard");
        return "secretary/dashboard";
    }

    // ─────────────────────────────────────────────
    // UC14 — BOOK APPOINTMENT ON BEHALF OF PATIENT
    // ─────────────────────────────────────────────

    // GET /secretary/appointments/book  →  shows the booking form page
    @GetMapping("/appointments/book")
    public String showBookAppointmentForm(Model model) {
        model.addAttribute("pageTitle", "Book Appointment for Patient");
        return "secretary/book-appointment";
    }

    // POST /secretary/appointments/book  →  receives the filled form and confirms it
    // @RequestParam String patientName  →  reads the "patientName" field from the HTML form
    @PostMapping("/appointments/book")
    public String bookAppointmentForPatient(
            @RequestParam String patientName,      // patient's name entered by secretary
            @RequestParam String patientSurname,   // patient's surname
            @RequestParam String doctorName,       // selected doctor
            @RequestParam String dateTime,         // chosen date and time slot
            Model model) {

        // TEMPORARY: just echo the data back to prove front-end → back-end connection works.
        // FUTURE: will call secretaryAppointmentService.bookForPatient(patientName, doctorName, dateTime)
        //         which will: find the patient by name, check the slot in the calendar, create Appointment.
        model.addAttribute("pageTitle", "Appointment Booked Successfully");
        model.addAttribute("message",
                "✅ Appointment Booked!\n\n" +
                        "Patient: " + patientName + " " + patientSurname + "\n" +
                        "Doctor: " + doctorName + "\n" +
                        "Date & Time: " + dateTime + "\n\n" +
                        "A confirmation email has been sent to the patient.\n" +
                        "Backend received and processed the booking request.");
        return "secretary/result";
        // renders templates/secretary/result.html
    }

    // ─────────────────────────────────────────────
    // UC15 — RESCHEDULE APPOINTMENT ON BEHALF OF PATIENT
    // ─────────────────────────────────────────────

    // GET /secretary/appointments/reschedule  →  shows the rescheduling form
    @GetMapping("/appointments/reschedule")
    public String showRescheduleForm(Model model) {
        model.addAttribute("pageTitle", "Reschedule Appointment for Patient");
        return "secretary/reschedule-appointment";
        // renders templates/secretary/reschedule-appointment.html
    }

    // POST /secretary/appointments/reschedule  →  processes the rescheduling
    @PostMapping("/appointments/reschedule")
    public String rescheduleAppointment(
            @RequestParam String patientName,
            @RequestParam Long appointmentId,    // ID of the existing appointment to change
            @RequestParam String newDateTime,    // the new date/time the patient wants
            Model model) {

        // TEMPORARY: echo data back.
        // FUTURE: will call secretaryAppointmentService.rescheduleForPatient(appointmentId, newDateTime)
        //         which will: find the appointment, check the new slot, update the calendar record.
        model.addAttribute("pageTitle", "Appointment Rescheduled Successfully");
        model.addAttribute("message",
                "✅ Appointment Rescheduled!\n\n" +
                        "Patient: " + patientName + "\n" +
                        "Appointment ID: " + appointmentId + "\n" +
                        "New Date & Time: " + newDateTime + "\n\n" +
                        "Patient has been notified by email.\n" +
                        "Backend processed the reschedule request.");
        return "secretary/result";
    }

    // ─────────────────────────────────────────────
    // UC16 — CANCEL APPOINTMENT ON BEHALF OF PATIENT
    // ─────────────────────────────────────────────

    // GET /secretary/appointments/cancel  →  shows the cancellation form
    @GetMapping("/appointments/cancel")
    public String showCancelAppointmentForm(Model model) {
        model.addAttribute("pageTitle", "Cancel Appointment for Patient");
        return "secretary/cancel-appointment";
        // renders templates/secretary/cancel-appointment.html
    }

    // POST /secretary/appointments/cancel  →  processes the cancellation
    @PostMapping("/appointments/cancel")
    public String cancelAppointment(
            @RequestParam String patientName,
            @RequestParam Long appointmentId,    // which appointment to delete
            Model model) {

        // TEMPORARY: echo data.
        // FUTURE: will call secretaryAppointmentService.cancelForPatient(appointmentId)
        //         which will: find the appointment by ID, remove it, notify the patient.
        model.addAttribute("pageTitle", "Appointment Cancelled");
        model.addAttribute("message",
                "✅ Appointment Cancelled!\n\n" +
                        "Patient: " + patientName + "\n" +
                        "Appointment ID: " + appointmentId + " has been removed.\n\n" +
                        "Patient has been notified by email.\n" +
                        "Backend processed the cancellation.");
        return "secretary/result";
    }

    // ─────────────────────────────────────────────
    // UC21 — VIEW BED AND ROOM AVAILABILITY
    // ─────────────────────────────────────────────

    // GET /secretary/rooms  →  shows the room/bed availability dashboard
    @GetMapping("/rooms")
    public String viewRoomAvailability(Model model) {
        // TODO: TEMPORARY - Testing frontend-backend communication
        // FUTURE: will call roomService.getAllRoomsWithBedStatus()
        //         which returns List<Room>, each room containing its List<Bed> with BedStatus (FREE/OCCUPIED)
        model.addAttribute("pageTitle", "Bed & Room Availability");
        model.addAttribute("message",
                "BACKEND RECEIVED: Opening room availability dashboard.\n" +
                        "Room and bed data will be loaded and displayed here.");
        return "secretary/rooms";
        // renders templates/secretary/rooms.html
    }

    // ─────────────────────────────────────────────
    // UC22 — TRIAGE (ADMIT PATIENT TO A BED)
    // ─────────────────────────────────────────────

    // POST /secretary/rooms/admit  →  assigns a bed to a patient
    @PostMapping("/rooms/admit")
    public String admitPatient(
            @RequestParam String roomNumber,     // which room the bed is in
            @RequestParam String bedId,          // which specific bed
            @RequestParam String patientName,
            @RequestParam String patientSurname,
            @RequestParam String dateOfBirth,    // to confirm patient identity
            Model model) {

        // TEMPORARY: echo data.
        // FUTURE: will call roomService.assignBedToPatient(bedId, patientName, patientSurname, dateOfBirth)
        //         which will: find the Bed by ID, set its BedStatus to OCCUPIED, link the patient to it.
        model.addAttribute("pageTitle", "Patient Admitted Successfully");
        model.addAttribute("message",
                "✅ Patient Admitted!\n\n" +
                        "Patient: " + patientName + " " + patientSurname + "\n" +
                        "Date of Birth: " + dateOfBirth + "\n" +
                        "Room: " + roomNumber + " — Bed: " + bedId + "\n\n" +
                        "Bed status set to OCCUPIED.\n" +
                        "Medical staff notified to escort patient to the room.\n" +
                        "Backend processed the admission.");
        return "secretary/result";
    }

    // ─────────────────────────────────────────────
    // UC23 — PATIENT DISMISSAL (FREE A BED)
    // ─────────────────────────────────────────────

    // POST /secretary/rooms/dismiss  →  frees a bed when a patient is discharged
    @PostMapping("/rooms/dismiss")
    public String dismissPatient(
            @RequestParam String bedId,          // which bed to free
            @RequestParam String patientName,
            Model model) {

        // TEMPORARY: echo data.
        // FUTURE: will call roomService.freeBed(bedId)
        //         which will: find the Bed by ID, set BedStatus to FREE, remove the patient link.
        model.addAttribute("pageTitle", "Patient Dismissed Successfully");
        model.addAttribute("message",
                "✅ Patient Dismissed!\n\n" +
                        "Patient: " + patientName + "\n" +
                        "Bed ID: " + bedId + " is now FREE.\n\n" +
                        "Bed status updated in the system.\n" +
                        "Backend processed the dismissal.");
        return "secretary/result";
    }
}
