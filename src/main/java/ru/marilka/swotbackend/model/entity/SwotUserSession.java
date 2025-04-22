package ru.marilka.swotbackend.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "swot_user_session")
@Setter
@Getter
public class SwotUserSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double userCoefficient;
    @Column(nullable = false)
    private Long sessionId;
    @Column(nullable = false)
    private Long userId;
}
