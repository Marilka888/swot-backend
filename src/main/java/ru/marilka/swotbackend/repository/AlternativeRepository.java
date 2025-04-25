package ru.marilka.swotbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.marilka.swotbackend.model.entity.AppUser;
import ru.marilka.swotbackend.model.entity.SwotAlternativeEntity;
import ru.marilka.swotbackend.model.entity.SwotFactorEntity;

import java.util.List;

public interface AlternativeRepository extends JpaRepository<SwotAlternativeEntity, Long> {
    List<SwotAlternativeEntity> findAllBySessionIdAndVersionId(Long sessionId, Long versionId);

    void deleteBySessionIdAndVersionId(Long sessionId, Long versionId);
    void deleteBySessionIdAndVersionIdAndInternalFactorAndExternalFactor(Long sessionId, Long versionId, String internal, String external);

    List<SwotAlternativeEntity> findBySessionIdAndVersionId(Long sessionId, Long versionId);

    List<SwotAlternativeEntity> findBySessionId(Long sessionId);

}
