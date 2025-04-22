package ru.marilka.swotbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.marilka.swotbackend.model.entity.SwotSessionEntity;

public interface SwotSessionRepository extends JpaRepository<SwotSessionEntity, Long> {
}

