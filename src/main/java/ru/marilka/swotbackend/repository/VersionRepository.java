package ru.marilka.swotbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.marilka.swotbackend.model.entity.SessionVersionEntity;

public interface VersionRepository extends JpaRepository<SessionVersionEntity, Long> {
}