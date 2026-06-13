package com.nvivx.vixhealthsystem.model.person;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nvivx.vixhealthsystem.model.enums.Role;
import com.nvivx.vixhealthsystem.model.medical.Appointment;
import com.nvivx.vixhealthsystem.model.medical.MedicalRecord;
import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a patient in the VIX Health System.
 * <p>
 * A patient has a unique fiscal code, personal information inherited
 * from Person, one medical record and a list of appointments.
 * <p>
 * Appointment-management logic is kept inside this class.
 *
 * @see Person
 * @see MedicalRecord
 * @see Appointment
 */
@Entity
@Table(name = "Patients")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, ignoreUnknown = true)
public class Patient extends Person {

    /**
     * Patient unique identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Italian fiscal code.
     * Must be unique for every patient.
     */
    @Column(name = "fiscal_code", nullable = false, unique = true, length = 16)
    private String fiscalCode;

    /**
     * Patient medical record.
     * One patient has exactly one medical record.
     */
    @JsonIgnore
    @OneToOne(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private MedicalRecord medicalRecord;

    /**
     * Patient appointments.
     * Marked as Transient because appointments are stored in a JSON file,
     * not in the SQL database.
     */
    @JsonIgnore
    @Transient
    private List<Appointment> appointments = new ArrayList<>();

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    /**
     * Default constructor required by JPA.
     */
    public Patient() {
    }

    /**
     * Creates a Patient with the specified fiscal code and medical record.
     *
     * @param fiscalCode    the Italian fiscal code (16 characters, unique)
     * @param medicalRecord the patient's medical record
     */
    public Patient(String fiscalCode, MedicalRecord medicalRecord) {
        this.fiscalCode = fiscalCode;
        this.medicalRecord = medicalRecord;
    }

    // =====================================================
    // GETTERS & SETTERS
    // =====================================================

    /**
     * Returns the patient's unique identifier.
     *
     * @return the patient ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the patient's unique identifier.
     *
     * @param id the patient ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the patient's Italian fiscal code.
     *
     * @return the fiscal code
     */
    public String getFiscalCode() {
        return fiscalCode;
    }

    /**
     * Sets the patient's Italian fiscal code.
     *
     * @param fiscalCode the fiscal code to set (must be 16 characters, unique)
     */
    public void setFiscalCode(String fiscalCode) {
        this.fiscalCode = fiscalCode;
    }

    /**
     * Returns the patient's medical record.
     *
     * @return the medical record
     */
    public MedicalRecord getMedicalRecord() {
        return medicalRecord;
    }

    /**
     * Sets the patient's medical record.
     *
     * @param medicalRecord the medical record to set
     */
    public void setMedicalRecord(MedicalRecord medicalRecord) {
        this.medicalRecord = medicalRecord;
    }

    /**
     * Returns the list of appointments for this patient.
     *
     * @return the list of appointments
     */
    public List<Appointment> getAppointments() {
        return appointments;
    }

    /**
     * Sets the list of appointments for this patient.
     *
     * @param appointments the list of appointments to set
     */
    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    // =====================================================
    // DOMAIN METHODS
    // =====================================================

    /**
     * Returns the system authentication role for patients.
     */
    public Role getSystemRole() { return Role.ROLE_PATIENT; }

    /**
     * Creates and registers a new appointment for this patient with a specialist.
     *
     * @param m  the medical specialist conducting the appointment
     * @param dt the scheduled date and time
     * @return the newly created appointment
     */
    public Appointment makeAppointment(MedicalSpecialist m, LocalDateTime dt) {
        Appointment appointment = new Appointment(0, dt, 0, "");

        appointment.setPatient(this);
        appointment.setMedicalSpecialist(m);

        appointments.add(appointment);

        return appointment;
    }

    /**
     * Reschedules an existing appointment to a new date and time.
     * Only the first appointment matching the old date is updated.
     *
     * @param dtOld the current date and time of the appointment to reschedule
     * @param dtNew the new date and time
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
     * Cancels and removes the appointment scheduled at the specified date and time.
     *
     * @param dt the date and time of the appointment to cancel
     */
    public void cancelAppointment(LocalDateTime dt) {
        appointments.removeIf(a -> a.getDateTime().equals(dt));
    }

    /**
     * Deletes the patient's account by clearing all appointments
     * and removing the reference to the medical record.
     * <p>
     * Actual removal from the database is handled by the service layer.
     */
    public void deleteAccount() {
        appointments.clear();
        medicalRecord = null;
    }
}
