package com.nvivx.vixhealthsystem.model.person.employee;
import com.nvivx.vixhealthsystem.model.person.Patient;
import com.nvivx.vixhealthsystem.model.facility.InternationRoom;
import java.time.LocalDateTime;

/**
 * Handles administrative tasks like appointment scheduling and patient admissions.
 *
 * The 'role' attribute differentiates different kinds of secretaries:
 * - "front office" (greets patients, general inquiries)
 * - "admissions" (handles patient check-in and room assignment)
 * - "billing" (handles payments and insurance)
 *
 * @see Employee
 */

public class Secretary extends Employee {
    private String role;

    // ========== Getters and Setters ==========

    public void setRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    // ========== Appointment Management Methods ==========

    /**
     * Books a new appointment for a patient with a medical specialist.
     * Checks availability before creating the appointment.
     *
     * @param p the patient who needs the appointment
     * @param m the medical specialist to see
     * @param dt the desired date and time
     */

    public void makeAppointmentForPatient(Patient p, MedicalSpecialist m, LocalDateTime dt) {

        // Will check if specialist is available at dt
        // Then insert a new appointment record into the database

    }

    /**
     * Changes the date/time of an existing patient appointment.
     *
     * @param p the patient whose appointment needs rescheduling
     * @param dtOld the current appointment date/time
     * @param dtNew the new desired date/time
     */

    public void rescheduleAppointmentForPatient(Patient p, LocalDateTime dtOld, LocalDateTime dtNew) {

        // Will find the appointment by patient and old datetime
        // Then update its datetime to dtNew (after checking availability)

    }

    /**
     * Cancels an existing appointment.
     * Frees up the specialist's time slot for other patients.
     *
     * @param p the patient whose appointment is being canceled
     * @param dt the date and time of the appointment to cancel
     */

    public void cancelAppointmentForPatient(Patient p, LocalDateTime dt) {

        // Will find the appointment and delete it or mark as canceled in the database

    }

    // ========== Room Management Methods ==========

    /**
     * Checks which internal rooms are available for patient admission.
     * Displays rooms with free beds.
     */

    public void getRoomAvailability() {

        // Will query the database for InternRoom entries where status = available
        // Or where number of occupied beds < total beds

    }

    /**
     * Assigns a patient to a specific internal room.
     * Called during patient admission.
     *
     * @param ir the internal room to assign
     * @param p the patient being admitted
     */

    public void setPatientInRoom(InternationRoom ir, Patient p) {

        // Will update the database to mark the bed/room as occupied by this patient
        // Also updates the patient's admission status

    }

    /**
     * Handles patient discharge.
     * Frees up the bed and room for the next patient.
     *
     * @param p the patient being discharged
     */

    public void dismissPatient(Patient p) {

        // Will find which room the patient is in
        // Then update the database to mark that bed as free
        // Also updates patient record with discharge date

    }
}