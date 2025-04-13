package ru.marilka.swotbackend.service;

import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;

@Service
public class SliceService {

    private List<Double> slices = Arrays.asList(0.2, 0.5, 0.8);

    public List<Double> getSlices() {
        return slices;
    }

    public void updateSlices(List<Double> newSlices) {
        if (newSlices.size() == 3) {
            this.slices = newSlices;
        }
    }

}

