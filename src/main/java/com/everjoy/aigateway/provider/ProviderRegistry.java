package com.everjoy.aigateway.provider;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ProviderRegistry {
    private final Map<String, LlmProvider> providers;

    public ProviderRegistry(List<LlmProvider> providers) {
        this.providers = providers.stream().collect(Collectors.toMap(LlmProvider::name, Function.identity()));
    }

    public LlmProvider get(String providerName) {
        LlmProvider provider = providers.get(providerName);
        if (provider == null) {
            throw new IllegalArgumentException("Provider not found: " + providerName);
        }
        return provider;
    }

    public LlmProvider getFallback() {
        return get("smart-provider");
    }
}
