package ru.marilka.swotbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.marilka.swotbackend.model.AlternativeDto;
import ru.marilka.swotbackend.model.entity.SwotFactorEntity;
import ru.marilka.swotbackend.repository.FactorRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AlternativeService {
    private final FactorRepository factorRepository;

    private static final List<Double> ALPHA_LEVELS = List.of(0.1, 0.5, 0.9);

    public List<AlternativeDto> calculateSelectedAlternatives(List<Long> selectedIds) {
        List<SwotFactorEntity> selectedFactors = factorRepository.findAllById(selectedIds);

        List<SwotFactorEntity> internalFactors = selectedFactors.stream()
                .filter(f -> f.getType().equalsIgnoreCase("strong") || f.getType().equalsIgnoreCase("weak"))
                .toList();

        List<SwotFactorEntity> externalFactors = selectedFactors.stream()
                .filter(f -> f.getType().equalsIgnoreCase("opportunity") || f.getType().equalsIgnoreCase("threat"))
                .toList();

        List<AlternativeDto> alternatives = new ArrayList<>();

        for (SwotFactorEntity internal : internalFactors) {
            for (SwotFactorEntity external : externalFactors) {
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
                        internal.getTitle(),
                        external.getTitle(),
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
        List<SwotFactorEntity> allFactors = factorRepository.findAll();

        List<SwotFactorEntity> internalFactors = allFactors.stream()
                .filter(f -> f.getType().equalsIgnoreCase("strong") || f.getType().equalsIgnoreCase("weak"))
                .toList();

        List<SwotFactorEntity> externalFactors = allFactors.stream()
                .filter(f -> f.getType().equalsIgnoreCase("opportunity") || f.getType().equalsIgnoreCase("threat"))
                .toList();

        List<AlternativeDto> alternatives = new ArrayList<>();

        for (SwotFactorEntity internal : internalFactors) {
            for (SwotFactorEntity external : externalFactors) {
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
                        internal.getTitle(),
                        external.getTitle(),
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

    private double trapezoidalMassCenter(SwotFactorEntity f) {
        double a = f.getWeightMin();
        double b = f.getWeightAvg1();
        double c = f.getWeightAvg2();
        double d = f.getWeightMax();
        return (a + 2 * b + 2 * c + d) / 6;
    }

    private double alphaCutMassCenter(SwotFactorEntity f, double alpha) {
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



public double trapezoidalMassCenter(SwotFactorEntity factor) {
    double a = factor.getA();
    double b = factor.getB();
    double c = factor.getC();
    double d = factor.getD();
    return (a + 2 * b + 2 * c + d) / 6.0;
}

public double alphaCutMassCenter(SwotFactorEntity factor, double alpha) {
    double a = factor.getA();
    double b = factor.getB();
    double c = factor.getC();
    double d = factor.getD();
    double left = a + alpha * (b - a);
    double right = d - alpha * (d - c);
    return (left + right) / 2.0;
}

public String defineStrategy(String internalType, String externalType) {
    internalType = internalType.toLowerCase();
    externalType = externalType.toLowerCase();
    if (internalType.equals("strong") && externalType.equals("opportunity")) {
        return "SO";
    } else if (internalType.equals("weak") && externalType.equals("opportunity")) {
        return "WO";
    } else if (internalType.equals("strong") && externalType.equals("threat")) {
        return "ST";
    } else if (internalType.equals("weak") && externalType.equals("threat")) {
        return "WT";
    } else {
        return "OTHER";
    }
}
