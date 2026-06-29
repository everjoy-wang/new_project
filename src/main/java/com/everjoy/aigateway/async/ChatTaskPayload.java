package com.everjoy.aigateway.async;

import com.everjoy.aigateway.model.ChatRequest;

public record ChatTaskPayload(String taskId, ChatRequest request) {
}
