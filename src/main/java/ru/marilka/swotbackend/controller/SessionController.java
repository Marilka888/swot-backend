package ru.marilka.swotbackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.marilka.swotbackend.model.request.CreateSessionRequest;
import ru.marilka.swotbackend.model.dto.SessionDto;
import ru.marilka.swotbackend.model.entity.SessionEntity;
import ru.marilka.swotbackend.model.entity.SwotUserSession;
import ru.marilka.swotbackend.repository.AppUserRepository;
import ru.marilka.swotbackend.repository.SessionRepository;
import ru.marilka.swotbackend.repository.UserSessionRepository;
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
    private final UserSessionRepository userSessionRepository;

    @GetMapping
    public ResponseEntity<Object> getUserSessions() {
        return ResponseEntity.ok(sessionService.getUserSessions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getSession(@PathVariable String id) {
        return ResponseEntity.ok(sessionService.getSession(Long.valueOf(id)).orElseThrow());
    }

    @PostMapping
    public ResponseEntity<SessionEntity> createSession(@RequestBody Map<String, String> payload) {
        String name = payload.get("name");
        String userId = payload.get("userId");
        return ResponseEntity.ok(sessionService.create(name, userId));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createSession(@RequestBody CreateSessionRequest request) {
        var session = new SessionEntity(); // инициализация
        session.setName(request.name());
        session.setAdminId(Long.valueOf(request.admin()));
        session.setNotes(request.notes());
        session.setAlternativeDifference(request.alternativeDifference());
        session.setTrapezoidDifference(request.trapezoidDifference());
        session.setCreatedAt(LocalDateTime.now());
        session.setLastModified(LocalDateTime.now());
        session.setCompleted(false);

        session = sessionRepository.save(session);

        if (request.participants() != null) {
            for (var p : request.participants()) {
                var sus = new SwotUserSession();
                sus.setSessionId(session.getId());
                sus.setUserId(p.userId());
                sus.setUserCoefficient(p.coefficient());
                userSessionRepository.save(sus);
            }
        }

        return ResponseEntity.ok(Map.of(
                "sessionId", session.getId()
        ));
    }
}

