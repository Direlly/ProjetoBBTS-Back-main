package com.example.service;

import com.example.model.MessageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service  
public class ZapApiService {
    private final WebClient webClient;
    //service para enviar mensagens via Z-API
    public ZapApiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.z-api.io/instances/3DFD4E9927835081B0624E20A388CB1E/token/202C7046E979B1877AEFD521/send-text")
                .build();
    }

    public Mono<ResponseEntity<String>> sendMessage(MessageRequest messageRequest) {
        return webClient.post()
                .uri("/instances/{instanceId}/token/{token}/send-text", 
                     "3DFD4E9927835081B0624E20A388CB1E", 
                     "202C7046E979B1877AEFD521")
                .bodyValue(messageRequest)
                .retrieve()
                .toEntity(String.class);
    }
}