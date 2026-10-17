package com.proyecto.backend.security;

import com.proyecto.backend.model.Rol;
import com.proyecto.backend.model.Usuario;

public final class SesionActual {

    private static final ThreadLocal<Usuario> USUARIO = new ThreadLocal<>();

    private SesionActual() {
    }

    public static void set(Usuario usuario) {
        USUARIO.set(usuario);
    }

    public static Usuario get() {
        return USUARIO.get();
    }

    public static void clear() {
        USUARIO.remove();
    }

    public static String nombre() {
        Usuario usuario = USUARIO.get();
        return usuario == null ? "sistema" : usuario.getEmail();
    }

    public static void exigirRol(Rol... rolesPermitidos) {
        Usuario usuario = USUARIO.get();
        if (usuario == null) {
            throw new SecurityException("Debe iniciar sesión");
        }
        for (Rol rol : rolesPermitidos) {
            if (usuario.getRol() == rol) {
                return;
            }
        }
        throw new SecurityException("No tiene permiso para esta operación");
    }

    public static boolean tieneRol(Rol... roles) {
        Usuario usuario = USUARIO.get();
        if (usuario == null) {
            return false;
        }
        for (Rol rol : roles) {
            if (usuario.getRol() == rol) {
                return true;
            }
        }
        return false;
    }
}
