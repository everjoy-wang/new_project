package com.everjoy.aigateway.context;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContextEngineTest {

    @Test
    void shouldTrimPromptToConfiguredLimit() {
        ContextEngine engine = new ContextEngine();
        String prompt = "a".repeat(1500);

        String trimmed = engine.trimPrompt(prompt, 1000);

        assertEquals(1000, trimmed.length());
    }

    @Test
    void shouldEstimateTokensRoughly() {
        ContextEngine engine = new ContextEngine();

        int tokens = engine.estimateTokens("this is a sample prompt for estimating token usage");

        assertTrue(tokens > 0);
    }
}
