package ru.marilka.swotbackend.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "swot_final_swot_factor")
@Setter
@Getter
public class SwotFinalSwotFactorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String type;
    @Column(nullable = false)
    private Long versionId;
    @Column(nullable = false)
    private Long sessionId;
    @Column(nullable = false)
    private double value;
}
