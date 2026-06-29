package com.everjoy.aigateway.provider;

import org.springframework.stereotype.Component;

@Component
public class FastMockProvider implements LlmProvider {
    @Override
    public String name() {
        return "fast-provider";
    }

    @Override
    public String generate(String prompt, boolean requireJson, String schemaType) {
        if (prompt != null && prompt.contains("force-fail-fast")) {
            throw new IllegalStateException("Simulated provider failure");
        }
        if (requireJson && "support_ticket".equalsIgnoreCase(schemaType)) {
            return "{\"title\":\"自动工单\",\"priority\":\"P2\",\"summary\":\"" + sanitize(prompt) + "\"}";
        }
        return "[FAST] " + prompt;
    }

    private String sanitize(String prompt) {
        if (prompt == null) {
            return "";
        }
        return prompt.replace("\"", "'");
    }
}
