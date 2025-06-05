package com.example.handler;

import org.springframework.lang.NonNull;
import com.example.model.MessageRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.socket.CloseStatus;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
// Importações necessárias para o WebSocket e manipulação de mensagens
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Set<WebSocketSession> allSessions = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private static final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    private static final Set<WebSocketSession> adminSessions = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private static final Map<WebSocketSession, String> sessionCpfMap = new ConcurrentHashMap<>();
    
    private final String INSTANCE_ID = "3DFD4E9927835081B0624E20A388CB1E";
    private final String TOKEN = "202C7046E979B1877AEFD521";
    private final WebClient zapWebClient;

    public ChatWebSocketHandler() {
        this.zapWebClient = WebClient.builder()
            .baseUrl("https://api.z-api.io/instances/" + INSTANCE_ID + "/token/" + TOKEN)
            .defaultHeader("Content-Type", "application/json")
            .build();
    }
    // Método chamado quando uma nova conexão WebSocket é estabelecida
    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        allSessions.add(session);
        URI uri = session.getUri();
        String path = uri != null ? uri.getPath() : "";
        System.out.println("Nova conexão estabelecida no caminho: " + path);

        String query = uri != null ? uri.getQuery() : "";
        if (query != null && query.contains("admin=true")) {
            adminSessions.add(session);
            System.out.println("Sessão marcada como admin.");
        } else {
            System.out.println("Sessão de usuário comum conectada.");
        }
    }
    // Método chamado quando uma mensagem de texto é recebida
    @Override
    public void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {
        JsonNode json = mapper.readTree(message.getPayload());

        if (json.has("type") && "init".equals(json.get("type").asText())) {
            handleFrontendInit(session, json);
            return;
        }

        if (json.has("sender") && json.has("content")) {
            handleRegularMessage(session, json);
            return;
        }

        System.out.println("Mensagem recebida de formato desconhecido: " + message.getPayload());
    }
    // Método para lidar com a inicialização do frontend
    private void handleFrontendInit(WebSocketSession session, JsonNode json) throws IOException {
        String username = json.get("username").asText();
        String cpf = json.get("cpf").asText();

        userSessions.put(cpf, session);
        sessionCpfMap.put(session, cpf);

        ObjectNode initMsg = mapper.createObjectNode();
        initMsg.put("type", "init");
        initMsg.put("username", username);
        initMsg.put("cpf", cpf);

        broadcastToAdmins(initMsg.toString());
        System.out.println("Usuário iniciado: " + username + " (" + cpf + ")");
    }
    // Método para lidar com mensagens regulares enviadas pelos usuários
    private void handleRegularMessage(WebSocketSession session, JsonNode json) throws IOException {
        String sender = json.get("sender").asText();
        String content = json.get("content").asText();
        String cpf = json.has("cpf") ? json.get("cpf").asText() : sessionCpfMap.get(session);
        long id = json.get("id").asLong();
        long replyTo = json.has("replyTo") ? json.get("replyTo").asLong() : 0;

        ObjectNode fullMsg = mapper.createObjectNode();
        fullMsg.put("id", id);
        fullMsg.put("sender", sender);
        fullMsg.put("content", content);
        fullMsg.put("cpf", cpf);
        if (replyTo != 0) fullMsg.put("replyTo", replyTo);

        TextMessage textMsg = new TextMessage(fullMsg.toString());

        if ("Admin".equals(sender)) {
            WebSocketSession userSession = userSessions.get(cpf);
            if (userSession != null && userSession.isOpen()) {
                userSession.sendMessage(textMsg);
                System.out.println("Mensagem do admin enviada para usuário " + cpf);
            } else {
                sendToZapApi(content, cpf);
            }
            broadcastToAdmins(fullMsg.toString());
        } else {
            broadcastToAdmins(fullMsg.toString());
        }
    }

    private void broadcastToAdmins(String message) throws IOException {
        for (WebSocketSession admin : adminSessions) {
            if (admin.isOpen()) {
                admin.sendMessage(new TextMessage(message));
            }
        }
    }
    // Método chamado quando uma conexão WebSocket é fechada
    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        allSessions.remove(session);
        adminSessions.remove(session);

        String cpf = sessionCpfMap.remove(session);
        if (cpf != null) {
            userSessions.remove(cpf);
            System.out.println("Usuário " + cpf + " desconectado");
        }

        System.out.println("Sessão desconectada.");
    }
    // Método para enviar uma mensagem específica para um usuário conectado via WebSocket
    public void broadcastToUser(String cpf, TextMessage message) {
        WebSocketSession userSession = userSessions.get(cpf);
        if (userSession != null && userSession.isOpen()) {
            try {
                userSession.sendMessage(message);
                System.out.println("Mensagem do webhook enviada para usuário " + cpf);
            } catch (IOException e) {
                System.err.println("Erro ao enviar mensagem via WebSocket: " + e.getMessage());
            }
        } else {
            System.out.println("Usuário " + cpf + " não está conectado ou a sessão está fechada.");
        }
    }
    // Método para enviar uma mensagem para a API Z-API
    private void sendToZapApi(String message, String number) {
        try {
            MessageRequest request = new MessageRequest();
            request.setNumber(number);
            request.setMessage(message);
            zapWebClient.post()
                .uri("/send-text")
                .bodyValue(request)
                .retrieve()
                .toEntity(String.class)
                .block();
        } catch (Exception e) {
            System.err.println("Erro ao enviar para Z-API: " + e.getMessage());
        }
    }
}