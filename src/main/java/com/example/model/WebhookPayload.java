package com.example.model;

import java.util.Map;
// Classe que representa o payload do webhook recebido do WhatsApp
// Contém o tipo de evento, número do remetente, mensagem e dados brutos
public class WebhookPayload {
    private String type;
    private String phone;
    private String message;
    private Map<String, Object> raw;

    public String getType() {
        return type;
    }

    public String getPhone() {
        return phone;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, Object> getRaw() {
        return raw;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setRaw(Map<String, Object> raw) {
        this.raw = raw;
    }
}
