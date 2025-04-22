package ru.marilka.swotbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VersionRepository extends JpaRepository<SessionVersionEntity, Long> {
}