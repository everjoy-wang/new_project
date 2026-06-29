package com.everjoy.aigateway.context;

import org.springframework.stereotype.Component;

@Component
public class ContextEngine {

    public String trimPrompt(String prompt, int maxChars) {
        if (prompt == null) {
            return "";
        }
        if (prompt.length() <= maxChars) {
            return prompt;
        }
        return prompt.substring(0, maxChars);
    }

    public int estimateTokens(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        // Rough estimate: 1 token ~= 4 characters for mixed text.
        return Math.max(1, text.length() / 4);
    }
}
