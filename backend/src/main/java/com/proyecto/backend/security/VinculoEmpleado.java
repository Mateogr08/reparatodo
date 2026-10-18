package com.proyecto.backend.security;

import com.proyecto.backend.model.Empleado;
import com.proyecto.backend.model.Rol;
import com.proyecto.backend.model.Usuario;
import com.proyecto.backend.repository.EmpleadoRepository;

import java.util.Optional;

/**
 * Vincula la cuenta de usuario (correo de login) con la ficha {@link Empleado} (campo correo).
 */
public final class VinculoEmpleado {

    private VinculoEmpleado() {
    }

    public static boolean puedeGestionarTodasLasFichas() {
        return SesionActual.tieneRol(Rol.ADMINISTRADOR, Rol.ENCARGADO_PERSONAL);
    }

    public static Optional<Empleado> fichaDelUsuarioActual(EmpleadoRepository empleadoRepository) {
        Usuario usuario = SesionActual.get();
        if (usuario == null || usuario.getEmail() == null) {
            return Optional.empty();
        }
        return empleadoRepository.findByCorreoIgnoreCase(usuario.getEmail());
    }

    public static Empleado exigirFichaVinculada(EmpleadoRepository empleadoRepository) {
        Usuario usuario = SesionActual.get();
        if (usuario == null) {
            throw new SecurityException("Debe iniciar sesión");
        }
        return fichaDelUsuarioActual(empleadoRepository)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Su cuenta no está vinculada a una ficha de empleado. "
                                + "El correo del usuario debe coincidir con el correo registrado en la ficha."
                ));
    }

    public static void exigirAccesoFicha(Empleado empleado) {
        Usuario usuario = SesionActual.get();
        if (usuario == null) {
            throw new SecurityException("Debe iniciar sesión");
        }
        if (puedeGestionarTodasLasFichas()) {
            return;
        }
        if (usuario.getRol() == Rol.EMPLEADO
                && empleado.getCorreo() != null
                && usuario.getEmail().equalsIgnoreCase(empleado.getCorreo())) {
            return;
        }
        throw new SecurityException("No tiene permiso para consultar este empleado");
    }
}
