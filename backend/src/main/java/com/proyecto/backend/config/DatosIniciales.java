package com.proyecto.backend.config;

import com.proyecto.backend.model.Rol;
import com.proyecto.backend.model.Usuario;
import com.proyecto.backend.repository.UsuarioRepository;
import com.proyecto.backend.security.PasswordHasher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatosIniciales implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;

    public DatosIniciales(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void run(String... args) {
        usuarioRepository.findByEmailIgnoreCase("admin@reparatodo.com").ifPresentOrElse(
                usuario -> {
                    if (usuario.getRol() == null) {
                        usuario.setRol(Rol.ADMINISTRADOR);
                    }
                    if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
                        usuario.setPassword(PasswordHasher.hash("Admin123"));
                    }
                    usuarioRepository.save(usuario);
                },
                () -> {
                    Usuario admin = new Usuario();
                    admin.setNombre("Administrador");
                    admin.setEmail("admin@reparatodo.com");
                    admin.setPassword(PasswordHasher.hash("Admin123"));
                    admin.setRol(Rol.ADMINISTRADOR);
                    usuarioRepository.save(admin);
                }
        );
    }
}
