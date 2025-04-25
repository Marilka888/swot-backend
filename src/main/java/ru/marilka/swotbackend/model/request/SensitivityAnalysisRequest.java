package ru.marilka.swotbackend.model.request;

import lombok.Data;

@Data
public class SensitivityAnalysisRequest {
    private Long sessionId;
    private Long versionId;
    private double trapezoidDifference;
}