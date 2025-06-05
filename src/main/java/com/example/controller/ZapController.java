package com.example.controller;

import com.example.model.MessageRequest;
import com.example.model.WebhookPayload;
import com.example.service.ZapApiService;
import com.example.handler.ChatWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.TextMessage;

@RestController
@RequestMapping("/api/zap")
public class ZapController {

    private final ZapApiService zapApiService;
    private final ChatWebSocketHandler chatWebSocketHandler;
    private final ObjectMapper mapper;

    public ZapController(ZapApiService zapApiService, 
                       ChatWebSocketHandler chatWebSocketHandler,
                       ObjectMapper mapper) {
        this.zapApiService = zapApiService;
        this.chatWebSocketHandler = chatWebSocketHandler;
        this.mapper = mapper;
    }
    // Envia uma mensagem para o WhatsApp usando a API do Zap
    @PostMapping("/messages")
    public ResponseEntity<String> sendMessage(@RequestBody MessageRequest messageRequest) {
        return zapApiService.sendMessage(messageRequest).block();
    }
    // Recebe o webhook do Zap e envia a mensagem para o WebSocket
    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(@RequestBody WebhookPayload payload) {
        if ("message".equals(payload.getType())) {
            String senderNumber = payload.getPhone();
            String content = payload.getMessage();

            ObjectNode messageJson = mapper.createObjectNode();
            messageJson.put("sender", "WhatsApp");
            messageJson.put("content", content);
            messageJson.put("phone", senderNumber);
            messageJson.put("id", System.currentTimeMillis());

            TextMessage textMessage = new TextMessage(messageJson.toString());
            chatWebSocketHandler.broadcastToUser(senderNumber, textMessage);
        }
        return ResponseEntity.ok().build();
    }
}