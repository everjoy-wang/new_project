package com.everjoy.aigateway.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatRequest(
        @NotBlank String tenantId,
        @NotBlank String userId,
        @NotBlank String taskType,
        @NotBlank @Size(max = 8000) String prompt,
        boolean requireJson,
        String schemaType
) {
}
