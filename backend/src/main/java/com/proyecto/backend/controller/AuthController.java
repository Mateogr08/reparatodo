package com.proyecto.backend.controller;

import com.proyecto.backend.security.SesionActual;
import com.proyecto.backend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> cuerpo) {
        return ResponseEntity.ok(authService.login(cuerpo.get("email"), cuerpo.get("password")));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        authService.logout(SesionActual.get());
        return ResponseEntity.noContent().build();
    }
}
