package ru.marilka.swotbackend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import ru.marilka.swotbackend.model.Factor;
import ru.marilka.swotbackend.model.Range;
import ru.marilka.swotbackend.model.entity.SwotFactorEntity;
import ru.marilka.swotbackend.repository.FactorRepository;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FactorService {

    private final FactorRepository factorRepository;

    public Factor create(Factor request) {
        return getFactor(factorRepository.save(getFactor(request)));
    }

    public void saveSelectedFactorIds(List<Long> factorIds) {
        // Здесь можно, например, сохранить их в сессию, или проставить флаг в базе
        List<SwotFactorEntity> selected = factorRepository.findAllById(factorIds);

        for (SwotFactorEntity factor : selected) {
            factor.setSelected(true); // допустим, есть поле selected
        }

        factorRepository.saveAll(selected);
    }

    @Transactional
    public List<Factor> update(List<Factor> request) {
        String type = request.getFirst().getType();
        String sessionId = request.getFirst().getSessionId();
        var versionId = request.getFirst().getVersionId();
        Map<String, List<Factor>> map = request.stream().collect(Collectors.groupingBy(Factor::getType));
        List<Factor> factorList = map.get(type);
        List<SwotFactorEntity> existingFactors = factorRepository.findAllBySessionIdAndVersionIdAndType(Long.valueOf(sessionId), versionId, type);

        // Удаляем устаревшие
        List<Long> updatedIds = factorList.stream().map(Factor::getId).toList();
        for (SwotFactorEntity existing : existingFactors) {
            if (!updatedIds.contains(existing.getId())) {
                factorRepository.deleteFactorById(existing.getId());
            }
        }

        // Сохраняем новые/обновленные
        List<SwotFactorEntity> toSave = factorList.stream().map(FactorService::getFactor).toList();
        factorRepository.saveAll(toSave);

        return factorRepository.findByType(type).stream()
                .map(FactorService::getFactor)
                .sorted(Comparator.comparingDouble(Factor::getMassCenter).reversed())
                .toList();
    }

    public List<Factor> getFactors(Long sessionId, Long versionId) {
        return factorRepository.findAllBySessionIdAndVersionId(sessionId, versionId).stream()
                .map(FactorService::getFactorWithMassCenter)
                .toList();
    }

    private static Factor getFactor(SwotFactorEntity a) {
        double min = safe(a.getWeightMin(), 1.0);
        double max = safe(a.getWeightMax(), 10.0);
        double avg1 = safe(a.getWeightAvg1(), (min + max) / 2 - 1);
        double avg2 = safe(a.getWeightAvg2(), (min + max) / 2 + 1);

        avg1 = Math.max(min, Math.min(avg1, max));
        avg2 = Math.max(min, Math.min(avg2, max));
        if (avg1 > avg2) avg1 = avg2;

        return Factor.builder()
                .id(a.getId())
                .name(a.getTitle())
                .type(a.getType())
                .range1(Range.builder().min(min).max(max).build())
                .range2(Range.builder().min(avg1).max(avg2).build())
                .build();
    }

    private static Factor getFactorWithMassCenter(SwotFactorEntity a) {
        Factor factor = getFactor(a);
        double a1 = factor.getRange1().getMin();
        double b1 = factor.getRange2().getMin();
        double b2 = factor.getRange2().getMax();
        double a2 = factor.getRange1().getMax();

        double massCenter = (a1 + b1 + b2 + a2) / 4.0;
        factor.setMassCenter(massCenter);
        factor.setSessionId(String.valueOf(a.getSessionId()));
        factor.setVersionId(a.getVersionId());
        factor.setUserId(a.getUserId());
        return factor;
    }

    private static SwotFactorEntity getFactor(Factor a) {
        double min = ObjectUtils.isEmpty(a.getRange1()) ? 0.0 : safe(a.getRange1().getMin(), 1.0);
        double max = ObjectUtils.isEmpty(a.getRange1()) ? 0.0 : safe(a.getRange1().getMax(), 10.0);
        double avg1 = ObjectUtils.isEmpty(a.getRange2()) ? 0.0 : safe(a.getRange2().getMin(), (min + max) / 2 - 1);
        double avg2 = ObjectUtils.isEmpty(a.getRange2()) ? 0.0 : safe(a.getRange2().getMax(), (min + max) / 2 + 1);

        return SwotFactorEntity.builder()
                .id(a.getId())
                .title(a.getName())
                .sessionId(Long.valueOf(a.getSessionId()))
                .versionId(a.getVersionId())
                .userId(a.getUserId())
                .type(a.getType())
                .weightMin(min)
                .weightMax(max)
                .weightAvg1(avg1)
                .weightAvg2(avg2)
                .build();
    }

    private static double safe(Double value, double fallback) {
        return value != null && !Double.isNaN(value) ? value : fallback;
    }

    public Collection<SwotFactorEntity> getAllBySessionAndVersion(Long sessionId, Long versionId) {
        return factorRepository.findBySessionIdAndVersionId(sessionId, versionId);
    }
}