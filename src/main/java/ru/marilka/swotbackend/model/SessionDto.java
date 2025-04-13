package ru.marilka.swotbackend.model;

import lombok.Data;

@Data
public class SessionDto {
    private String name;
    private String admin;
    private String notes;
    private double alternativeDifference;
    private double trapezoidDifference;
}
