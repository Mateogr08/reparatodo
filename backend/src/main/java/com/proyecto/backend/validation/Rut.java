package com.proyecto.backend.validation;

import java.util.regex.Pattern;

/**
 * RUT colombiano (NIT de persona natural o jurídica) con dígito de verificación DIAN.
 */
public final class Rut {

    private static final Pattern NUMERO = Pattern.compile("^\\d{6,10}$");
    private static final int[] PESOS = {3, 7, 13, 17, 19, 23, 29, 37, 41, 43, 47, 53, 59, 67, 71};

    private Rut() {
    }

    public static String normalizar(String rut) {
        if (rut == null) {
            return "";
        }
        return rut.replaceAll("\\D", "");
    }

    public static boolean esValido(String rut) {
        String limpio = normalizar(rut);
        if (limpio.length() < 7 || limpio.length() > 11) {
            return false;
        }

        String numero = limpio.substring(0, limpio.length() - 1);
        String dv = limpio.substring(limpio.length() - 1);

        if (!NUMERO.matcher(numero).matches()) {
            return false;
        }

        return dv.equals(String.valueOf(calcularDv(numero)));
    }

    public static String formatear(String rut) {
        String limpio = normalizar(rut);
        if (limpio.length() < 2) {
            return rut == null ? "" : rut.trim();
        }

        String numero = limpio.substring(0, limpio.length() - 1);
        String dv = limpio.substring(limpio.length() - 1);

        StringBuilder conPuntos = new StringBuilder();
        int contador = 0;
        for (int i = numero.length() - 1; i >= 0; i--) {
            if (contador == 3) {
                conPuntos.insert(0, '.');
                contador = 0;
            }
            conPuntos.insert(0, numero.charAt(i));
            contador++;
        }
        return conPuntos + "-" + dv;
    }

    static int calcularDv(String numero) {
        int suma = 0;
        for (int i = 0; i < numero.length(); i++) {
            int digito = Character.getNumericValue(numero.charAt(numero.length() - 1 - i));
            suma += digito * PESOS[i];
        }
        int residuo = suma % 11;
        return residuo > 1 ? 11 - residuo : residuo;
    }
}
