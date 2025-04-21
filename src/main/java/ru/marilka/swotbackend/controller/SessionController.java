package ru.marilka.swotbackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.marilka.swotbackend.model.entity.SessionVersionEntity;
import ru.marilka.swotbackend.model.entity.SwotSession;
import ru.marilka.swotbackend.model.entity.SwotSessionEntity;
import ru.marilka.swotbackend.service.SessionService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/sessions")
@CrossOrigin
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping
    public ResponseEntity<Object> getUserSessions() {
        return ResponseEntity.ok(sessionService.getUserSessions());
    }

    @PostMapping("/complete")
    public ResponseEntity<String> completeSession() {
        sessionService.completeLastSession();
        return ResponseEntity.ok("Сессия завершена");
    }

    @PostMapping("/{sessionId}/versions")
    public ResponseEntity<SessionVersionEntity> createVersion(@PathVariable Long sessionId) {
        SessionVersionEntity version = sessionService.createNewVersion(sessionId);
        return ResponseEntity.ok(version);
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
}

