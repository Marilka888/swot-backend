package ru.marilka.swotbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import ru.marilka.swotbackend.model.entity.SwotAlternativeEntity;

import java.util.List;

public interface AlternativeRepository extends JpaRepository<SwotAlternativeEntity, Long> {
    List<SwotAlternativeEntity> findAllBySessionIdAndVersionId(Long sessionId, Long versionId);

    @Modifying
    void deleteBySessionIdAndVersionId(Long sessionId, Long versionId);

    @Modifying
    void deleteBySessionIdAndVersionIdAndInternalFactorAndExternalFactor(Long sessionId, Long versionId, String internal, String external);

    List<SwotAlternativeEntity> findBySessionIdAndVersionId(Long sessionId, Long versionId);

    List<SwotAlternativeEntity> findBySessionId(Long sessionId);

}
