package com.everjoy.aigateway.model;

public record AsyncChatStatus(
        String taskId,
        String status,
        ChatResponse response,
        String error
) {
}
