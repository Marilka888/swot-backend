package ru.marilka.swotbackend.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ParticipantDto {
    private Long id;
    private String fullName;
    private String role;
}
