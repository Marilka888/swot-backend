package ru.marilka.swotbackend.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "swot_alternative")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwotAlternativeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String internalFactor;

    @Column(nullable = false)
    private String externalFactor;

    private double internalMassCenter;
    private double externalMassCenter;
    private double dPlus;
    private double dMinus;
    private double closeness;

    private String strategyType;

    @Column(nullable = false)
    private Long sessionId;

    @Column(nullable = false)
    private Long versionId;
}

