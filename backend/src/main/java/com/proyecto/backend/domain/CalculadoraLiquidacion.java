package com.proyecto.backend.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Reglas de cálculo de liquidación (proporción por días, AFP, EPS, impuestos, líquido).
 * Sin acceso a persistencia ni seguridad.
 */
public final class CalculadoraLiquidacion {

    private static final BigDecimal TASA_AFP = new BigDecimal("0.04");
    private static final BigDecimal TASA_ISAPRE = new BigDecimal("0.04");
    private static final BigDecimal TASA_IMPUESTO = new BigDecimal("0.05");
    private static final BigDecimal TOPE_EXENTO = new BigDecimal("7003620");

    private CalculadoraLiquidacion() {
    }

    public record Entrada(
            BigDecimal sueldoContrato,
            int diasTrabajados,
            int diasMes,
            BigDecimal bonificaciones,
            BigDecimal descuentosFijosEmpleado
    ) {
    }

    public record Resultado(
            BigDecimal sueldoProporcional,
            BigDecimal bonificaciones,
            BigDecimal afp,
            BigDecimal isapre,
            BigDecimal impuestos,
            BigDecimal otrosDescuentos,
            BigDecimal totalDescuentos,
            BigDecimal sueldoLiquido
    ) {
    }

    public static Resultado calcular(Entrada entrada) {
        BigDecimal sueldoProporcional = nvl(entrada.sueldoContrato())
                .multiply(BigDecimal.valueOf(entrada.diasTrabajados()))
                .divide(BigDecimal.valueOf(entrada.diasMes()), 0, RoundingMode.HALF_UP);
        BigDecimal bonos = nvl(entrada.bonificaciones());
        BigDecimal base = sueldoProporcional.add(bonos);
        BigDecimal afp = porcentaje(base, TASA_AFP);
        BigDecimal isapre = porcentaje(base, TASA_ISAPRE);
        BigDecimal impuestos = BigDecimal.ZERO;
        if (base.compareTo(TOPE_EXENTO) > 0) {
            impuestos = porcentaje(base.subtract(TOPE_EXENTO), TASA_IMPUESTO);
        }
        BigDecimal otros = nvl(entrada.descuentosFijosEmpleado())
                .multiply(BigDecimal.valueOf(entrada.diasTrabajados()))
                .divide(BigDecimal.valueOf(entrada.diasMes()), 0, RoundingMode.HALF_UP);
        BigDecimal totalDescuentos = afp.add(isapre).add(impuestos).add(otros);
        BigDecimal liquido = base.subtract(totalDescuentos);
        if (liquido.compareTo(BigDecimal.ZERO) < 0) {
            liquido = BigDecimal.ZERO;
        }
        return new Resultado(
                sueldoProporcional,
                bonos,
                afp,
                isapre,
                impuestos,
                otros,
                totalDescuentos,
                liquido
        );
    }

    private static BigDecimal nvl(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor;
    }

    private static BigDecimal porcentaje(BigDecimal base, BigDecimal tasa) {
        return base.multiply(tasa).setScale(0, RoundingMode.HALF_UP);
    }
}
