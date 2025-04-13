package ru.marilka.swotbackend.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Setter
public class FactorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type;

    private double weightMin;
    private double weightMax;
    private double weightAvg1;
    private double weightAvg2;
    private boolean selected;

    @Column(nullable = false)
    private Long versionId;
    private Long sessionId;

}
