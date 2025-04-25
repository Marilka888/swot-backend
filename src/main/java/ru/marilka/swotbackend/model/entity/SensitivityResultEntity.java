package ru.marilka.swotbackend.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SensitivityResultEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sessionId;
    private Long versionId;

    private String alt1;
    private String alt2;

    private int lesser;
    private int greater;
    private int equal;
}