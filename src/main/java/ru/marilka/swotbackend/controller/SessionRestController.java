package ru.marilka.swotbackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.marilka.swotbackend.model.SessionDto;
import ru.marilka.swotbackend.model.entity.AppUser;
import ru.marilka.swotbackend.model.entity.SessionEntity;
import ru.marilka.swotbackend.repository.AppUserRepository;
import ru.marilka.swotbackend.repository.SessionRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/v1/sessions")
@RequiredArgsConstructor
@CrossOrigin
public class SessionRestController {

    private final SessionRepository sessionRepository;
    private final AppUserRepository userRepository;

    @PostMapping("create")
    public ResponseEntity<Void> create(@RequestBody SessionDto dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userRepository.findByUsername(username).orElseThrow();
        Long userId = user.getId();

        SessionEntity session = new SessionEntity();
        session.setName(dto.getName());
        session.setAdmin(dto.getAdmin());
        session.setNotes(dto.getNotes());
        session.setAlternativeDifference(dto.getAlternativeDifference());
        session.setTrapezoidDifference(dto.getTrapezoidDifference());
        session.setCreatedAt(LocalDateTime.now());
        session.setLastModified(LocalDateTime.now());
        session.setCompleted(false);
        session.setUserId(userId);
        sessionRepository.save(session);
        return ResponseEntity.ok().build();
    }
}

