package com.proyecto.backend.service;

import com.proyecto.backend.domain.CalculadoraLiquidacion;
import com.proyecto.backend.model.Empleado;
import com.proyecto.backend.model.Liquidacion;
import com.proyecto.backend.model.Rol;
import com.proyecto.backend.model.Usuario;
import com.proyecto.backend.repository.AsistenciaRepository;
import com.proyecto.backend.repository.EmpleadoRepository;
import com.proyecto.backend.repository.LiquidacionRepository;
import com.proyecto.backend.security.SesionActual;
import com.proyecto.backend.security.VinculoEmpleado;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class LiquidacionService {

    private final LiquidacionRepository liquidacionRepository;
    private final EmpleadoRepository empleadoRepository;
    private final AsistenciaRepository asistenciaRepository;
    private final PdfLiquidacionService pdfLiquidacionService;

    public LiquidacionService(
            LiquidacionRepository liquidacionRepository,
            EmpleadoRepository empleadoRepository,
            AsistenciaRepository asistenciaRepository,
            PdfLiquidacionService pdfLiquidacionService
    ) {
        this.liquidacionRepository = liquidacionRepository;
        this.empleadoRepository = empleadoRepository;
        this.asistenciaRepository = asistenciaRepository;
        this.pdfLiquidacionService = pdfLiquidacionService;
    }

    public Liquidacion calcular(Long empleadoId, String periodo, BigDecimal bonificaciones) {
        SesionActual.exigirRol(Rol.ADMINISTRADOR, Rol.ENCARGADO_PERSONAL);
        YearMonth mes = parsearPeriodo(periodo);
        Empleado empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new NoSuchElementException("Empleado no encontrado"));

        LocalDate desde = mes.atDay(1);
        LocalDate hasta = mes.atEndOfMonth();
        int diasMes = mes.lengthOfMonth();
        int diasTrabajados = (int) asistenciaRepository.countByEmpleadoIdAndFechaBetweenAndPresenteTrue(
                empleadoId, desde, hasta
        );
        if (diasTrabajados <= 0) {
            throw new IllegalArgumentException("No hay asistencia registrada en el periodo " + periodo);
        }

        CalculadoraLiquidacion.Resultado montos = CalculadoraLiquidacion.calcular(
                new CalculadoraLiquidacion.Entrada(
                        empleado.getSueldoBruto(),
                        diasTrabajados,
                        diasMes,
                        bonificaciones,
                        empleado.getDescuentos()
                )
        );

        Liquidacion liquidacion = liquidacionRepository.findByEmpleadoIdAndPeriodo(empleadoId, periodo)
                .orElseGet(Liquidacion::new);
        if ("PAGADA".equals(liquidacion.getEstado())) {
            throw new IllegalArgumentException("La liquidación del periodo ya fue pagada y no se puede recalcular");
        }

        liquidacion.setEmpleado(empleado);
        liquidacion.setPeriodo(periodo);
        liquidacion.setDiasTrabajados(diasTrabajados);
        liquidacion.setSueldoBruto(montos.sueldoProporcional());
        liquidacion.setBonificaciones(montos.bonificaciones());
        liquidacion.setAfp(montos.afp());
        liquidacion.setIsapre(montos.isapre());
        liquidacion.setImpuestos(montos.impuestos());
        liquidacion.setOtrosDescuentos(montos.otrosDescuentos());
        liquidacion.setTotalDescuentos(montos.totalDescuentos());
        liquidacion.setSueldoLiquido(montos.sueldoLiquido());
        liquidacion.setFechaCalculo(LocalDateTime.now());
        liquidacion.setEstado("CALCULADA");
        liquidacion.setDocumentoPdf(null);
        Liquidacion guardada = liquidacionRepository.save(liquidacion);
        guardada.setDocumentoPdf(pdfLiquidacionService.generar(guardada));
        return liquidacionRepository.save(guardada);
    }

    public List<Liquidacion> listar() {
        Usuario actual = SesionActual.get();
        if (actual == null) {
            throw new SecurityException("Debe iniciar sesión");
        }
        if (actual.getRol() == Rol.EMPLEADO) {
            Empleado propio = VinculoEmpleado.exigirFichaVinculada(empleadoRepository);
            return liquidacionRepository.findByEmpleadoIdOrderByPeriodoDesc(propio.getId());
        }
        SesionActual.exigirRol(Rol.ADMINISTRADOR, Rol.ENCARGADO_PERSONAL);
        return liquidacionRepository.findAll();
    }

    public Liquidacion buscar(Long id) {
        Liquidacion liquidacion = liquidacionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Liquidación no encontrada"));
        controlarAcceso(liquidacion);
        return liquidacion;
    }

    public byte[] obtenerPdf(Long id) {
        Liquidacion liquidacion = buscar(id);
        if (liquidacion.getDocumentoPdf() == null || liquidacion.getDocumentoPdf().length == 0) {
            liquidacion.setDocumentoPdf(pdfLiquidacionService.generar(liquidacion));
            liquidacionRepository.save(liquidacion);
        }
        return liquidacion.getDocumentoPdf();
    }

    private void controlarAcceso(Liquidacion liquidacion) {
        VinculoEmpleado.exigirAccesoFicha(liquidacion.getEmpleado());
    }

    private YearMonth parsearPeriodo(String periodo) {
        if (periodo == null || !periodo.matches("\\d{4}-\\d{2}")) {
            throw new IllegalArgumentException("El periodo debe tener el formato AAAA-MM");
        }
        return YearMonth.parse(periodo);
    }

}
