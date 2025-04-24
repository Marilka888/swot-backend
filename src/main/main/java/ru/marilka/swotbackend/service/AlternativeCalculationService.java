package ru.marilka.swotbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.marilka.swotbackend.model.AlternativeDto;
import ru.marilka.swotbackend.model.AlternativeResultDto;
import ru.marilka.swotbackend.model.AlternativeRevealDto;
import ru.marilka.swotbackend.model.RevealDto;
import ru.marilka.swotbackend.model.entity.SwotAlternativeEntity;
import ru.marilka.swotbackend.model.entity.SwotFactorEntity;
import ru.marilka.swotbackend.repository.AlternativeRepository;
import ru.marilka.swotbackend.repository.FactorRepository;
import ru.marilka.swotbackend.repository.SessionRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AlternativeCalculationService {
    private static final List<Double> ALPHA_LEVELS = List.of(0.1, 0.5, 0.9);
    private final AlternativeRepository alternativeRepo;
    private final FactorRepository factorRepository;

    public List<AlternativeDto> recalculateWithReveal(List<AlternativeRevealDto> revealList) {
        List<AlternativeDto> recalculated = new ArrayList<>();
        List<SwotFactorEntity> allFactors = factorRepository.findAll();

        Map<String, SwotFactorEntity> internalMap = new HashMap<>();
        Map<String, SwotFactorEntity> externalMap = new HashMap<>();
        for (SwotFactorEntity factor : allFactors) {
            String type = factor.getType().toLowerCase();
            if (type.equals("strong") || type.equals("weak")) {
                internalMap.put(factor.getTitle(), factor);
            } else if (type.equals("opportunity") || type.equals("threat")) {
                externalMap.put(factor.getTitle(), factor);
            }
        }

        for (AlternativeRevealDto reveal : revealList) {
            SwotFactorEntity internal = internalMap.get(reveal.getInternal());
            SwotFactorEntity external = externalMap.get(reveal.getExternal());
            if (internal == null || external == null) continue;

            double dPlusSum = 0;
            double dMinusSum = 0;

            for (double alpha : ALPHA_LEVELS) {
                double x = alphaCutMassCenter(internal, alpha) * reveal.getInternalPercent() / 100.0;
                double y = alphaCutMassCenter(external, alpha) * reveal.getExternalPercent() / 100.0;

                double dPlus = Math.sqrt(Math.pow(10 - x, 2) + Math.pow(10 - y, 2));
                double dMinus = Math.sqrt(Math.pow(-10 - x, 2) + Math.pow(-10 - y, 2));
                dPlusSum += dPlus;
                dMinusSum += dMinus;
            }

            double dPlusAvg = dPlusSum / ALPHA_LEVELS.size();
            double dMinusAvg = dMinusSum / ALPHA_LEVELS.size();
            double closenessAvg = dMinusAvg / (dPlusAvg + dMinusAvg);

            double internalCenter = trapezoidalMassCenter(internal);
            double externalCenter = trapezoidalMassCenter(external);
            String strategyType = defineStrategy(internal.getType(), external.getType());

            AlternativeDto dto = new AlternativeDto();
            dto.setInternalFactor(internal.getTitle());
            dto.setExternalFactor(external.getTitle());
            dto.setInternalMassCenter(internalCenter);
            dto.setExternalMassCenter(externalCenter);
            dto.setDPlus(dPlusAvg);
            dto.setDMinus(dMinusAvg);
            dto.setCloseness(closenessAvg);
            dto.setStrategyType(strategyType);

            recalculated.add(dto);
        }

        return recalculated;
    }

    public double trapezoidalMassCenter(SwotFactorEntity factor) {
        double a = factor.getWeightMin();
        double b = factor.getWeightMax();
        double c = factor.getWeightAvg1();
        double d = factor.getWeightAvg2();

        return (a + 2 * b + 2 * c + d) / 6.0;
    }
    public double alphaCutMassCenter(SwotFactorEntity factor, double alpha) {
        double a = factor.getWeightMin();
        double b = factor.getWeightMax();
        double c = factor.getWeightAvg1();
        double d = factor.getWeightAvg2();

        double left = a + alpha * (b - a);
        double right = d - alpha * (d - c);

        return (left + right) / 2.0;
    }

    public String defineStrategy(String internalType, String externalType) {
        internalType = internalType.toLowerCase();
        externalType = externalType.toLowerCase();

        if (internalType.equals("strong") && externalType.equals("opportunity")) {
            return "SO"; // Сильные стороны + Возможности
        } else if (internalType.equals("weak") && externalType.equals("opportunity")) {
            return "WO"; // Слабости + Возможности
        } else if (internalType.equals("strong") && externalType.equals("threat")) {
            return "ST"; // Сильные стороны + Угрозы
        } else if (internalType.equals("weak") && externalType.equals("threat")) {
            return "WT"; // Слабости + Угрозы
        } else {
            return "OTHER";
        }
    }


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

