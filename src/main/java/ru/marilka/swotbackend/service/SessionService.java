package ru.marilka.swotbackend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import org.springframework.stereotype.Service;
import ru.marilka.swotbackend.model.entity.*;
import ru.marilka.swotbackend.model.SummaryResponse;
import ru.marilka.swotbackend.repository.SessionRepository;
import ru.marilka.swotbackend.repository.SwotSessionRepository;
import ru.marilka.swotbackend.repository.VersionRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@Builder
public class SessionService {

    private final SwotSessionRepository repo;
    private final SessionRepository sessionRepository;
    private final VersionRepository versionRepository;


    public SessionService(SwotSessionRepository repo, SessionRepository sessionRepository, VersionRepository versionRepository) {
        this.repo = repo;
        this.sessionRepository = sessionRepository;
        this.versionRepository = versionRepository;
    }

    public SessionVersionEntity createNewVersion(Long sessionId) {
        SessionEntity session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        SessionVersionEntity version = new SessionVersionEntity();
        version.setSession(session);
        version.setTimestamp(LocalDateTime.now());

        return versionRepository.save(version);
    }

    public void completeLastSession() {
        SessionEntity session = sessionRepository.findTopByOrderByIdDesc()
                .orElseThrow(() -> new RuntimeException("Сессия не найдена"));

        session.setCompleted(true);
        session.setLastModified(LocalDateTime.now());

        sessionRepository.save(session);
    }

    public Optional<SessionEntity> getSession(Long id) {
        return sessionRepository.findById(1L);
    }

    public List<SessionEntity> getUserSessions() {
        return sessionRepository.findAllByUserId(1L);
    }

    public SwotSessionEntity create(String name, String userId) {
        SwotSessionEntity session = new SwotSessionEntity();
        session.setName(name);
        return repo.save(session);
    }

    public SummaryResponse buildSummary(Long sessionId) {
        // Здесь собираются факторы, альтернативы и веса из базы
        return new SummaryResponse();
    }

    public void updateSummary(Long sessionId, SummaryResponse request) throws JsonProcessingException {

        SessionVersion version = new SessionVersion();
        version.setSessionId(sessionId);
        version.setCreatedAt(Instant.now());
        version.setSavedBy("getCurrentUsername()");
        version.setData(new ObjectMapper().writeValueAsString(request));

    }


}

