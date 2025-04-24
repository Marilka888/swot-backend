package ru.marilka.swotbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.marilka.swotbackend.model.entity.SessionEntity;
import ru.marilka.swotbackend.model.entity.SwotVersionEntity;
import ru.marilka.swotbackend.repository.SessionRepository;
import ru.marilka.swotbackend.repository.VersionRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final VersionRepository versionRepository;

    public SwotVersionEntity createNewVersion(Long sessionId) {
       SwotVersionEntity version = new SwotVersionEntity();
        version.setSessionId(sessionId);
        version.setCreatedAt(LocalDateTime.now());

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
        return sessionRepository.findById(id);
    }

    public List<SessionEntity> getUserSessions() {
        return sessionRepository.findAllByAdminId(1L);
    }

    public SessionEntity create(String name, String userId) {
        SessionEntity session = new SessionEntity();
        session.setName(name);
        return sessionRepository.save(session);
    }

}

