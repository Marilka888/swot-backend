package ru.marilka.swotbackend.model;

public record Strategy(
        String name,
        TrapezoidalFuzzyNumber internal,
        TrapezoidalFuzzyNumber external) {
}
