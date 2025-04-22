package ru.marilka.swotbackend.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.marilka.swotbackend.model.SwotSummaryDto;
import ru.marilka.swotbackend.model.entity.SwotVersionEntity;
import ru.marilka.swotbackend.repository.SwotSessionRepository;
import ru.marilka.swotbackend.repository.SwotVersionRepository;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SwotService {

    private final SwotSessionRepository sessionRepository;
    private final SwotVersionRepository versionRepository;
    private final ObjectMapper objectMapper;

    public SwotSummaryDto buildSummary(Long sessionId) {
        SwotSessionEntity session = sessionRepository.findById(sessionId).orElseThrow();
        SwotSummaryDto dto = new SwotSummaryDto();
        dto.setSessionName(session.getName());
        return dto;
    }

    public void saveSummary(Long sessionId, SwotSummaryDto summary) {
        SwotSessionEntity session = sessionRepository.findById(sessionId).orElseThrow();
        session.setName(summary.getSessionName());
        sessionRepository.save(session);

        // versioning
        SwotVersionEntity version = new SwotVersionEntity();
        version.setSessionId(sessionId);
        version.setCreatedAt(Instant.now());
        version.setSavedBy("system"); // Replace with current user context if needed
        try {
            version.setData(objectMapper.writeValueAsString(summary));
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize summary", e);
        }
        versionRepository.save(version);
    }

    public List<SwotVersionEntity> getVersions(Long sessionId) {
        return versionRepository.findBySessionIdOrderByCreatedAtDesc(sessionId);
    }

    public SwotSummaryDto getVersionData(Long versionId) {
        SwotVersionEntity version = versionRepository.findById(versionId).orElseThrow();
        try {
            return objectMapper.readValue(version.getData(), SwotSummaryDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize version data", e);
        }
    }

    public void restoreVersion(Long sessionId, Long versionId) {
        SwotSummaryDto versionData = getVersionData(versionId);
        saveSummary(sessionId, versionData);
    }
}