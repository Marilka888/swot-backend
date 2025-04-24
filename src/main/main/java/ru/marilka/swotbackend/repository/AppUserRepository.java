package ru.marilka.swotbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.marilka.swotbackend.model.entity.AppUser;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
}
