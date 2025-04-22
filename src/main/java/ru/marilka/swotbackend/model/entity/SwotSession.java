package ru.marilka.swotbackend.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
public class SwotSession {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String userId;
    private Instant createdAt = Instant.now();
}
