package ru.marilka.swotbackend.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SensitivityResultDto {
    private String description;
    private String internalFactor1;
    private String externalFactor1;
    private String internalFactor2;
    private String externalFactor2;
    private int lesser;
    private int greater;
    private int equal;
}