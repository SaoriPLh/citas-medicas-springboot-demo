package com.saori.citas_medicas.controllers;

import com.saori.citas_medicas.dto.AuthResponse;
import com.saori.citas_medicas.dto.LoginRequest;
import com.saori.citas_medicas.dto.RegistroRequest;
import com.saori.citas_medicas.models.Usuario;
import com.saori.citas_medicas.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Usuario> registrarUsuario(@RequestBody RegistroRequest request) {
        Usuario usuario = authService.registrarUsuario(request);
        return ResponseEntity.ok(usuario);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        String token = authService.authenticate(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
