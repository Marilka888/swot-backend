package ru.marilka.swotbackend.model;

public record RevealDto(
        String internal,
        String external,
        double internalPercent,
        double externalPercent
) {}

