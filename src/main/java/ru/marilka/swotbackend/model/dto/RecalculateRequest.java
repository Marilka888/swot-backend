package ru.marilka.swotbackend.model.dto;

import lombok.Data;
import ru.marilka.swotbackend.model.entity.SwotAlternativeEntity;
import java.util.List;

@Data
public class RecalculateRequest {
    private Long sessionId;
    private Long versionId;
    private List<SwotAlternativeEntity> alternatives;
}