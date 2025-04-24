package ru.marilka.swotbackend.model.request;

import ru.marilka.swotbackend.model.AlternativeRevealDto;

import java.util.List;

public record RecalculateRequest(
        Long sessionId,
        Long versionId,
        List<AlternativeRevealDto> revealList
) {}

