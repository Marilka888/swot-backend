package ru.marilka.swotbackend.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "session")
@Setter
@Getter
public class SessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private boolean completed;

    private LocalDateTime createdAt;

    private LocalDateTime lastModified;

    private String adminId;
    private String notes;
    private double alternativeDifference;
    private double trapezoidDifference;
}

