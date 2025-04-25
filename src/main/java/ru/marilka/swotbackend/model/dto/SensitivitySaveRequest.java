package ru.marilka.swotbackend.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class SensitivitySaveRequest {
    private Long sessionId;
    private Long versionId;
    private List<SensitivityResultDto> results;
}