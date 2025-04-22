package ru.marilka.swotbackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.marilka.swotbackend.model.SessionDto;
import ru.marilka.swotbackend.model.entity.SessionEntity;
import ru.marilka.swotbackend.repository.AppUserRepository;
import ru.marilka.swotbackend.repository.SessionRepository;
import ru.marilka.swotbackend.service.SessionService;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/v1/sessions")
@CrossOrigin
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;
    private final SessionRepository sessionRepository;
    private final AppUserRepository userRepository;

    @GetMapping
    public ResponseEntity<Object> getUserSessions() {
        return ResponseEntity.ok(sessionService.getUserSessions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getSession(@PathVariable String id) {
        return ResponseEntity.ok(sessionService.getSession(Long.valueOf(id)).orElseThrow());
    }

    @PostMapping
    public ResponseEntity<SwotSessionEntity> createSession(@RequestBody Map<String, String> payload) {
        String name = payload.get("name");
        String userId = payload.get("userId");
        return ResponseEntity.ok(sessionService.create(name, userId));
    }

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

