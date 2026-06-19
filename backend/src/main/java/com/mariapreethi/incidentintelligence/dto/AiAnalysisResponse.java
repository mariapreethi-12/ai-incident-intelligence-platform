package com.mariapreethi.incidentintelligence.dto;

import java.util.List;

public record AiAnalysisResponse(
        String title,
        String summary,
        String rootCause,
        List<String> affectedServices,
        List<String> timeline,
        List<String> recommendedDebuggingSteps
) {
}
