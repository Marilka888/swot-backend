package ru.marilka.swotbackend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.marilka.swotbackend.model.SummaryResponse;
import ru.marilka.swotbackend.service.SessionService;

@RestController
@RequestMapping("/api/session")
@CrossOrigin
public class SessionSummaryController {

    private final SessionService sessionService;

    public SessionSummaryController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping("/{id}/summary/swot")
    public ResponseEntity<SummaryResponse> getSummary(@PathVariable Long id) {
        SummaryResponse summary = sessionService.buildSummary(id);
        return ResponseEntity.ok(summary);
    }

    @PostMapping("/{id}/summary/swot")
    public ResponseEntity<Void> updateSummary(@PathVariable Long id, @RequestBody SummaryResponse request) throws JsonProcessingException {
        sessionService.updateSummary(id, request);
        return ResponseEntity.ok().build();
    }
}

