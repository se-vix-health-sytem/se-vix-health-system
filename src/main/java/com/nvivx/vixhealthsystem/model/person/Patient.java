package com.nvivx.vixhealthsystem.model.person;

import com.nvivx.vixhealthsystem.model.medical.Appointment;
import com.nvivx.vixhealthsystem.model.medical.MedicalRecord;
import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a patient in the VIX Health System.
 *
 * A patient has a unique fiscal code, personal information inherited
 * from Person, one medical record and a list of appointments.
 *
 * Appointment-management logic is kept inside this class.
 *
 * @see Person
 * @see MedicalRecord
 * @see Appointment
 */
@Entity
@Table(name = "Patients")
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
     * One patient has one medical record.
     */
    @OneToOne(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private MedicalRecord medicalRecord;

    /**
     * Patient appointments.
     */
    @Transient
    private List<Appointment> appointments = new ArrayList<>();
    public Patient() {
    }

    public Patient(String fiscalCode, MedicalRecord medicalRecord) {
        this.fiscalCode = fiscalCode;
        this.medicalRecord = medicalRecord;
    }

    public Long getId() {
        return id;
    }

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

    public Appointment makeAppointment(MedicalSpecialist m, LocalDateTime dt) {
        Appointment appointment = new Appointment(0, dt, 0, "");

        appointment.setPatient(this);
        appointment.setMedicalSpecialist(m);

        appointments.add(appointment);

        return appointment;
    }

    public void rescheduleAppointment(LocalDateTime dtOld, LocalDateTime dtNew) {
        for (Appointment a : appointments) {
            if (a.getDateTime().equals(dtOld)) {
                a.setDateTime(dtNew);
                break;
            }
        }
    }

    public void cancelAppointment(LocalDateTime dt) {
        appointments.removeIf(a -> a.getDateTime().equals(dt));
    }

    public void deleteAccount() {
        appointments.clear();
        medicalRecord = null;
    }
}