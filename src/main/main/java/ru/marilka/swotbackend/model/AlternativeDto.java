package ru.marilka.swotbackend.model;

import lombok.*;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
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

