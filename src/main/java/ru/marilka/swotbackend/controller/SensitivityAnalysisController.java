package ru.marilka.swotbackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.marilka.swotbackend.model.dto.SensitivityResultDto;
import ru.marilka.swotbackend.model.request.SensitivityAnalysisRequest;
import ru.marilka.swotbackend.service.SensitivityAnalysisService;

import java.util.List;

@RestController
@RequestMapping("/api/session")
@RequiredArgsConstructor
@CrossOrigin
public class SensitivityAnalysisController {

    private final SensitivityAnalysisService sensitivityAnalysisService;

    @PostMapping("/sensitivity-analysis")
    public ResponseEntity<List<SensitivityResultDto>> performSensitivityAnalysis(
            @RequestBody SensitivityAnalysisRequest request
    ) {
        List<SensitivityResultDto> result = sensitivityAnalysisService.analyze(request.getSessionId(), request.getVersionId(),
                request.getTrapezoidDifference()
        );
        return ResponseEntity.ok(result);
    }
}