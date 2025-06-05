package com.example.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.model.User;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {

        System.out.println("Recebido usuário para registro: " + user);
       
        return ResponseEntity.ok("Usuário registrado com sucesso");
    }
}
