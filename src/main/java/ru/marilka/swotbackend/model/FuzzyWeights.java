package ru.marilka.swotbackend.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class FuzzyWeights {
    private double min;
    private double max;
    private double value;
}
