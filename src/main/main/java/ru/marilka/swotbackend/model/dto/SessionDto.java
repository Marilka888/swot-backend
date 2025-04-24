package ru.marilka.swotbackend.model.dto;

import lombok.Data;

@Data
public class SessionDto {
    private String name;
    private Long admin;
    private String notes;
    private double alternativeDifference;
    private double trapezoidDifference;
}
