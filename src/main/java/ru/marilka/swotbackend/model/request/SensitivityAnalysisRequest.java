package ru.marilka.swotbackend.model.request;

import lombok.Data;

@Data
public class SensitivityAnalysisRequest {
    private Long sessionId;
    private Long versionId;
    Double delta;           // Δ между альтернативами
    Double factorDistance;   // Расстояние между факторами
}