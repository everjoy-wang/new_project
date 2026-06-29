package com.everjoy.aigateway.provider;

public interface LlmProvider {
    String name();

    String generate(String prompt, boolean requireJson, String schemaType);
}
