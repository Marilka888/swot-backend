package ru.marilka.swotbackend.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SelectedFactorsRequest {
    private List<Long> factorIds;

    public List<Long> getFactorIds() {
        return factorIds;
    }

    public void setFactorIds(List<Long> factorIds) {
        this.factorIds = factorIds;
    }
}
