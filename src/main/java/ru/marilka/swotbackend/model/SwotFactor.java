package ru.marilka.swotbackend.model;

public record SwotFactor(
        String id,
        String description,
        TrapezoidalFuzzyNumber fuzzyNumber
) {
}
