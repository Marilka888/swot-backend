package ru.marilka.swotbackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.marilka.swotbackend.model.AlternativeDto;
import ru.marilka.swotbackend.model.Factor;
import ru.marilka.swotbackend.model.dto.*;
import ru.marilka.swotbackend.model.entity.*;
import ru.marilka.swotbackend.model.request.CreateSessionRequest;
import ru.marilka.swotbackend.repository.AppUserRepository;
import ru.marilka.swotbackend.repository.SensitivityResultRepository;
import ru.marilka.swotbackend.repository.SessionRepository;
import ru.marilka.swotbackend.repository.UserSessionRepository;
import ru.marilka.swotbackend.service.AlternativeService;
import ru.marilka.swotbackend.service.FactorService;
import ru.marilka.swotbackend.service.SensitivityAnalysisService;
import ru.marilka.swotbackend.service.SessionService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final AlternativeService alternativeService;
    private final SessionService sessionService;
    private final FactorService factorService;
    private final SessionRepository sessionRepository;
    private final UserSessionRepository userSessionRepository;
    private final AppUserRepository userRepo;

    @GetMapping
    public ResponseEntity<Object> getUserSessions() {
        return ResponseEntity.ok(sessionService.getUserSessions());
    }

    @GetMapping("/{sessionId}")
    /**
     * Получение жанных по выбранной сессии (+ инфа по версиям)
     */
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

    @PostMapping("/create")
    /**
     * Создание сессии.
     */
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
        int totalInput = request.participants().stream().mapToInt(ru.marilka.swotbackend.model.request.ParticipantDto::coefficient).sum();
        int participantCount = request.participants().size();

        for (var p : request.participants()) {
            var sus = new SwotUserSession();
            sus.setSessionId(session.getId());
            sus.setUserId(p.userId());
            // Нормализация
            sus.setUserCoefficient((double) (p.coefficient() * participantCount) / totalInput);
            userSessionRepository.save(sus);
        }

        return ResponseEntity.ok(Map.of(
                "sessionId", session.getId()
        ));
    }

    @GetMapping("/results/pdf")
    public ResponseEntity<byte[]> exportResultsPdf(
            @RequestParam Long sessionId,
            @RequestParam Long versionId) throws Exception {

        List<Factor> factors = factorService.getFactors(sessionId, versionId);
        List<AlternativeDto> alternatives = alternativeService.calculateSelectedAlternatives(sessionId, versionId);
        var sessionEntity = sessionRepository.findById(sessionId).orElseThrow();
        String sessionName = "SWOT Сессия " + sessionEntity.getName();
        byte[] pdf = alternativeService.exportToPdf(sessionName, factors, alternatives);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "swot_results.pdf");

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }

}