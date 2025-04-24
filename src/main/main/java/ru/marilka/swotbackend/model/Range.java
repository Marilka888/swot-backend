package ru.marilka.swotbackend.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class Range {
    private double min;
    private double max;
}
