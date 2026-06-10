package com.nvivx.vixhealthsystem.model.medical;

import com.nvivx.vixhealthsystem.model.facility.SpecializedRoom;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
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

}