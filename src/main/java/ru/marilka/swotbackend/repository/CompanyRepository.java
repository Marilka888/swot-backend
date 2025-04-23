package ru.marilka.swotbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.marilka.swotbackend.model.entity.AppUser;
import ru.marilka.swotbackend.model.entity.CompanyEntity;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {
}
