package ru.marilka.swotbackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.marilka.swotbackend.model.SwotSummaryDto;
import ru.marilka.swotbackend.model.AlternativeDto;
import ru.marilka.swotbackend.model.entity.SwotVersionEntity;
import ru.marilka.swotbackend.model.request.CreateVersionRequest;
import ru.marilka.swotbackend.repository.AlternativeRepository;
import ru.marilka.swotbackend.service.AlternativeService;
import ru.marilka.swotbackend.service.SessionService;
import ru.marilka.swotbackend.service.SwotService;
import ru.marilka.swotbackend.service.VersionService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/session")
@CrossOrigin
@RequiredArgsConstructor
public class SwotController {

    private final SwotService swotService;
    private final SessionService sessionService;
    private final AlternativeService alternativeService;
    private final VersionService versionService;
    private final AlternativeRepository alternativeRepository;

    @GetMapping("/alternatives")
    public ResponseEntity<List<AlternativeDto>> getAlternativesBySessionAndVersion(
            @RequestParam Long sessionId,
            @RequestParam(required = false) Long versionId) {
        List<AlternativeDto> alternatives = alternativeService.calculateAlternatives(sessionId, versionId);
        return ResponseEntity.ok(alternatives);
    }

    @GetMapping("/result/alternatives")
    @Transactional
    public ResponseEntity<List<AlternativeDto>> getResultAlternativesBySessionAndVersion(
            @RequestParam Long sessionId,
            @RequestParam(required = false) Long versionId) {
        List<AlternativeDto> alternatives = alternativeService.calculateSelectedAlternatives(sessionId, versionId);
        return ResponseEntity.ok(alternatives);
    }

    @PostMapping("/complete/{id}")
    public ResponseEntity<?> completeSession(@PathVariable Long id) {
        Long sessionId = id;
        sessionService.markSessionAsCompleted(sessionId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/versions")
    public ResponseEntity<List<SwotVersionEntity>> getVersions(@PathVariable Long id) {
        return ResponseEntity.ok(swotService.getVersions(id));
    }

    @PostMapping("/version/create-from-current")
    public ResponseEntity<Map<String, Object>> createVersionFromCurrent(@RequestBody CreateVersionRequest request) {
        String sessionId = request.getSessionId();
        String baseVersionId = request.getBaseVersionId();

        // Создаём новую версию на основе факторов из baseVersionId
        SwotVersionEntity newVersion = versionService.createFromExisting(sessionId, baseVersionId);

        Map<String, Object> response = new HashMap<>();
        response.put("newVersionId", newVersion.getId());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/alternatives")
    /**
     * Получение альтернатив по итогу подсчетов (выбранные)
     */
    public List<AlternativeDto> getAlternatives(@RequestBody List<Long> request) {
        return alternativeService.calculateSelectedAlternatives(request);
    }

    @PostMapping("/{sessionId}/versions")
    /**
     * Создание версии для выбраннойй сессии
     */
    public ResponseEntity<SwotVersionEntity> createVersion(@PathVariable Long sessionId) {
        SwotVersionEntity version = sessionService.createNewVersion(sessionId);
        return ResponseEntity.ok(version);
    }
}