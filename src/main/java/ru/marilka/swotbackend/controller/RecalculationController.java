package ru.marilka.swotbackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.marilka.swotbackend.model.AlternativeResultDto;
import ru.marilka.swotbackend.model.AlternativeRevealDto;
import ru.marilka.swotbackend.service.AlternativeCalculationService;

import java.util.List;

@RestController
@RequestMapping("/api/session")
@RequiredArgsConstructor
@CrossOrigin
public class RecalculationController {

    private final AlternativeCalculationService calculationService;

    @PostMapping("/recalculate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AlternativeResultDto>> recalculate(@RequestBody List<AlternativeRevealDto> reveals) {
        List<AlternativeResultDto> results = calculationService.recalculateAlternatives(reveals);
        return ResponseEntity.ok(results);
    }
}

