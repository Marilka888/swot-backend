package ru.marilka.swotbackend.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AlternativeDto {
    private String internalFactor;
    private String externalFactor;
    private double internalMassCenter;
    private double externalMassCenter;
    private double dPlus;
    private double dMinus;
    private double closeness;
    private String strategyType;
}

