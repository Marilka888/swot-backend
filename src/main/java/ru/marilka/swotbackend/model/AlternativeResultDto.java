package ru.marilka.swotbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AlternativeResultDto {
    private String internalFactor;
    private String externalFactor;
    private double dplus;
    private double dminus;
    private double closeness;
}

