package ru.marilka.swotbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.marilka.swotbackend.model.dto.SensitivityComparisonDto;
import ru.marilka.swotbackend.model.dto.SensitivityResultDto;
import ru.marilka.swotbackend.model.entity.SensitivityResultEntity;
import ru.marilka.swotbackend.model.entity.SessionEntity;
import ru.marilka.swotbackend.model.entity.SwotAlternativeEntity;
import ru.marilka.swotbackend.model.entity.SwotFactorEntity;
import ru.marilka.swotbackend.repository.FactorRepository;
import ru.marilka.swotbackend.repository.SensitivityResultRepository;
import ru.marilka.swotbackend.repository.SessionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SensitivityAnalysisService {
    private final AlternativeService alternativeService;
    private final FactorService factorService;
    private final SessionRepository sessionRepository;
    private final SensitivityResultRepository sensitivityResultRepository;
    private final FactorRepository factorRepository;

    public List<SensitivityComparisonDto> runDetailedAnalysis(Long sessionId, Long versionId, Double delta, Double factorDistance) {
        List<SwotAlternativeEntity> alternatives = alternativeService.getBySessionAndVersion(sessionId, versionId);
        List<SwotFactorEntity> allFactors = factorRepository.findBySessionIdAndVersionId(sessionId, versionId);

        List<SensitivityComparisonDto> results = new ArrayList<>();

        for (int i = 0; i < alternatives.size(); i++) {
            for (int j = i + 1; j < alternatives.size(); j++) {
                SwotAlternativeEntity alt1 = alternatives.get(i);
                SwotAlternativeEntity alt2 = alternatives.get(j);

                double originalD1 = alt1.getCloseness();
                double originalD2 = alt2.getCloseness();
                double closenessDiff = Math.abs(originalD1 - originalD2);

                if (delta != null && closenessDiff > delta) {
                    continue;
                }

                int equal = 0;
                int lesser = 0;
                int greater = 0;

                for (int k = 0; k < 20; k++) {
                    // Генерация сдвинутых mass-центров из трапеций
                    Map<String, Double> adjustedCenters = allFactors.stream()
                            .collect(Collectors.toMap(
                                    SwotFactorEntity::getTitle,  // ключ - название фактора
                                    f -> {
                                        double min = shift(f.getWeightMin(), factorDistance);
                                        double avg1 = shift(f.getWeightAvg1(), factorDistance);
                                        double avg2 = shift(f.getWeightAvg2(), factorDistance);
                                        double max = shift(f.getWeightMax(), factorDistance);
                                        return trapezoidalCenter(min, avg1, avg2, max);
                                    },
                                    (v1, v2) -> v1 // если дубликаты, взять первое (или логика по userId?)
                            ));

                    double d1 = recalculateCloseness(alt1, adjustedCenters);
                    double d2 = recalculateCloseness(alt2, adjustedCenters);

                    if (Math.abs(d1 - d2) < 1e-6) {
                        equal++;
                    } else if (d1 > d2) {
                        lesser++;
                    } else {
                        greater++;
                    }
                }

                results.add(new SensitivityComparisonDto(alt1, alt2, equal, lesser, greater));
            }
        }

        return results;
    }

    private double shift(double value, double distance) {
        double delta = (Math.random() * distance * 2) - distance;
        double shifted = value + delta;
        return Math.max(0.0, Math.min(10.0, shifted)); // ограничить в [0;10]
    }

    private double trapezoidalCenter(double a, double b, double c, double d) {
        return (a + 2 * b + 2 * c + d) / 6.0;
    }

    private double recalculateCloseness(SwotAlternativeEntity alt, Map<String, Double> adjustedCenters) {
        double internal = adjustedCenters.getOrDefault(alt.getInternalFactor(), 0.0);
        double external = adjustedCenters.getOrDefault(alt.getExternalFactor(), 0.0);

        double dPlus = Math.abs(internal - 10);
        double dMinus = Math.abs(external - 0);

        return dMinus / (dPlus + dMinus);
    }


    public void saveSensitivityResults(Long sessionId, Long versionId, List<SensitivityResultDto> results) {
//        List<SensitivityResultEntity> entities = results.stream()
//                .map(dto -> SensitivityResultEntity.builder()
//                        .sessionId(sessionId)
//                        .versionId(versionId)
//                        .alt1(dto.getInternalFactor1() + "+" + dto.getExternalFactor1())
//                        .alt2(dto.getInternalFactor2() + "+" + dto.getExternalFactor2())
//                        .lesser(dto.getLesser())
//                        .greater(dto.getGreater())
//                        .equal(dto.getEqual())
//                        .build())
//                .collect(Collectors.toList());
//        sensitivityResultRepository.saveAll(entities);
    }
//    public List<SensitivityResultDto> analyze(Long sessionId, Long versionId) {
//        double threshold = 0.01; // сравнение d*
//        double delta = sessionRepository.findById(sessionId).orElseThrow().getTrapezoidDifference();
//        List<SwotAlternativeEntity> alternatives = alternativeService.getBySessionAndVersion(sessionId, versionId);
//        Map<String, SwotFactorEntity> allFactors = factorService.getAllBySessionAndVersion(sessionId, versionId).stream()
//                .collect(Collectors.toMap(SwotFactorEntity::getTitle, f -> f));
//
//        List<SensitivityResultDto> resultList = new ArrayList<>();
//
//        for (int i = 0; i < alternatives.size(); i++) {
//            for (int j = i + 1; j < alternatives.size(); j++) {
//                SwotAlternativeEntity alt1 = alternatives.get(i);
//                SwotAlternativeEntity alt2 = alternatives.get(j);
//
//                double d1 = alt1.getCloseness();
//                double d2 = alt2.getCloseness();
//                if (Math.abs(d1 - d2) < threshold) {
//
//                    SwotFactorEntity internal1 = allFactors.get(alt1.getInternalFactor());
//                    SwotFactorEntity external1 = allFactors.get(alt1.getExternalFactor());
//                    SwotFactorEntity internal2 = allFactors.get(alt2.getInternalFactor());
//                    SwotFactorEntity external2 = allFactors.get(alt2.getExternalFactor());
//
//                    if (internal1 == null || external1 == null || internal2 == null || external2 == null) continue;
//
//                    int equal = 0, greater = 0, lesser = 0;
//
//                    for (double step = -delta; step <= delta; step += 0.001) {
//                        // Все 4 вершины * 2 фактора = 8 случаев на шаг
//                        for (int factorIndex = 0; factorIndex < 2; factorIndex++) {
//                            SwotFactorEntity targetInternal = factorIndex == 0 ? internal1 : external1;
//
//                            for (String vertex : List.of("min", "avg1", "avg2", "max")) {
//                                SwotFactorEntity modInternal = modifyVertex(targetInternal, vertex, step);
//                                SwotFactorEntity stableExternal = (factorIndex == 0) ? external1 : internal1;
//
//                                double modD1 = recalculateCloseness(modInternal, stableExternal);
//                                double stableD2 = recalculateCloseness(internal2, external2);
//
//                                int cmp = Double.compare(modD1, stableD2);
//                                if (cmp == 0) equal++;
//                                else if (cmp > 0) greater++;
//                                else lesser++;
//                            }
//                        }
//                    }
//
//                    resultList.add(SensitivityResultDto.builder()
//                            .internalFactor1(alt1.getInternalFactor())
//                            .externalFactor1(alt1.getExternalFactor())
//                            .internalFactor2(alt2.getInternalFactor())
//                            .externalFactor2(alt2.getExternalFactor())
//                            .description("A" + (i + 1) + "(" + alt1.getExternalFactor() + " и " + alt1.getInternalFactor()
//                                    + ") vs A" + (j + 1) + "(" + alt2.getExternalFactor() + " и " + alt2.getInternalFactor()+ ")"
//                                    + " альтернативы равны в " + equal + " случаях, "
//                                    + "A" + (i + 1) + " приоритетнее альтернативы " + "A" + (j + 1)
//                                    + " в " + lesser + " случаях, "
//                                    + "A" + (j + 1) + " приоритетнее альтернативы " + "A" + (i + 1)
//                                    + " в " + greater + " случаях")
//                            .build());
//                }
//            }
//        }
//
//        return resultList;
//    }

    private SwotFactorEntity modifyVertex(SwotFactorEntity original, String vertex, double step) {
        return SwotFactorEntity.builder()
                .title(original.getTitle())
                .type(original.getType())
                .weightMin(vertex.equals("min") ? original.getWeightMin() + step : original.getWeightMin())
                .weightAvg1(vertex.equals("avg1") ? original.getWeightAvg1() + step : original.getWeightAvg1())
                .weightAvg2(vertex.equals("avg2") ? original.getWeightAvg2() + step : original.getWeightAvg2())
                .weightMax(vertex.equals("max") ? original.getWeightMax() + step : original.getWeightMax())
                .build();
    }

    private double recalculateCloseness(SwotFactorEntity internal, SwotFactorEntity external) {
        double center1 = (internal.getWeightMin() + internal.getWeightAvg1() + internal.getWeightAvg2() + internal.getWeightMax()) / 4.0;
        double center2 = (external.getWeightMin() + external.getWeightAvg1() + external.getWeightAvg2() + external.getWeightMax()) / 4.0;
        return 1.0 / (1.0 + Math.abs(center1 - center2));
    }

    private SwotFactorEntity cloneWithShift(SwotFactorEntity original, double delta) {
        if (original == null) {
            throw new IllegalArgumentException("Один из факторов не найден для анализа чувствительности.");
        }

        return SwotFactorEntity.builder()
                .title(original.getTitle())
                .weightMax(original.getWeightMax() - delta)
                .weightMin(original.getWeightMin() - delta)
                .weightAvg1(original.getWeightAvg1() + delta)
                .weightAvg2(original.getWeightAvg2() + delta)
                .type(original.getType())
                .build();
    }


//    private double recalculateCloseness(SwotFactorEntity internal, SwotFactorEntity external) {
//        // Тут твоя логика пересчёта d* для двух факторов
//        // Например, центр масс или нормализованная метрика
//        double center1 = (internal.getWeightMin() + internal.getWeightAvg2() + internal.getWeightMax() + internal.getWeightAvg1()) / 4.0;
//        double center2 = (external.getWeightMin() + external.getWeightAvg2() + external.getWeightMax() + external.getWeightAvg1()) / 4.0;
//        return 1.0 / (1.0 + Math.abs(center1 - center2));
//    }
}
