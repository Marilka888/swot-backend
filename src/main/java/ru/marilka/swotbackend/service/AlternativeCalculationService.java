package ru.marilka.swotbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.marilka.swotbackend.model.AlternativeResultDto;
import ru.marilka.swotbackend.model.AlternativeRevealDto;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlternativeCalculationService {

    public List<AlternativeResultDto> recalculateAlternatives(List<AlternativeRevealDto> reveals) {
        List<AlternativeResultDto> results = new ArrayList<>();

        for (AlternativeRevealDto reveal : reveals) {
            double internalValue = getFactorValue(reveal.getInternal()) * reveal.getInternalPercent() / 100.0;
            double externalValue = getFactorValue(reveal.getExternal()) * reveal.getExternalPercent() / 100.0;

            double dplus = Math.max(internalValue, externalValue);
            double dminus = Math.min(internalValue, externalValue);
            double closeness = dplus == 0 ? 0 : dminus / (dplus + dminus);

            results.add(new AlternativeResultDto(
                    reveal.getInternal(),
                    reveal.getExternal(),
                    round(dplus),
                    round(dminus),
                    round(closeness)
            ));
        }

        return results;
    }
// todo
    // пример: возвращает вес фактора (замени на работу с базой или fuzzy-логикой)
    private double getFactorValue(String factorCode) {
        // TODO: можно загрузить значения из базы или кэш
        return switch (factorCode) {
            case "S1" -> 0.85;
            case "O2" -> 0.92;
            case "W1" -> 0.45;
            case "T2" -> 0.33;
            default -> 0.5;
        };
    }

    private double round(double val) {
        return Math.round(val * 1000.0) / 1000.0;
    }
}

