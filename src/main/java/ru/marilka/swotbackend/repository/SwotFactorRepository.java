package ru.marilka.swotbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.marilka.swotbackend.model.entity.SwotFactor;

import java.util.List;

public interface SwotFactorRepository extends JpaRepository<SwotFactor, Long> {
    List<SwotFactor> findAllByType(String type);
    List<SwotFactor> findAllByTypeAndSessionId(String type, String sessionId);
    List<SwotFactor> findAllBySessionId(String sessionId);
}
