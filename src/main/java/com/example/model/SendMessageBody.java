package com.example.model;

// Classe que representa uma requisição de mensagem para o WhatsApp
// Contém o número do destinatário e o conteúdo da mensagem
public class SendMessageBody {
    private String number;
    private String message;

    public SendMessageBody(String number, String message) {
        this.number = number;
        this.message = message;
    }

    // Getters e Setters
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
