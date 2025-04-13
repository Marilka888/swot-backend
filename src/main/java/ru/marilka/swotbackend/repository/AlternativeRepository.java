package ru.marilka.swotbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.marilka.swotbackend.model.entity.Alternative;

import java.util.List;

public interface AlternativeRepository extends JpaRepository<Alternative, Long> {
    List<Alternative> findAllBySessionId(String sessionId);

}

