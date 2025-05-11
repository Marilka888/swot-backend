package ru.marilka.swotbackend.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class SessionWithParticipantsDto {
    private Long id;
    private String name;
    private String notes;
    private Double alternativeDifference;
    private Double trapezoidDifference;
    private List<ParticipantDto> participants;
}

