package ru.marilka.swotbackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.marilka.swotbackend.model.AlternativeDto;
import ru.marilka.swotbackend.model.AlternativeResultDto;
import ru.marilka.swotbackend.model.AlternativeRevealDto;
import ru.marilka.swotbackend.model.RevealDto;
import ru.marilka.swotbackend.model.request.RecalculateRequest;
import ru.marilka.swotbackend.service.AlternativeCalculationService;

import java.util.List;

@RestController
@RequestMapping("/api/session")
@RequiredArgsConstructor
@CrossOrigin
public class RecalculationController {

    private final AlternativeCalculationService calculationService;


    @PostMapping("/recalculate")
    public ResponseEntity<List<AlternativeDto>> recalculate(@RequestBody RecalculateRequest request) {
        List<AlternativeDto> result = calculationService.recalculateWithReveal(
                request.sessionId(), request.versionId(), request.revealList()
        );
        return ResponseEntity.ok(result);
    }

}

