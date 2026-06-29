package com.everjoy.aigateway.service;

import com.everjoy.aigateway.context.ContextEngine;
import com.everjoy.aigateway.model.ChatRequest;
import com.everjoy.aigateway.model.ChatResponse;
import com.everjoy.aigateway.provider.LlmProvider;
import com.everjoy.aigateway.provider.ProviderRegistry;
import com.everjoy.aigateway.routing.ModelRoutingService;
import com.everjoy.aigateway.schema.SchemaGuardService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class ChatService {
    private static final int MAX_PROMPT_CHARS = 4000;

    private final ModelRoutingService modelRoutingService;
    private final ProviderRegistry providerRegistry;
    private final ContextEngine contextEngine;
    private final SchemaGuardService schemaGuardService;
    private final ResponseCacheService responseCacheService;
    private final QuotaService quotaService;

    public ChatService(ModelRoutingService modelRoutingService,
                       ProviderRegistry providerRegistry,
                       ContextEngine contextEngine,
                       SchemaGuardService schemaGuardService,
                       ResponseCacheService responseCacheService,
                       QuotaService quotaService) {
        this.modelRoutingService = modelRoutingService;
        this.providerRegistry = providerRegistry;
        this.contextEngine = contextEngine;
        this.schemaGuardService = schemaGuardService;
        this.responseCacheService = responseCacheService;
        this.quotaService = quotaService;
    }

    public ChatResponse process(ChatRequest request) {
        if (!quotaService.tryAcquire(request.tenantId())) {
            throw new IllegalStateException("Tenant daily quota exceeded");
        }

        String trimmedPrompt = contextEngine.trimPrompt(request.prompt(), MAX_PROMPT_CHARS);
        String cacheKey = buildCacheKey(request, trimmedPrompt);
        Optional<ChatResponse> cached = responseCacheService.get(cacheKey);
        if (cached.isPresent()) {
            return cached.get();
        }

        String preferred = modelRoutingService.chooseModel(request.taskType(), trimmedPrompt);
        String content;
        String usedProvider;
        try {
            LlmProvider provider = providerRegistry.get(preferred);
            content = provider.generate(trimmedPrompt, request.requireJson(), request.schemaType());
            usedProvider = provider.name();
        } catch (Exception ex) {
            LlmProvider fallback = providerRegistry.getFallback();
            content = fallback.generate(trimmedPrompt, request.requireJson(), request.schemaType());
            usedProvider = fallback.name();
        }

        if (request.requireJson()) {
            boolean valid = schemaGuardService.validate(request.schemaType(), content);
            if (!valid) {
                throw new IllegalStateException("Model output does not match required schema");
            }
        }

        responseCacheService.put(cacheKey, content);

        return new ChatResponse(
                UUID.randomUUID().toString(),
                usedProvider,
                content,
                contextEngine.estimateTokens(trimmedPrompt),
                false,
                Instant.now()
        );
    }

    private String buildCacheKey(ChatRequest request, String trimmedPrompt) {
        return "chat:" + request.tenantId() + ":" + request.taskType() + ":" + request.requireJson() + ":" + trimmedPrompt.hashCode();
    }
}
