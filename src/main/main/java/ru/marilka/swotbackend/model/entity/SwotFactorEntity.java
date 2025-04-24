package ru.marilka.swotbackend.model.entity;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "swot_factor")
@Setter
@Getter
public class SwotFactorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String type;

    private double weightMin;
    private double weightMax;
    private double weightAvg1;
    private double weightAvg2;
    private boolean selected;

    @Column(nullable = false)
    private Long versionId;
    @Column(nullable = false)
    private Long sessionId;
    @Column(nullable = false)
    private Long userId;
}
