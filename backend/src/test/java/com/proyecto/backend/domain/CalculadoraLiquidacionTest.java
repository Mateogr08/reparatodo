package com.proyecto.backend.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculadoraLiquidacionTest {

    @Test
    void calculaProporcionalConDescuentosLegales() {
        CalculadoraLiquidacion.Resultado resultado = CalculadoraLiquidacion.calcular(
                new CalculadoraLiquidacion.Entrada(
                        new BigDecimal("3000000"),
                        15,
                        30,
                        new BigDecimal("100000"),
                        new BigDecimal("60000")
                )
        );

        assertEquals(new BigDecimal("1500000"), resultado.sueldoProporcional());
        assertEquals(new BigDecimal("100000"), resultado.bonificaciones());
        assertEquals(new BigDecimal("64000"), resultado.afp());
        assertEquals(new BigDecimal("64000"), resultado.isapre());
        assertEquals(new BigDecimal("30000"), resultado.otrosDescuentos());
        assertEquals(new BigDecimal("158000"), resultado.totalDescuentos());
        assertEquals(new BigDecimal("1442000"), resultado.sueldoLiquido());
    }
}
