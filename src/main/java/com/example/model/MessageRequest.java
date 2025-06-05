package com.example.model;

// Classe que representa uma requisição de mensagem para o WhatsApp
// Contém o número do destinatário e o conteúdo da mensagem
public class MessageRequest {
    private String number;
    private String message;

    public String getNumber() {
        return number;
    }
    public void setNumber(String number) {
        this.number = number;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
