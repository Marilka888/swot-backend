package ru.marilka.swotbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.marilka.swotbackend.model.entity.SwotFactorEntity;
import ru.marilka.swotbackend.model.entity.SwotVersionEntity;
import ru.marilka.swotbackend.repository.FactorRepository;
import ru.marilka.swotbackend.repository.VersionRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VersionService {

    private final VersionRepository versionRepository;
    private final FactorRepository factorRepository;

    @Transactional
    public SwotVersionEntity createFromExisting(String sessionId, String baseVersionId) {
        // 2. Создаем новую версию
        SwotVersionEntity newVersion = new SwotVersionEntity();
        newVersion.setSessionId(Long.valueOf(sessionId));
        newVersion.setCreatedAt(LocalDateTime.now());
        newVersion.setData(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
        versionRepository.save(newVersion);

        // 3. Копируем факторы из базовой версии в новую
        List<SwotFactorEntity> baseFactors = factorRepository.findBySessionIdAndVersionId(
                Long.valueOf(sessionId),
                Long.valueOf(baseVersionId)
        );

        List<SwotFactorEntity> copiedFactors = baseFactors.stream()
                .map(factor -> {
                    SwotFactorEntity copy = new SwotFactorEntity();
                    copy.setSessionId(factor.getSessionId());
                    copy.setVersionId(newVersion.getId());
                    copy.setTitle(factor.getTitle());
                    copy.setType(factor.getType());
                    copy.setUserId(factor.getUserId());
                    copy.setWeightMin(factor.getWeightMin());
                    copy.setWeightAvg2(factor.getWeightAvg2());
                    copy.setWeightAvg1(factor.getWeightAvg1());
                    copy.setWeightMax(factor.getWeightMax());
                    return copy;
                })
                .toList();

        factorRepository.saveAll(copiedFactors);

        return newVersion;
    }
}
