package com.proyecto.backend.controller;

import com.proyecto.backend.service.ReporteService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "http://localhost:5173")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping
    public Map<String, Object> consultar(
            @RequestParam String tipo,
            @RequestParam LocalDate desde,
            @RequestParam LocalDate hasta
    ) {
        return reporteService.consolidado(tipo, desde, hasta);
    }

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> exportarPdf(
            @RequestParam String tipo,
            @RequestParam LocalDate desde,
            @RequestParam LocalDate hasta
    ) {
        byte[] documento = reporteService.exportarPdf(tipo, desde, hasta);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte-" + tipo.toLowerCase() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(documento);
    }
}
