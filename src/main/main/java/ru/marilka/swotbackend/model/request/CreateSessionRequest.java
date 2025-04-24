package ru.marilka.swotbackend.model.request;

import java.util.List;

public record CreateSessionRequest(
        String name,
        String admin,
        String notes,
        double alternativeDifference,
        double trapezoidDifference,
        List<ParticipantDto> participants
) {}
