package com.proyecto.backend.service;

import com.proyecto.backend.model.Rol;
import com.proyecto.backend.model.Usuario;
import com.proyecto.backend.repository.UsuarioRepository;
import com.proyecto.backend.security.PasswordHasher;
import com.proyecto.backend.security.SesionActual;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> listar() {
        SesionActual.exigirRol(Rol.ADMINISTRADOR);
        return usuarioRepository.findAll();
    }

    public Usuario guardar(Usuario usuario) {
        SesionActual.exigirRol(Rol.ADMINISTRADOR);
        validar(usuario, true);

        if (usuarioRepository.existsByEmailIgnoreCase(usuario.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese correo");
        }
        if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }
        if (usuario.getRol() == null) {
            usuario.setRol(Rol.USUARIO);
        }
        usuario.setEmail(usuario.getEmail().trim().toLowerCase());
        usuario.setPassword(PasswordHasher.hash(usuario.getPassword()));
        usuario.setToken(null);
        return usuarioRepository.save(usuario);
    }

    public Usuario actualizar(Long id, Usuario datos) {
        SesionActual.exigirRol(Rol.ADMINISTRADOR);
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        validar(datos, false);
        if (usuarioRepository.existsByEmailIgnoreCaseAndIdNot(datos.getEmail().trim(), id)) {
            throw new IllegalArgumentException("Ya existe un usuario con ese correo");
        }

        existente.setNombre(datos.getNombre().trim());
        existente.setEmail(datos.getEmail().trim().toLowerCase());
        if (datos.getRol() != null) {
            existente.setRol(datos.getRol());
        }
        if (datos.getPassword() != null && !datos.getPassword().isBlank()) {
            existente.setPassword(PasswordHasher.hash(datos.getPassword()));
        }
        return usuarioRepository.save(existente);
    }

    public void eliminar(Long id) {
        SesionActual.exigirRol(Rol.ADMINISTRADOR);
        if (!usuarioRepository.existsById(id)) {
            throw new NoSuchElementException("Usuario no encontrado");
        }
        usuarioRepository.deleteById(id);
    }

    private void validar(Usuario usuario, boolean alta) {
        if (usuario.getNombre() == null || usuario.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            throw new IllegalArgumentException("El correo es obligatorio");
        }
        if (!usuario.getEmail().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("El correo no tiene un formato válido");
        }
        if (alta && (usuario.getPassword() == null || usuario.getPassword().length() < 6)) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
        }
    }
}
