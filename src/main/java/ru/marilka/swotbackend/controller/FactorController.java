package ru.marilka.swotbackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.marilka.swotbackend.model.Factor;
import ru.marilka.swotbackend.model.request.SelectedFactorsRequest;
import ru.marilka.swotbackend.service.AlternativeService;
import ru.marilka.swotbackend.service.FactorService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/factors")
@CrossOrigin
@RequiredArgsConstructor
public class FactorController {

    private final FactorService factorService;
    private final AlternativeService alternativeService;

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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> saveSelectedFactors(@RequestBody SelectedFactorsRequest request) {
        alternativeService.calculateSelectedAlternatives(request.getFactorIds());
        return ResponseEntity.ok().build();
    }
}

