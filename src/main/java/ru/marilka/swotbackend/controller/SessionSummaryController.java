package ru.marilka.swotbackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.marilka.swotbackend.model.dto.AlternativeSummaryDto;
import ru.marilka.swotbackend.model.dto.SwotSummaryDto;
import ru.marilka.swotbackend.model.entity.SwotAlternativeEntity;
import ru.marilka.swotbackend.model.entity.SwotFactorEntity;
import ru.marilka.swotbackend.repository.AlternativeRepository;
import ru.marilka.swotbackend.repository.FactorRepository;
import ru.marilka.swotbackend.repository.SessionRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/session")
@RequiredArgsConstructor
@CrossOrigin
public class SessionSummaryController {

    private final SessionRepository sessionRepository;
    private final FactorRepository factorRepository;
    private final AlternativeRepository alternativeRepository;

        @GetMapping("/{sessionId}/summary")
        public ResponseEntity<SwotSummaryDto> getSummary(
                @PathVariable Long sessionId,
                @RequestParam Long versionId
        ) {
            var session = sessionRepository.findById(sessionId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            var allFactors = factorRepository.findBySessionIdAndVersionId(sessionId, versionId);
            Map<String, List<String>> grouped = new HashMap<>();
            Map<String, List<Integer>> factorNumbers = new HashMap<>();

            for (String type : List.of("strong", "weak", "opportunity", "threat")) {
                List<SwotFactorEntity> list = allFactors.stream()
                        .filter(f -> f.getType().equalsIgnoreCase(type))
                        .toList();
                grouped.put(type, list.stream().map(SwotFactorEntity::getTitle).toList());
                factorNumbers.put(type, list.stream().map(f -> {
                    double center = (f.getWeightMin() + 2 * f.getWeightMax() + 2 * f.getWeightAvg1() + f.getWeightAvg2()) / 6.0;
                    return (int) Math.round(center);
                }).toList());

            }

            List<SwotAlternativeEntity> alternatives = alternativeRepository.findBySessionIdAndVersionId(sessionId, versionId);
            List<AlternativeSummaryDto> altDtos = alternatives.stream()
                    .map(a -> new AlternativeSummaryDto(
                            a.getInternalFactor(),
                            a.getExternalFactor(),
                            a.getDMinus(),
                            a.getDPlus(),
                            a.getCloseness()
                    )).toList();

            SwotSummaryDto dto = new SwotSummaryDto(
                    session.getName(),
                    grouped,
                    factorNumbers,
                    altDtos
            );

            return ResponseEntity.ok(dto);
        }
    }


