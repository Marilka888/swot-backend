package ru.marilka.swotbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.marilka.swotbackend.model.entity.SwotSession;
import ru.marilka.swotbackend.model.entity.SwotSessionEntity;

import java.util.List;

public interface SwotSessionRepository extends JpaRepository<SwotSessionEntity, Long> {
    List<SwotSession> findByUserId(String userId);
}

