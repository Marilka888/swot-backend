package ru.marilka.swotbackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.marilka.swotbackend.model.dto.*;
import ru.marilka.swotbackend.model.entity.*;
import ru.marilka.swotbackend.model.request.CreateSessionRequest;
import ru.marilka.swotbackend.repository.AppUserRepository;
import ru.marilka.swotbackend.repository.SensitivityResultRepository;
import ru.marilka.swotbackend.repository.SessionRepository;
import ru.marilka.swotbackend.repository.UserSessionRepository;
import ru.marilka.swotbackend.service.AlternativeService;
import ru.marilka.swotbackend.service.SensitivityAnalysisService;
import ru.marilka.swotbackend.service.SessionService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final AlternativeService alternativeService;
    private final SensitivityAnalysisService sensitivityService;
    private final SensitivityResultRepository sensitivityRepo;
    private final SessionService sessionService;
    private final SessionRepository sessionRepository;
    private final UserSessionRepository userSessionRepository;
    private final AppUserRepository userRepo;

    @GetMapping
    public ResponseEntity<Object> getUserSessions() {
        return ResponseEntity.ok(sessionService.getUserSessions());
    }


    @GetMapping("/{sessionId}")
    public ResponseEntity<SessionWithParticipantsDto> getSessionWithParticipants(@PathVariable Long sessionId) {
        SessionEntity session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found"));

        List<SwotUserSession> links = userSessionRepository.findBySessionId(sessionId);

        List<ParticipantDto> participants = links.stream()
                .map(link -> {
                    AppUser user = userRepo.findById(link.getUserId()).orElseThrow();
                    return ParticipantDto.builder()
                            .id(user.getId())
                            .fullName(user.getFullName())
                            .role(user.getRole())
                            .build();
                })
                .toList();

        SessionWithParticipantsDto dto = SessionWithParticipantsDto.builder()
                .id(session.getId())
                .name(session.getName())
                .notes(session.getNotes())
                .alternativeDifference(session.getAlternativeDifference())
                .trapezoidDifference(session.getTrapezoidDifference())
                .participants(participants)
                .build();

        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<SessionEntity> createSession(@RequestBody Map<String, String> payload) {
        String name = payload.get("name");
        String userId = payload.get("userId");
        return ResponseEntity.ok(sessionService.create(name, userId));
    }
    @PostMapping("/recalculate/save")
    public ResponseEntity<Void> recalculateAndSave(@RequestBody RecalculateRequest request) {
        alternativeService.replaceAlternatives(request.getSessionId(), request.getVersionId(), request.getAlternatives());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/sensitivity-analysis/save")
    public ResponseEntity<Void> saveSensitivity(@RequestBody SensitivitySaveRequest request) {
        sensitivityService.saveSensitivityResults(request.getSessionId(), request.getVersionId(), request.getResults());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/results/{sessionId}")
    public ResponseEntity<SessionResultsResponse> getResults(@PathVariable Long sessionId, @RequestParam Long versionId) {
        List<SwotAlternativeEntity> alternatives = alternativeService.getBySessionAndVersion(sessionId, versionId);
        List<SensitivityResultEntity> sensitivity = sensitivityRepo.findBySessionIdAndVersionId(sessionId, versionId);
        return ResponseEntity.ok(new SessionResultsResponse(alternatives, sensitivity));
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