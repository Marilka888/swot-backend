package ru.marilka.swotbackend.model;

import ru.marilka.swotbackend.model.entity.SwotFactor;

import java.util.List;


public class FactorUpdateRequest {
    private List<SwotFactor> factors;

    public List<SwotFactor> getFactors() {
        return factors;
    }

    public void setFactors(List<SwotFactor> factors) {
        this.factors = factors;
    }
}
