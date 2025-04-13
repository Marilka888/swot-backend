package ru.marilka.swotbackend.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.time.Instant;

@Entity
@Setter
@Getter
public class SessionVersion {
    @Id
    @GeneratedValue
    private Long id;
    private Long sessionId;
    private String data; // JSON
    private Instant createdAt;
    private String savedBy;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String summaryJson;

}

