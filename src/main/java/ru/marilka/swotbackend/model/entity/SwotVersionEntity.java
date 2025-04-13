package ru.marilka.swotbackend.model.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
public class SwotVersionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sessionId;

    @Lob
    private String data;

    private Instant createdAt;

    private String savedBy;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public String getSavedBy() { return savedBy; }
    public void setSavedBy(String savedBy) { this.savedBy = savedBy; }
}

