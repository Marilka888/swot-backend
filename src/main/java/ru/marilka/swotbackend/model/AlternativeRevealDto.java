package ru.marilka.swotbackend.model;

import lombok.Data;

@Data
public class AlternativeRevealDto {
    private String internal;
    private String external;
    private double internalPercent;
    private double externalPercent;
}

