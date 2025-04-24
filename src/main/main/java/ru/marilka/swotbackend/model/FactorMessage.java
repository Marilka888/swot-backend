package ru.marilka.swotbackend.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FactorMessage {
    private Long sessionId;
    private String name;
    private String type; // "strong", "weak", etc.
}

