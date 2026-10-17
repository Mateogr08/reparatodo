package com.proyecto.backend.service;

import com.proyecto.backend.model.Usuario;
import com.proyecto.backend.repository.UsuarioRepository;
import com.proyecto.backend.security.PasswordHasher;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Map<String, Object> login(String email, String password) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email == null ? "" : email.trim())
                .orElseThrow(() -> new IllegalArgumentException("Correo o contraseña incorrectos"));

        if (!PasswordHasher.coincide(password, usuario.getPassword())) {
            throw new IllegalArgumentException("Correo o contraseña incorrectos");
        }

        usuario.setToken(UUID.randomUUID().toString());
        usuarioRepository.save(usuario);

        return Map.of(
                "token", usuario.getToken(),
                "id", usuario.getId(),
                "nombre", usuario.getNombre(),
                "email", usuario.getEmail(),
                "rol", usuario.getRol().name()
        );
    }

    public void logout(Usuario usuario) {
        if (usuario != null) {
            usuario.setToken(null);
            usuarioRepository.save(usuario);
        }
    }
}
