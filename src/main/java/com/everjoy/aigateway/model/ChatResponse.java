package com.everjoy.aigateway.model;

import java.time.Instant;

public record ChatResponse(
        String requestId,
        String provider,
        String content,
        int estimatedTokens,
        boolean fromCache,
        Instant createdAt
) {
}
