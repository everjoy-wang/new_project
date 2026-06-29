package com.everjoy.aigateway.async;

import com.everjoy.aigateway.model.AsyncChatStatus;
import com.everjoy.aigateway.model.ChatRequest;
import com.everjoy.aigateway.model.ChatResponse;
import com.everjoy.aigateway.service.ChatService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AsyncChatService {
    public static final String CHAT_TASK_QUEUE = "ai.gateway.chat.task.queue";
    private final Map<String, AsyncChatStatus> statusStore = new ConcurrentHashMap<>();
    private final ChatService chatService;
    private final RabbitTemplate rabbitTemplate;

    public AsyncChatService(ChatService chatService, RabbitTemplate rabbitTemplate) {
        this.chatService = chatService;
        this.rabbitTemplate = rabbitTemplate;
    }

    public String submit(ChatRequest request) {
        String taskId = UUID.randomUUID().toString();
        statusStore.put(taskId, new AsyncChatStatus(taskId, "PENDING", null, null));
        ChatTaskPayload payload = new ChatTaskPayload(taskId, request);
        try {
            rabbitTemplate.convertAndSend(CHAT_TASK_QUEUE, payload);
        } catch (Exception ex) {
            process(payload);
        }
        return taskId;
    }

    public AsyncChatStatus status(String taskId) {
        return statusStore.getOrDefault(taskId, new AsyncChatStatus(taskId, "NOT_FOUND", null, "task not found"));
    }

    @RabbitListener(queues = CHAT_TASK_QUEUE)
    public void process(ChatTaskPayload payload) {
        String taskId = payload.taskId();
        statusStore.put(taskId, new AsyncChatStatus(taskId, "RUNNING", null, null));
        try {
            ChatResponse response = chatService.process(payload.request());
            statusStore.put(taskId, new AsyncChatStatus(taskId, "SUCCESS", response, null));
        } catch (Exception ex) {
            statusStore.put(taskId, new AsyncChatStatus(taskId, "FAILED", null, ex.getMessage()));
        }
    }
}
