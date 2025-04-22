package ru.marilka.swotbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.marilka.swotbackend.model.entity.SessionEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<SessionEntity, Long> {
    Optional<SessionEntity> findTopByOrderByIdDesc(); // Последняя созданная

    List<SessionEntity> findAllByUserId(Long userId); // Последняя созданная
}

