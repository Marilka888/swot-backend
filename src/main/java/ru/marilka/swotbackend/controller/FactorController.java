package ru.marilka.swotbackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.marilka.swotbackend.model.Factor;
import ru.marilka.swotbackend.model.SelectedFactorsRequest;
import ru.marilka.swotbackend.model.entity.SwotSession;
import ru.marilka.swotbackend.model.entity.SwotSessionEntity;
import ru.marilka.swotbackend.service.AlternativeService;
import ru.marilka.swotbackend.service.FactorService;
import ru.marilka.swotbackend.service.SessionService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/factors")
@CrossOrigin
public class FactorController {

    private final FactorEventPublisher eventPublisher;
    private final SessionService sessionService;
    private final FactorService factorService;
    private final AlternativeService alternativeService;

    public FactorController(FactorEventPublisher eventPublisher, SessionService sessionService, FactorService factorService, AlternativeService alternativeService) {
        this.eventPublisher = eventPublisher;
        this.sessionService = sessionService;
        this.factorService = factorService;
        this.alternativeService = alternativeService;
    }

//    @GetMapping
//    public ResponseEntity<List<Factor>> getFactors() {
//        return ResponseEntity.ok(factorService.getAll());
//    }

    @GetMapping("/results")
    public ResponseEntity<List<Factor>> getResultFactors() {
        return ResponseEntity.ok(factorService.getAll());
    }
    @GetMapping
    public ResponseEntity<List<Factor>> getFactorsBySessionAndVersion(
            @RequestParam("sessionId") Long sessionId,
            @RequestParam("versionId") Long versionId) {

        List<Factor> factors = factorService.getFactors(sessionId, versionId);
        return ResponseEntity.ok(factors);
    }
    @PostMapping
    public ResponseEntity<?> createFactor(@RequestBody Factor request) {
        Factor saved = factorService.create(request);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/strong")
    public ResponseEntity<?> editStrongFactors(@RequestBody List<Factor> request) {
        List<Factor> updated = factorService.update(request);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/weak")
    public ResponseEntity<?> editWeakFactors(@RequestBody List<Factor> request) {
        List<Factor> updated = factorService.update(request);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/opportunity")
    public ResponseEntity<?> editOpportunityFactors(@RequestBody List<Factor> request) {
        List<Factor> updated = factorService.update(request);
        return ResponseEntity.ok(updated);
    }


    @PostMapping("/threat")
    public ResponseEntity<?> editThreatFactors(@RequestBody List<Factor> request) {
        List<Factor> updated = factorService.update(request);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/selected")
    public ResponseEntity<Void> saveSelectedFactors(@RequestBody SelectedFactorsRequest request) {
        alternativeService.calculateSelectedAlternatives(request.getFactorIds());
        return ResponseEntity.ok().build();
    }
}

