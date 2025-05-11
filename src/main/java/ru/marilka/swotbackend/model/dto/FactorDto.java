package ru.marilka.swotbackend.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FactorDto {
    private Long id;          // Идентификатор фактора
    private String name;      // Название фактора (что отображается в таблице)
    private String type;      // Тип фактора (strong, weak, opportunity, threat)
    private Double massCenter; // Центр масс (агрегированное значение для вывода)
}

