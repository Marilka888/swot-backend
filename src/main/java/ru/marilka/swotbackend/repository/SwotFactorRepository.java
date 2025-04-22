package ru.marilka.swotbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.marilka.swotbackend.model.entity.SwotFactor;

public interface SwotFactorRepository extends JpaRepository<SwotFactor, Long> {
}
