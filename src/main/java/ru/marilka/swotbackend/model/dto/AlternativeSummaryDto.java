package ru.marilka.swotbackend.model.dto;

public record AlternativeSummaryDto(
        String factor1,
        String factor2,
        double d_minus,
        double d_plus,
        double d_star
) {}

