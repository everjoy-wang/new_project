package com.everjoy.aigateway.api;

import com.everjoy.aigateway.async.AsyncChatService;
import com.everjoy.aigateway.model.AsyncChatAccepted;
import com.everjoy.aigateway.model.AsyncChatStatus;
import com.everjoy.aigateway.model.ChatRequest;
import com.everjoy.aigateway.model.ChatResponse;
import com.everjoy.aigateway.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ChatController {
    private final ChatService chatService;
    private final AsyncChatService asyncChatService;

    public ChatController(ChatService chatService, AsyncChatService asyncChatService) {
        this.chatService = chatService;
        this.asyncChatService = asyncChatService;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok");
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        return ResponseEntity.ok(chatService.process(request));
    }

    @PostMapping("/chat/async")
    public ResponseEntity<AsyncChatAccepted> chatAsync(@Valid @RequestBody ChatRequest request) {
        String taskId = asyncChatService.submit(request);
        return ResponseEntity.accepted().body(new AsyncChatAccepted(taskId, "PENDING"));
    }

    @GetMapping("/chat/async/{taskId}")
    public ResponseEntity<AsyncChatStatus> queryTask(@PathVariable String taskId) {
        return ResponseEntity.ok(asyncChatService.status(taskId));
    }
}
