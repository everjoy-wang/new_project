package com.everjoy.aigateway.routing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ModelRoutingServiceTest {

    @Test
    void shouldPickSmartModelForAnalysisTasks() {
        ModelRoutingService service = new ModelRoutingService();

        String chosen = service.chooseModel("analysis", "summarize this enterprise incident");

        assertEquals("smart-provider", chosen);
    }

    @Test
    void shouldPickSmartModelForLongPrompt() {
        ModelRoutingService service = new ModelRoutingService();
        String longPrompt = "x".repeat(1200);

        String chosen = service.chooseModel("general", longPrompt);

        assertEquals("smart-provider", chosen);
    }

    @Test
    void shouldPickFastModelForSimpleTasks() {
        ModelRoutingService service = new ModelRoutingService();

        String chosen = service.chooseModel("general", "hello");

        assertEquals("fast-provider", chosen);
    }
}
