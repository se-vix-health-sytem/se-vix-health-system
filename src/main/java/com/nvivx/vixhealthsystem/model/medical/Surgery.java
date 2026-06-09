package com.nvivx.vixhealthsystem.model.medical;

import com.nvivx.vixhealthsystem.model.facility.SpecializedRoom;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Surgeries")
public class Surgery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_record_id", nullable = false)
    private MedicalRecord medicalRecord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialized_room_id", nullable = false)
    private SpecializedRoom specializedRoom;

    @Column(name = "surgery_date", nullable = false)
    private LocalDateTime dateTime;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    public Surgery() {
    }

    public Surgery(LocalDateTime dateTime, String name, String description, SpecializedRoom specializedRoom) {
        this.dateTime = dateTime;
        this.name = name;
        this.description = description;
        this.specializedRoom = specializedRoom;
    }

    public Long getId() {
        return id;
    }

    public MedicalRecord getMedicalRecord() {
        return medicalRecord;
    }

    public void setMedicalRecord(MedicalRecord medicalRecord) {
        this.medicalRecord = medicalRecord;
    }

    public SpecializedRoom getSpecializedRoom() {
        return specializedRoom;
    }

    public void setSpecializedRoom(SpecializedRoom specializedRoom) {
        this.specializedRoom = specializedRoom;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}