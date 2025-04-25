package ru.marilka.swotbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.marilka.swotbackend.model.dto.SensitivityResultDto;
import ru.marilka.swotbackend.model.entity.SwotAlternativeEntity;
import ru.marilka.swotbackend.model.entity.SwotFactorEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SensitivityAnalysisService {
    private final AlternativeService alternativeService;
    private final FactorService factorService;

    public List<SensitivityResultDto> analyze(Long sessionId, Long versionId, double delta) {
        double threshold = 0.01; // сравнение d*

        List<SwotAlternativeEntity> alternatives = alternativeService.getBySessionAndVersion(sessionId, versionId);
        Map<String, SwotFactorEntity> allFactors = factorService.getAllBySessionAndVersion(sessionId, versionId).stream()
                .collect(Collectors.toMap(SwotFactorEntity::getTitle, f -> f));

        List<SensitivityResultDto> resultList = new ArrayList<>();

        for (int i = 0; i < alternatives.size(); i++) {
            for (int j = i + 1; j < alternatives.size(); j++) {
                SwotAlternativeEntity alt1 = alternatives.get(i);
                SwotAlternativeEntity alt2 = alternatives.get(j);

                double d1 = alt1.getCloseness();
                double d2 = alt2.getCloseness();
                if (Math.abs(d1 - d2) < threshold) {

                    SwotFactorEntity internal1 = allFactors.get(alt1.getInternalFactor());
                    SwotFactorEntity external1 = allFactors.get(alt1.getExternalFactor());
                    SwotFactorEntity internal2 = allFactors.get(alt2.getInternalFactor());
                    SwotFactorEntity external2 = allFactors.get(alt2.getExternalFactor());

                    if (internal1 == null || external1 == null || internal2 == null || external2 == null) continue;

                    int equal = 0, greater = 0, lesser = 0;

                    for (double step = -delta; step <= delta; step += 0.001) {
                        // Все 4 вершины * 2 фактора = 8 случаев на шаг
                        for (int factorIndex = 0; factorIndex < 2; factorIndex++) {
                            SwotFactorEntity targetInternal = factorIndex == 0 ? internal1 : external1;

                            for (String vertex : List.of("min", "avg1", "avg2", "max")) {
                                SwotFactorEntity modInternal = modifyVertex(targetInternal, vertex, step);
                                SwotFactorEntity stableExternal = (factorIndex == 0) ? external1 : internal1;

                                double modD1 = recalculateCloseness(modInternal, stableExternal);
                                double stableD2 = recalculateCloseness(internal2, external2);

                                int cmp = Double.compare(modD1, stableD2);
                                if (cmp == 0) equal++;
                                else if (cmp > 0) greater++;
                                else lesser++;
                            }
                        }
                    }

                    resultList.add(SensitivityResultDto.builder()
                            .internalFactor1(alt1.getInternalFactor())
                            .externalFactor1(alt1.getExternalFactor())
                            .internalFactor2(alt2.getInternalFactor())
                            .externalFactor2(alt2.getExternalFactor())
                            .comparison(equal > greater && equal > lesser ? 0 : (greater > lesser ? 1 : -1))
                            .description("A" + (i + 1) + "(" + alt1.getExternalFactor() + " и " + alt1.getInternalFactor()
                                    + ") vs A" + (j + 1) + "(" + alt2.getExternalFactor() + " и " + alt2.getInternalFactor()+ ")"
                                    + " альтернативы равны в " + equal + " случаях, "
                                    + "A" + (i + 1) + " приоритетнее альтернативы " + "A" + (j + 1)
                                    + " в " + lesser + " случаях, "
                                    + "A" + (j + 1) + " приоритетнее альтернативы " + "A" + (i + 1)
                                    + " в " + greater + " случаях")
                            .build());
                }
            }
        }

        return resultList;
    }

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
