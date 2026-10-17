package com.proyecto.backend.service;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.proyecto.backend.model.Asistencia;
import com.proyecto.backend.model.Liquidacion;
import com.proyecto.backend.model.Rol;
import com.proyecto.backend.repository.AsistenciaRepository;
import com.proyecto.backend.repository.LiquidacionRepository;
import com.proyecto.backend.security.SesionActual;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class ReporteService {

    private final AsistenciaRepository asistenciaRepository;
    private final LiquidacionRepository liquidacionRepository;

    public ReporteService(AsistenciaRepository asistenciaRepository, LiquidacionRepository liquidacionRepository) {
        this.asistenciaRepository = asistenciaRepository;
        this.liquidacionRepository = liquidacionRepository;
    }

    public Map<String, Object> consolidado(String tipo, LocalDate desde, LocalDate hasta) {
        SesionActual.exigirRol(Rol.ADMINISTRADOR, Rol.ENCARGADO_PERSONAL);
        validarRango(desde, hasta);

        return switch (tipo == null ? "" : tipo.toUpperCase()) {
            case "ASISTENCIA" -> reporteAsistencia(desde, hasta);
            case "NOMINA" -> reporteNomina(desde, hasta);
            case "DESCUENTOS" -> reporteDescuentos(desde, hasta);
            default -> throw new IllegalArgumentException("El tipo de reporte debe ser ASISTENCIA, NOMINA o DESCUENTOS");
        };
    }

    public byte[] exportarPdf(String tipo, LocalDate desde, LocalDate hasta) {
        Map<String, Object> datos = consolidado(tipo, desde, hasta);
        try {
            ByteArrayOutputStream salida = new ByteArrayOutputStream();
            Document documento = new Document();
            PdfWriter.getInstance(documento, salida);
            documento.open();
            documento.add(new Paragraph("REPARATODO — Reporte " + tipo));
            documento.add(new Paragraph("Desde " + desde + " hasta " + hasta));
            documento.add(new Paragraph(" "));

            PdfPTable tabla = new PdfPTable(2);
            tabla.setWidthPercentage(100);
            datos.forEach((clave, valor) -> {
                if (!"detalle".equals(clave)) {
                    tabla.addCell(clave);
                    tabla.addCell(String.valueOf(valor));
                }
            });
            documento.add(tabla);
            documento.close();
            return salida.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo exportar el reporte en PDF");
        }
    }

    private Map<String, Object> reporteAsistencia(LocalDate desde, LocalDate hasta) {
        List<Asistencia> registros = asistenciaRepository.findByFechaBetween(desde, hasta);
        long presentes = registros.stream().filter(Asistencia::isPresente).count();
        long ausentes = registros.size() - presentes;
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("tipo", "ASISTENCIA");
        resultado.put("totalRegistros", registros.size());
        resultado.put("presentes", presentes);
        resultado.put("ausentes", ausentes);
        resultado.put("detalle", registros);
        return resultado;
    }

    private Map<String, Object> reporteNomina(LocalDate desde, LocalDate hasta) {
        List<Liquidacion> liquidaciones = liquidacionesEnRango(desde, hasta);
        BigDecimal bruto = sumar(liquidaciones, Liquidacion::getSueldoBruto);
        BigDecimal bonos = sumar(liquidaciones, Liquidacion::getBonificaciones);
        BigDecimal descuentos = sumar(liquidaciones, Liquidacion::getTotalDescuentos);
        BigDecimal liquido = sumar(liquidaciones, Liquidacion::getSueldoLiquido);
        NumberFormat moneda = NumberFormat.getCurrencyInstance(Locale.of("es", "CO"));
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("tipo", "NOMINA");
        resultado.put("liquidaciones", liquidaciones.size());
        resultado.put("totalSueldosBrutos", moneda.format(bruto));
        resultado.put("totalBonificaciones", moneda.format(bonos));
        resultado.put("totalDescuentos", moneda.format(descuentos));
        resultado.put("totalSueldoLiquido", moneda.format(liquido));
        resultado.put("detalle", liquidaciones);
        return resultado;
    }

    private Map<String, Object> reporteDescuentos(LocalDate desde, LocalDate hasta) {
        List<Liquidacion> liquidaciones = liquidacionesEnRango(desde, hasta);
        NumberFormat moneda = NumberFormat.getCurrencyInstance(Locale.of("es", "CO"));
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("tipo", "DESCUENTOS");
        resultado.put("totalAfp", moneda.format(sumar(liquidaciones, Liquidacion::getAfp)));
        resultado.put("totalIsapre", moneda.format(sumar(liquidaciones, Liquidacion::getIsapre)));
        resultado.put("totalImpuestos", moneda.format(sumar(liquidaciones, Liquidacion::getImpuestos)));
        resultado.put("totalOtros", moneda.format(sumar(liquidaciones, Liquidacion::getOtrosDescuentos)));
        resultado.put("totalDescuentos", moneda.format(sumar(liquidaciones, Liquidacion::getTotalDescuentos)));
        resultado.put("detalle", liquidaciones);
        return resultado;
    }

    private List<Liquidacion> liquidacionesEnRango(LocalDate desde, LocalDate hasta) {
        LocalDateTime inicio = desde.atStartOfDay();
        LocalDateTime fin = hasta.plusDays(1).atStartOfDay();
        return liquidacionRepository.findByFechaCalculoBetween(inicio, fin);
    }

    private BigDecimal sumar(List<Liquidacion> liquidaciones, java.util.function.Function<Liquidacion, BigDecimal> getter) {
        return liquidaciones.stream()
                .map(getter)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void validarRango(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null) {
            throw new IllegalArgumentException("Debe indicar un rango de fechas");
        }
        if (hasta.isBefore(desde)) {
            throw new IllegalArgumentException("La fecha final no puede ser anterior a la inicial");
        }
    }
}
