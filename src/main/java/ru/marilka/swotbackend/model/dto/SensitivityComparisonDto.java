package ru.marilka.swotbackend.model.dto;

import ru.marilka.swotbackend.model.entity.SwotAlternativeEntity;

public record SensitivityComparisonDto(
        SwotAlternativeEntity alt1,
        SwotAlternativeEntity alt2,
        int equal,
        int lesser,
        int greater,
        double maxLesserRejection,
        double maxGreaterRejection

) {}
