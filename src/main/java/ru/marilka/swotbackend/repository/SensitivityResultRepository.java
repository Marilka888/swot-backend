package ru.marilka.swotbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.marilka.swotbackend.model.entity.SensitivityResultEntity;

import java.util.List;

public interface SensitivityResultRepository extends JpaRepository<SensitivityResultEntity, Long> {
    List<SensitivityResultEntity> findBySessionIdAndVersionId(Long sessionId, Long versionId);
}