package ru.marilka.swotbackend.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "swot_alternative")
@Setter
@Getter
public class SwotAlternativeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double dMinus;
    private double dPlus;
    private double dStar;
    private String oneFactor;
    private double percentageOneFactor;
    private String twoFactor;
    private double percentageTwoFactor;
    @Column(nullable = false)
    private Long versionId;
    @Column(nullable = false)
    private Long sessionId;
}
