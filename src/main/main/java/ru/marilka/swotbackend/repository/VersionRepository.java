package ru.marilka.swotbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.marilka.swotbackend.model.entity.SwotVersionEntity;

public interface VersionRepository extends JpaRepository<SwotVersionEntity, Long> {
}