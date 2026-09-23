package com.proyecto.backend.validation;

import java.util.regex.Pattern;

public final class Rut {
    
    private static final Pattern CUERPO = Pattern.compile("^\\d{6,10}$");

    private Rut() {
    }

    public static String normalizar(String rut) {
        if (rut == null) {
            return "";
        }

        return rut
                .replace(".", "")
                .replace("-", "")
                .replace(" ", "")
                .trim();
    }

    public static boolean esValido(String rut) {
        String limpio = normalizar(rut);

        return CUERPO.matcher(limpio).matches();
    }

    public static String formatear(String rut) {
        String limpio = normalizar(rut);

        if (limpio.isEmpty()) {
            return "";
        }

        StringBuilder resultado = new StringBuilder();

        int contador = 0;

        for (int i = limpio.length() - 1; i >= 0; i--) {

            if (contador == 3) {
                resultado.insert(0, '.');
                contador = 0;
            }

            resultado.insert(0, limpio.charAt(i));
            contador++;
        }

        return resultado.toString();
    }
}