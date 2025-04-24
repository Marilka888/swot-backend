package ru.marilka.swotbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.marilka.swotbackend.model.entity.SwotVersionEntity;

import java.util.List;

public interface SwotVersionRepository extends JpaRepository<SwotVersionEntity, Long> {
    List<SwotVersionEntity> findBySessionIdOrderByCreatedAtDesc(Long sessionId);
}