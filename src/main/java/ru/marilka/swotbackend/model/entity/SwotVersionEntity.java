package ru.marilka.swotbackend.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "swot_version")
@Getter
@Setter
public class SwotVersionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sessionId;

    @Lob
    private String data;

    private LocalDateTime createdAt;

    private String savedBy;
}

