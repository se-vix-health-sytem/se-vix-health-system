package com.nvivx.vixhealthsystem.model.person;


import com.nvivx.vixhealthsystem.model.medical.Appointment;
import com.nvivx.vixhealthsystem.model.medical.MedicalRecord;
import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.spi.ToolProvider.findFirst;

/**
 * Represents a patient in the VIX Health System.
 *
 * Extends Person with a unique fiscal code (used as primary key in DB),
 * a medical record (composition — one patient, one record), and a list
 * of appointments. All appointment-management logic lives here.
 *
 * @see Person
 * @see MedicalRecord
 * @see Appointment
 */


public class Patient extends Person{

    private String fiscalCode;
    private MedicalRecord medicalRecord;
    private List<Appointment> appointments;


    //-------------- Constructors -------------------


    public Patient() {
        this.appointments = new ArrayList<>();
    }

    public Patient(String fiscalCode, MedicalRecord medicalRecord) {
        this.fiscalCode = fiscalCode;
        this.medicalRecord = medicalRecord;
        this.appointments = new ArrayList<>();
    }


    //-------------- Getters and Setters -------------------


    public String getFiscalCode() {
        return fiscalCode;
    }

    public void setFiscalCode(String fiscalCode) {
        this.fiscalCode = fiscalCode;
    }

    public MedicalRecord getMedicalRecord() {
        return medicalRecord;
    }

    public void setMedicalRecord(MedicalRecord medicalRecord) {
        this.medicalRecord = medicalRecord;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }


    //-------------- Methods -------------------


    /**
     * Books a new appointment with a given specialist at the given date/time interval.
     * ID is set to 0 as a placeholde - the DB will assign the real ID on persist.
     *
     * @param m the medical specialist to book with
     * @param dt the requested date and time
     * @return the newly created appointment
     */
    public Appointment makeAppointment(MedicalSpecialist m, LocalDateTime dt) {
        Appointment appointment = new Appointment(0, dt, 0, "");
        appointments.add(appointment);
        return appointment;
    }

    /**
     * Reschedules an existing appointment identified by its old date/time.
     *
     * @param dtOld current date/time of the appointment
     * @param dtNew the new date and time to assign
     */
    public void rescheduleAppointment(LocalDateTime dtOld, LocalDateTime dtNew) {
        for (Appointment a : appointments) {
            if (a.getDateTime().equals(dtOld)) {
                a.setDateTime(dtNew);
                break;
            }
        }
    }

    /**
     * Cancels the appointment at the given date/time, removing it from the list.
     *
     * @param dt the date/time of the appointment to cancel
     */
    public void cancelAppointment(LocalDateTime dt) {
        appointments.removeIf(a -> a.getDateTime().equals(dt));
    }

    /**
     * Clears all appointments and detaches the medical record.
     * Called when the patient deletes their account.
     */
    public void deleteAccount() {
        this.appointments.clear();
        this.medicalRecord = null;
    }



}
