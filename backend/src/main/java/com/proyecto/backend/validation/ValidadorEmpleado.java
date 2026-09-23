package com.proyecto.backend.validation;

import com.proyecto.backend.model.Empleado;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

public final class ValidadorEmpleado {

    public static final int EDAD_MINIMA = 18;
    public static final int EDAD_MAXIMA = 75;

    public static final BigDecimal SUELDO_MINIMO =
            new BigDecimal("2000000");

    public static final BigDecimal SUELDO_MAXIMO =
            new BigDecimal("20000000");

    private static final Set<String> JORNADAS =
            Set.of("Completa", "Parcial", "Turnos");

    private static final Pattern NOMBRE =
            Pattern.compile("^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ' ]{5,80}$");

    private static final Pattern CORREO =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    private static final Pattern TELEFONO_MOVIL =
            Pattern.compile("^(\\+?57)?\\s?3\\d{2}\\s?\\d{3}\\s?\\d{4}$");

    private ValidadorEmpleado() {
    }

    public static void validar(Empleado empleado) {

        // =========================
        // NOMBRE
        // =========================

        if (empleado.getNombre() == null ||
                empleado.getNombre().isBlank()) {

            throw new IllegalArgumentException(
                    "El nombre completo es obligatorio"
            );
        }

        empleado.setNombre(
                normalizarTexto(empleado.getNombre())
        );

        if (!NOMBRE.matcher(empleado.getNombre()).matches()) {

            throw new IllegalArgumentException(
                    "El nombre solo puede incluir letras, espacios y tildes (5 a 80 caracteres)"
            );
        }

        if (empleado.getNombre().trim().split("\\s+").length < 2) {

            throw new IllegalArgumentException(
                    "Ingresa nombre y apellido"
            );
        }


        // =========================
        // DOCUMENTO / RUT
        // =========================

        if (empleado.getRut() == null ||
                empleado.getRut().isBlank()) {

            throw new IllegalArgumentException(
                    "El número de documento es obligatorio"
            );
        }

        if (!Rut.esValido(empleado.getRut())) {

            throw new IllegalArgumentException(
                    "El número de documento no es válido. Debe contener entre 6 y 10 dígitos"
            );
        }

        empleado.setRut(
                Rut.formatear(empleado.getRut())
        );


        // =========================
        // FECHA DE NACIMIENTO
        // =========================

        if (empleado.getFechaNacimiento() == null) {

            throw new IllegalArgumentException(
                    "La fecha de nacimiento es obligatoria"
            );
        }

        int edad = Period.between(
                empleado.getFechaNacimiento(),
                LocalDate.now()
        ).getYears();

        if (edad < EDAD_MINIMA) {

            throw new IllegalArgumentException(
                    "El empleado debe ser mayor de " +
                            EDAD_MINIMA +
                            " años"
            );
        }

        if (edad > EDAD_MAXIMA) {

            throw new IllegalArgumentException(
                    "La edad no puede superar los " +
                            EDAD_MAXIMA +
                            " años"
            );
        }


        // =========================
        // DIRECCIÓN
        // =========================

        if (empleado.getDireccion() == null ||
                empleado.getDireccion().isBlank()) {

            throw new IllegalArgumentException(
                    "La dirección es obligatoria"
            );
        }

        empleado.setDireccion(
                normalizarTexto(empleado.getDireccion())
        );

        if (empleado.getDireccion().length() < 8) {

            throw new IllegalArgumentException(
                    "La dirección es demasiado corta"
            );
        }


        // =========================
        // TELÉFONO
        // =========================

        if (empleado.getTelefono() == null ||
                empleado.getTelefono().isBlank()) {

            throw new IllegalArgumentException(
                    "El teléfono es obligatorio"
            );
        }

        String telefono =
                empleado.getTelefono().trim();

        if (!TELEFONO_MOVIL.matcher(telefono).matches()) {

            throw new IllegalArgumentException(
                    "El teléfono debe ser un celular colombiano, por ejemplo 300 123 4567"
            );
        }

        empleado.setTelefono(
                normalizarTelefono(telefono)
        );


        // =========================
        // CORREO
        // =========================

        if (empleado.getCorreo() == null ||
                empleado.getCorreo().isBlank()) {

            throw new IllegalArgumentException(
                    "El correo es obligatorio"
            );
        }

        empleado.setCorreo(
                empleado.getCorreo()
                        .trim()
                        .toLowerCase(Locale.ROOT)
        );

        if (!CORREO.matcher(empleado.getCorreo()).matches()) {

            throw new IllegalArgumentException(
                    "El correo no tiene un formato válido"
            );
        }


        // =========================
        // CARGO
        // =========================

        if (empleado.getCargo() == null ||
                empleado.getCargo().isBlank()) {

            throw new IllegalArgumentException(
                    "El cargo o profesión es obligatorio"
            );
        }

        empleado.setCargo(
                normalizarTexto(empleado.getCargo())
        );

        if (empleado.getCargo().length() < 3) {

            throw new IllegalArgumentException(
                    "El cargo es demasiado corto"
            );
        }


        // =========================
        // JORNADA
        // =========================

        if (empleado.getJornada() == null ||
                empleado.getJornada().isBlank()) {

            throw new IllegalArgumentException(
                    "La jornada es obligatoria"
            );
        }

        if (!JORNADAS.contains(empleado.getJornada())) {

            throw new IllegalArgumentException(
                    "La jornada debe ser Completa, Parcial o Turnos"
            );
        }


        // =========================
        // SUELDO BRUTO
        // =========================

        if (empleado.getSueldoBruto() == null) {

            throw new IllegalArgumentException(
                    "El sueldo bruto es obligatorio"
            );
        }

        if (empleado.getSueldoBruto()
                .compareTo(SUELDO_MINIMO) < 0) {

            throw new IllegalArgumentException(
                    "El sueldo bruto mínimo es $400.000"
            );
        }

        if (empleado.getSueldoBruto()
                .compareTo(SUELDO_MAXIMO) > 0) {

            throw new IllegalArgumentException(
                    "El sueldo bruto supera el máximo permitido"
            );
        }

        if (empleado.getSueldoBruto()
                .remainder(BigDecimal.ONE)
                .compareTo(BigDecimal.ZERO) != 0) {

            throw new IllegalArgumentException(
                    "El sueldo bruto debe ser un monto entero en pesos"
            );
        }


        // =========================
        // DESCUENTOS
        // =========================

        if (empleado.getDescuentos() == null) {

            empleado.setDescuentos(
                    BigDecimal.ZERO
            );
        }

        if (empleado.getDescuentos()
                .compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "Los descuentos no pueden ser negativos"
            );
        }

        if (empleado.getDescuentos()
                .remainder(BigDecimal.ONE)
                .compareTo(BigDecimal.ZERO) != 0) {

            throw new IllegalArgumentException(
                    "Los descuentos deben ser un monto entero en pesos"
            );
        }

        if (empleado.getDescuentos()
                .compareTo(empleado.getSueldoBruto()) > 0) {

            throw new IllegalArgumentException(
                    "Los descuentos no pueden superar el sueldo bruto"
            );
        }

        BigDecimal topeDescuentos =
                empleado.getSueldoBruto()
                        .multiply(new BigDecimal("0.4"));

        if (empleado.getDescuentos()
                .compareTo(topeDescuentos) > 0) {

            throw new IllegalArgumentException(
                    "Los descuentos no pueden superar el 40% del sueldo bruto"
            );
        }
    }


    // =========================
    // CONFIRMAR ELIMINACIÓN
    // =========================

    public static void validarConfirmacionEliminacion(
            String rutRegistrado,
            String rutConfirmado) {

        if (rutConfirmado == null ||
                rutConfirmado.isBlank()) {

            throw new IllegalArgumentException(
                    "Para eliminar debes confirmar el número de documento del empleado"
            );
        }

        if (!Rut.normalizar(rutRegistrado)
                .equals(Rut.normalizar(rutConfirmado))) {

            throw new IllegalArgumentException(
                    "El número de documento de confirmación no coincide con el del empleado"
            );
        }
    }


    // =========================
    // JORNADAS
    // =========================

    public static List<String> jornadasPermitidas() {
        return List.copyOf(JORNADAS);
    }


    // =========================
    // NORMALIZAR TEXTO
    // =========================

    private static String normalizarTexto(String valor) {

        return valor
                .trim()
                .replaceAll("\\s+", " ");
    }


    // =========================
    // NORMALIZAR TELÉFONO
    // =========================

    private static String normalizarTelefono(String telefono) {

        String digitos =
                telefono.replaceAll("\\D", "");

        if (digitos.startsWith("57")) {
            digitos = digitos.substring(2);
        }

        return "+57 " +
                digitos.substring(0, 3) +
                " " +
                digitos.substring(3);
    }
}