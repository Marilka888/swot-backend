package ru.marilka.swotbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import ru.marilka.swotbackend.model.entity.FactorEntity;

import java.util.List;

public interface FactorRepository extends JpaRepository<FactorEntity, Long> {
    List<FactorEntity> findByType(String type);

    List<FactorEntity> findAllBySessionIdAndVersionId(Long sessionId, Long versionId);

    @Modifying
    @NativeQuery("DELETE FROM Factor f WHERE f.id = :id")
    void deleteFactorById(Long id);
}
