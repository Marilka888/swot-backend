package ru.marilka.swotbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Factor {
    private Long id;

    private String name;
    private String type;

    private Range range1;
    private Range range2;

    private String sessionId;

    private double massCenter;

    private Long versionId;
    private Long userId;
}
