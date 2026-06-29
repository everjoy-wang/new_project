package com.everjoy.aigateway.provider;

import org.springframework.stereotype.Component;

@Component
public class SmartMockProvider implements LlmProvider {
    @Override
    public String name() {
        return "smart-provider";
    }

    @Override
    public String generate(String prompt, boolean requireJson, String schemaType) {
        if (requireJson && "support_ticket".equalsIgnoreCase(schemaType)) {
            return "{\"title\":\"智能工单\",\"priority\":\"P1\",\"summary\":\"已分析: " + sanitize(prompt) + "\"}";
        }
        return "[SMART] " + prompt;
    }

    private String sanitize(String prompt) {
        if (prompt == null) {
            return "";
        }
        return prompt.replace("\"", "'");
    }
}
