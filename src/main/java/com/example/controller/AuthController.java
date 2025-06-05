package com.example.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import com.example.model.User;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (user.getCpf() == null || user.getCpf().isEmpty()) {
            return ResponseEntity.badRequest().body("Dados de usuário inválidos");
        }
        System.out.println("Recebido usuário para registro: " + user);
       
        Map<String, String> response = new HashMap<>();
        response.put("token", "token-jwt-exemplo");
        response.put("message", "Usuário registrado com sucesso");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        if (loginRequest == null || loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
            return ResponseEntity.badRequest().body("Dados de login inválidos");
        }
        
        // Verificação simples de credenciais (apenas para exemplo)
        if (!"12345678900".equals(loginRequest.getUsername()) || !"senha123".equals(loginRequest.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("CPF ou senha inválidos");
        }
        
        Map<String, String> response = new HashMap<>();
        response.put("token", "token-jwt-exemplo");
        response.put("message", "Usuário logado com sucesso");
        return ResponseEntity.ok(response);
    }
    
    // Classe interna para o LoginRequest
    static class LoginRequest {
        private String username;
        private String password;
        
        // Getters e Setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}