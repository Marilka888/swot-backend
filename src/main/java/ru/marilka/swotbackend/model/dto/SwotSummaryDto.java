package ru.marilka.swotbackend.model.dto;

import java.util.List;
import java.util.Map;

public record SwotSummaryDto(
        String sessionName,
        Map<String, List<String>> factors,
        Map<String, List<Integer>> factorNumbers,
        List<AlternativeSummaryDto> alternatives
) {}

