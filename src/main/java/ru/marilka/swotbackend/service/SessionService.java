package ru.marilka.swotbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.marilka.swotbackend.model.entity.SessionEntity;
import ru.marilka.swotbackend.repository.SessionRepository;
import ru.marilka.swotbackend.repository.SwotSessionRepository;
import ru.marilka.swotbackend.repository.VersionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class SessionService {

    private final SwotSessionRepository repo;
    private final SessionRepository sessionRepository;
    private final VersionRepository versionRepository;

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

}

