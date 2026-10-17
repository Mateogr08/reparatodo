package com.proyecto.backend.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public final class PasswordHasher {

    private PasswordHasher() {
    }

    public static String hash(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No se pudo cifrar la contraseña");
        }
    }

    public static boolean coincide(String password, String hashGuardado) {
        if (password == null || hashGuardado == null) {
            return false;
        }
        return hash(password).equalsIgnoreCase(hashGuardado);
    }
}
