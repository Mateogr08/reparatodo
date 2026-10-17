package com.proyecto.backend.service;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.proyecto.backend.model.Liquidacion;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class PdfLiquidacionService {

    public byte[] generar(Liquidacion liquidacion) {
        try {
            ByteArrayOutputStream salida = new ByteArrayOutputStream();
            Document documento = new Document();
            PdfWriter.getInstance(documento, salida);
            documento.open();

            NumberFormat moneda = NumberFormat.getCurrencyInstance(Locale.of("es", "CO"));
            DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            documento.add(new Paragraph("REPARATODO — Liquidación de sueldo"));
            documento.add(new Paragraph(" "));
            documento.add(new Paragraph("Empleado: " + liquidacion.getEmpleado().getNombre()));
            documento.add(new Paragraph("RUT: " + liquidacion.getEmpleado().getRut()));
            documento.add(new Paragraph("Cargo: " + nulo(liquidacion.getEmpleado().getCargo())));
            documento.add(new Paragraph("Periodo: " + liquidacion.getPeriodo()));
            documento.add(new Paragraph("Días trabajados: " + liquidacion.getDiasTrabajados()));
            documento.add(new Paragraph("Fecha de cálculo: " + liquidacion.getFechaCalculo().format(formatoFecha)));
            documento.add(new Paragraph(" "));

            PdfPTable tabla = new PdfPTable(2);
            tabla.setWidthPercentage(100);
            agregar(tabla, "Sueldo bruto proporcional", moneda.format(liquidacion.getSueldoBruto()));
            agregar(tabla, "Bonificaciones", moneda.format(liquidacion.getBonificaciones()));
            agregar(tabla, "AFP (pensión 4%)", moneda.format(liquidacion.getAfp()));
            agregar(tabla, "ISAPRE / EPS (salud 4%)", moneda.format(liquidacion.getIsapre()));
            agregar(tabla, "Impuestos", moneda.format(liquidacion.getImpuestos()));
            agregar(tabla, "Otros descuentos", moneda.format(liquidacion.getOtrosDescuentos()));
            agregar(tabla, "Total descuentos", moneda.format(liquidacion.getTotalDescuentos()));
            agregar(tabla, "Sueldo líquido", moneda.format(liquidacion.getSueldoLiquido()));
            documento.add(tabla);

            documento.close();
            return salida.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo generar el PDF de liquidación");
        }
    }

    private void agregar(PdfPTable tabla, String concepto, String valor) {
        tabla.addCell(concepto);
        tabla.addCell(valor);
    }

    private String nulo(String valor) {
        return valor == null || valor.isBlank() ? "—" : valor;
    }
}
