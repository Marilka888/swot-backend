package ru.marilka.swotbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import ru.marilka.swotbackend.model.entity.SwotFactorEntity;

import java.util.List;

public interface FactorRepository extends JpaRepository<SwotFactorEntity, Long> {
    List<SwotFactorEntity> findByType(String type);

    List<SwotFactorEntity> findAllBySessionIdAndVersionId(Long sessionId, Long versionId);

    @Modifying
    @NativeQuery("DELETE FROM Factor f WHERE f.id = :id")
    void deleteFactorById(Long id);

    List<SwotFactorEntity> findAllBySessionIdAndVersionIdAndType(Long sessionId, Long versionId, String type);

}
