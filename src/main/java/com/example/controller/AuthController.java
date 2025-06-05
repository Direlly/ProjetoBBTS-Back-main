package com.example.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.model.User;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (user.getCpf() == null || user.getCpf().isEmpty()) {
            return ResponseEntity.badRequest().body("Dados de usuário inválidos");
        }
        System.out.println("Recebido usuário para registro: " + user);
       
        return ResponseEntity.ok("Usuário registrado com sucesso");

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
        if (!"12345678900".equals(loginRequest.getUserCpf())) || !"senha123".equals(loginRequest.getPassword())) {
            return ResponseEntity.status().(HttpStatus.UNAUTHORIRIZED).body("CPF ou senha inválidos");
        }
        String token = "token-jwt-exemplo"; 

        map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("message", "Usuário logado com sucesso");
        return ResponseEntity.ok("Usuário logado com sucesso");
    
    }
    
}
