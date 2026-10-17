package com.proyecto.backend.controller;

import com.proyecto.backend.model.Liquidacion;
import com.proyecto.backend.service.LiquidacionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/liquidaciones")
@CrossOrigin(origins = "http://localhost:5173")
public class LiquidacionController {

    private final LiquidacionService liquidacionService;

    public LiquidacionController(LiquidacionService liquidacionService) {
        this.liquidacionService = liquidacionService;
    }

    @GetMapping
    public List<Liquidacion> listar() {
        return liquidacionService.listar();
    }

    @PostMapping
    public Liquidacion calcular(@RequestBody Map<String, Object> cuerpo) {
        Long empleadoId = Long.valueOf(cuerpo.get("empleadoId").toString());
        String periodo = cuerpo.get("periodo").toString();
        BigDecimal bonificaciones = cuerpo.get("bonificaciones") == null || cuerpo.get("bonificaciones").toString().isBlank()
                ? BigDecimal.ZERO
                : new BigDecimal(cuerpo.get("bonificaciones").toString());
        return liquidacionService.calcular(empleadoId, periodo, bonificaciones);
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> pdf(@PathVariable Long id) {
        byte[] documento = liquidacionService.obtenerPdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=liquidacion-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(documento);
    }
}
