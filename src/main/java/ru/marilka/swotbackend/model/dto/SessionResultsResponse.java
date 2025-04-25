package ru.marilka.swotbackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.marilka.swotbackend.model.entity.SwotAlternativeEntity;
import ru.marilka.swotbackend.model.entity.SensitivityResultEntity;
import java.util.List;

@Data
@AllArgsConstructor
public class SessionResultsResponse {
    private List<SwotAlternativeEntity> alternatives;
    private List<SensitivityResultEntity> sensitivityResults;
}