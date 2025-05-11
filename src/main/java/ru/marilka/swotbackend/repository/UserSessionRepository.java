package ru.marilka.swotbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.marilka.swotbackend.model.entity.SwotUserSession;

import java.util.List;
import java.util.Optional;

public interface UserSessionRepository extends JpaRepository<SwotUserSession, Long> {
    List<SwotUserSession> findBySessionId(Long sessionId);

    Optional<SwotUserSession> findSwotUserSessionBySessionIdAndUserId(Long sessionId, Long userId);
}
