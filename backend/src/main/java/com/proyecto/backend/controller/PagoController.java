package com.proyecto.backend.controller;

import com.proyecto.backend.model.Pago;
import com.proyecto.backend.service.PagoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
@CrossOrigin(origins = "http://localhost:5173")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @GetMapping
    public List<Pago> listar() {
        return pagoService.listar();
    }

    @PostMapping
    public Pago emitir(@RequestBody Map<String, Object> cuerpo) {
        Long liquidacionId = Long.valueOf(cuerpo.get("liquidacionId").toString());
        String medio = cuerpo.get("medio").toString();
        String referencia = cuerpo.get("referencia") == null ? "" : cuerpo.get("referencia").toString();
        return pagoService.emitir(liquidacionId, medio, referencia);
    }
}
