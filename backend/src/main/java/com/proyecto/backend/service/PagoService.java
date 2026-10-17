package com.proyecto.backend.service;

import com.proyecto.backend.model.Liquidacion;
import com.proyecto.backend.model.Pago;
import com.proyecto.backend.model.Rol;
import com.proyecto.backend.repository.LiquidacionRepository;
import com.proyecto.backend.repository.PagoRepository;
import com.proyecto.backend.security.SesionActual;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
public class PagoService {

    private static final Set<String> MEDIOS = Set.of("CHEQUE", "DEPOSITO");

    private final PagoRepository pagoRepository;
    private final LiquidacionRepository liquidacionRepository;

    public PagoService(PagoRepository pagoRepository, LiquidacionRepository liquidacionRepository) {
        this.pagoRepository = pagoRepository;
        this.liquidacionRepository = liquidacionRepository;
    }

    public Pago emitir(Long liquidacionId, String medio, String referencia) {
        SesionActual.exigirRol(Rol.ADMINISTRADOR, Rol.ENCARGADO_PERSONAL);
        Liquidacion liquidacion = liquidacionRepository.findById(liquidacionId)
                .orElseThrow(() -> new NoSuchElementException("Liquidación no encontrada"));

        if (!"CALCULADA".equals(liquidacion.getEstado())) {
            throw new IllegalArgumentException("Solo se pueden pagar liquidaciones calculadas pendientes");
        }
        if (medio == null || !MEDIOS.contains(medio.toUpperCase())) {
            throw new IllegalArgumentException("El medio de pago debe ser CHEQUE o DEPOSITO");
        }
        if (referencia == null || referencia.isBlank()) {
            throw new IllegalArgumentException("Debe indicar el número de cheque o la cuenta de depósito");
        }
        if ("DEPOSITO".equalsIgnoreCase(medio)
                && (liquidacion.getEmpleado().getNumeroCuenta() == null
                || liquidacion.getEmpleado().getNumeroCuenta().isBlank())) {
            throw new IllegalArgumentException("El empleado no tiene cuenta bancaria registrada");
        }

        Pago pago = new Pago();
        pago.setLiquidacion(liquidacion);
        pago.setMedio(medio.toUpperCase());
        pago.setValor(liquidacion.getSueldoLiquido());
        pago.setReferencia(referencia.trim());
        pago.setFechaEmision(LocalDateTime.now());
        pago.setResultado("EMITIDO");

        liquidacion.setEstado("PAGADA");
        liquidacionRepository.save(liquidacion);
        return pagoRepository.save(pago);
    }

    public List<Pago> listar() {
        SesionActual.exigirRol(Rol.ADMINISTRADOR, Rol.ENCARGADO_PERSONAL);
        return pagoRepository.findAllByOrderByFechaEmisionDesc();
    }
}
