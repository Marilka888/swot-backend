package ru.marilka.swotbackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.marilka.swotbackend.model.*;
import ru.marilka.swotbackend.model.entity.AlternativeDto;
import ru.marilka.swotbackend.model.entity.SwotVersionEntity;
import ru.marilka.swotbackend.service.AlternativeService;
import ru.marilka.swotbackend.service.SwotService;

import java.util.List;

@RestController
@RequestMapping("/api/session")
@CrossOrigin
public class SwotController {

    private final SwotService swotService;
    private final AlternativeService alternativeService;

    public SwotController(SwotService swotService, AlternativeService alternativeService) {
        this.swotService = swotService;
        this.alternativeService = alternativeService;
    }

    @GetMapping("/{id}/summary")
    public ResponseEntity<SwotSummaryDto> getSummary(@PathVariable Long id) {
        return ResponseEntity.ok(swotService.buildSummary(id));
    }

    @PostMapping("/{id}/summary")
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
}