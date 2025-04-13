package ru.marilka.swotbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.marilka.swotbackend.model.entity.AlternativeDto;
import ru.marilka.swotbackend.model.entity.FactorEntity;
import ru.marilka.swotbackend.repository.FactorRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AlternativeService {
    private final FactorRepository factorRepository;

    private static final List<Double> ALPHA_LEVELS = List.of(0.1, 0.5, 0.9);

    public List<AlternativeDto> calculateSelectedAlternatives(List<Long> selectedIds) {
        List<FactorEntity> selectedFactors = factorRepository.findAllById(selectedIds);

        List<FactorEntity> internalFactors = selectedFactors.stream()
                .filter(f -> f.getType().equalsIgnoreCase("strong") || f.getType().equalsIgnoreCase("weak"))
                .toList();

        List<FactorEntity> externalFactors = selectedFactors.stream()
                .filter(f -> f.getType().equalsIgnoreCase("opportunity") || f.getType().equalsIgnoreCase("threat"))
                .toList();

        List<AlternativeDto> alternatives = new ArrayList<>();

        for (FactorEntity internal : internalFactors) {
            for (FactorEntity external : externalFactors) {
                double ra = 0;
                double dPlusSum = 0;
                double dMinusSum = 0;

                for (double alpha : ALPHA_LEVELS) {
                    double x = alphaCutMassCenter(internal, alpha);
                    double y = alphaCutMassCenter(external, alpha);

                    double dPlus = Math.sqrt(Math.pow(10 - x, 2) + Math.pow(10 - y, 2));
                    double dMinus = Math.sqrt(Math.pow(-10 - x, 2) + Math.pow(-10 - y, 2));
                    double closeness = dMinus / (dPlus + dMinus);

                    ra += alpha * closeness;
                    dPlusSum += dPlus;
                    dMinusSum += dMinus;
                }

                double dPlusAvg = dPlusSum / ALPHA_LEVELS.size();
                double dMinusAvg = dMinusSum / ALPHA_LEVELS.size();

                double internalCenter = trapezoidalMassCenter(internal);
                double externalCenter = trapezoidalMassCenter(external);

                String strategyType = defineStrategy(internal.getType(), external.getType());

                alternatives.add(new AlternativeDto(
                        internal.getName(),
                        external.getName(),
                        internalCenter,
                        externalCenter,
                        dPlusAvg,
                        dMinusAvg,
                        ra,
                        strategyType
                ));
            }
        }

        return alternatives.stream()
                .sorted(Comparator.comparingDouble(AlternativeDto::getCloseness).reversed())
                .toList();
    }


    public List<AlternativeDto> calculateAlternatives() {
        List<FactorEntity> allFactors = factorRepository.findAll();

        List<FactorEntity> internalFactors = allFactors.stream()
                .filter(f -> f.getType().equalsIgnoreCase("strong") || f.getType().equalsIgnoreCase("weak"))
                .toList();

        List<FactorEntity> externalFactors = allFactors.stream()
                .filter(f -> f.getType().equalsIgnoreCase("opportunity") || f.getType().equalsIgnoreCase("threat"))
                .toList();

        List<AlternativeDto> alternatives = new ArrayList<>();

        for (FactorEntity internal : internalFactors) {
            for (FactorEntity external : externalFactors) {
                double ra = 0;
                double dPlusSum = 0;
                double dMinusSum = 0;

                for (double alpha : ALPHA_LEVELS) {
                    double x = alphaCutMassCenter(internal, alpha);
                    double y = alphaCutMassCenter(external, alpha);

                    double dPlus = Math.sqrt(Math.pow(10 - x, 2) + Math.pow(10 - y, 2));
                    double dMinus = Math.sqrt(Math.pow(-10 - x, 2) + Math.pow(-10 - y, 2));
                    double closeness = dMinus / (dPlus + dMinus);

                    ra += alpha * closeness;
                    dPlusSum += dPlus;
                    dMinusSum += dMinus;
                }

                double dPlusAvg = dPlusSum / ALPHA_LEVELS.size();
                double dMinusAvg = dMinusSum / ALPHA_LEVELS.size();

                double internalCenter = trapezoidalMassCenter(internal);
                double externalCenter = trapezoidalMassCenter(external);

                String strategyType = defineStrategy(internal.getType(), external.getType());

                alternatives.add(new AlternativeDto(
                        internal.getName(),
                        external.getName(),
                        internalCenter,
                        externalCenter,
                        dPlusAvg,
                        dMinusAvg,
                        ra,
                        strategyType
                ));
            }
        }

        return alternatives.stream()
                .sorted(Comparator.comparingDouble(AlternativeDto::getCloseness).reversed())
                .toList();
    }

    private double trapezoidalMassCenter(FactorEntity f) {
        double a = f.getWeightMin();
        double b = f.getWeightAvg1();
        double c = f.getWeightAvg2();
        double d = f.getWeightMax();
        return (a + 2 * b + 2 * c + d) / 6;
    }

    private double alphaCutMassCenter(FactorEntity f, double alpha) {
        double a = f.getWeightMin();
        double b = f.getWeightAvg1();
        double c = f.getWeightAvg2();
        double d = f.getWeightMax();

        double left = a + (b - a) * alpha;
        double right = d - (d - c) * alpha;

        return (left + right) / 2;
    }

    private String defineStrategy(String internalType, String externalType) {
        if (internalType.equalsIgnoreCase("strong") && externalType.equalsIgnoreCase("opportunity")) return "SO";
        if (internalType.equalsIgnoreCase("strong") && externalType.equalsIgnoreCase("threat")) return "ST";
        if (internalType.equalsIgnoreCase("weak") && externalType.equalsIgnoreCase("threat")) return "WT";
        if (internalType.equalsIgnoreCase("weak") && externalType.equalsIgnoreCase("opportunity")) return "WO";
        return "UNDEFINED";
    }
}
