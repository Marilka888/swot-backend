package ru.marilka.swotbackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.marilka.swotbackend.model.AlternativeDto;
import ru.marilka.swotbackend.model.dto.FactorDto;
import ru.marilka.swotbackend.model.dto.SensitivityResultDto;
import ru.marilka.swotbackend.model.request.SensitivityAnalysisRequest;
import ru.marilka.swotbackend.repository.SessionRepository;
import ru.marilka.swotbackend.service.AlternativeService;
import ru.marilka.swotbackend.service.FactorService;
import ru.marilka.swotbackend.service.SensitivityAnalysisService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/session")
@RequiredArgsConstructor
@CrossOrigin
public class SensitivityAnalysisController {

    private final SensitivityAnalysisService sensitivityAnalysisService;
    private final SessionRepository sessionRepository;

    @GetMapping("/sensitivity-analysis/pdf")
    public ResponseEntity<byte[]> exportSensitivityPdf(
            @RequestParam Long sessionId,
            @RequestParam Long versionId,
            @RequestParam double delta,
            @RequestParam double factorDistance
    ) throws IOException {
        var sessionEntity = sessionRepository.findById(sessionId).orElseThrow();
        String sessionName = "SWOT Сессия " + sessionEntity.getName();

        var result = sensitivityAnalysisService.runDetailedAnalysis(
                sessionId,
                versionId,
                delta,
                factorDistance
        );
        byte[] pdf = sensitivityAnalysisService.exportSensitivityAnalysis(sessionName, result);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "results.pdf");

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }

    @PostMapping("/sensitivity-analysis")
    public ResponseEntity<?> runSensitivityAnalysis(@RequestBody SensitivityAnalysisRequest request) {
        var result = sensitivityAnalysisService.runDetailedAnalysis(
                request.getSessionId(),
                request.getVersionId(),
                request.getDelta(),
                request.getFactorDistance()
        );
        return ResponseEntity.ok(result);
    }
}