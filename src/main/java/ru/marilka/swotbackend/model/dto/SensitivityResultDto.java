package ru.marilka.swotbackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.marilka.swotbackend.model.entity.SwotAlternativeEntity;

@Data
@Builder
@AllArgsConstructor
public class SensitivityResultDto {
//    private String description;
//    private String internalFactor1;
//    private String externalFactor1;
//    private String internalFactor2;
//    private String externalFactor2;
    private int lesser;
    private int greater;
    private int equal;

    private SwotAlternativeEntity alt1;
    private int comparison; // -1: alt2 лучше, 0: равны, 1: alt1 лучше
    private SwotAlternativeEntity alt2;
}