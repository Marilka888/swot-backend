package ru.marilka.swotbackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.marilka.swotbackend.model.SwotSummaryDto;
import ru.marilka.swotbackend.model.AlternativeDto;
import ru.marilka.swotbackend.model.entity.SwotVersionEntity;
import ru.marilka.swotbackend.service.AlternativeService;
import ru.marilka.swotbackend.service.SessionService;
import ru.marilka.swotbackend.service.SwotService;

import java.util.List;

@RestController
@RequestMapping("/v1/session")
@CrossOrigin
@RequiredArgsConstructor
public class SwotController {

    private final SwotService swotService;
    private final SessionService sessionService;
    private final AlternativeService alternativeService;

    @PostMapping("/complete")
    public ResponseEntity<?> completeSession() {
        Long sessionId = 4L;
        sessionService.markSessionAsCompleted(sessionId);
        return ResponseEntity.ok().build();
    }

//    @PostMapping("/complete")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<String> completeSession() {
//        sessionService.completeLastSession();
//        return ResponseEntity.ok("Сессия завершена");
//    }

    @GetMapping("/{id}/summary")
    public ResponseEntity<SwotSummaryDto> getSummary(@PathVariable Long id) {
        return ResponseEntity.ok(swotService.buildSummary(id));
    }

    @PostMapping("/{id}/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateSummary(@PathVariable Long id, @RequestBody SwotSummaryDto summary) {
        swotService.saveSummary(id, summary);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/versions")
    public ResponseEntity<List<SwotVersionEntity>> getVersions(@PathVariable Long id) {
        return ResponseEntity.ok(swotService.getVersions(id));
    }

    @GetMapping("/{sessionId}/version/{versionId}")
    public ResponseEntity<SwotSummaryDto> getVersion(@PathVariable Long sessionId, @PathVariable Long versionId) {
        return ResponseEntity.ok(swotService.getVersionData(versionId));
    }

    @PostMapping("/{sessionId}/version/{versionId}/restore")
    public ResponseEntity<Void> restoreVersion(@PathVariable Long sessionId, @PathVariable Long versionId) {
        swotService.restoreVersion(sessionId, versionId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/alternatives")
    public List<AlternativeDto> getAlternatives() {
        return alternativeService.calculateAlternatives();
    }

    @PostMapping("/alternatives")
    public List<AlternativeDto> getAlternatives(@RequestBody List<Long> request) {
        return alternativeService.calculateSelectedAlternatives(request);
    }

    @PostMapping("/{sessionId}/versions")
    public ResponseEntity<SwotVersionEntity> createVersion(@PathVariable Long sessionId) {
        SwotVersionEntity version = sessionService.createNewVersion(sessionId);
        return ResponseEntity.ok(version);
    }
}