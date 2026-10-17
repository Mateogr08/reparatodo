package com.proyecto.backend.controller;

import com.proyecto.backend.model.Asistencia;
import com.proyecto.backend.service.AsistenciaService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/asistencias")
@CrossOrigin(origins = "http://localhost:5173")
public class AsistenciaController {

    private final AsistenciaService asistenciaService;

    public AsistenciaController(AsistenciaService asistenciaService) {
        this.asistenciaService = asistenciaService;
    }

    @GetMapping
    public List<Asistencia> listar(
            @RequestParam(required = false) Long empleadoId,
            @RequestParam(required = false) LocalDate desde,
            @RequestParam(required = false) LocalDate hasta
    ) {
        return asistenciaService.listar(empleadoId, desde, hasta);
    }

    @PostMapping
    public Asistencia registrar(@RequestBody Map<String, Object> cuerpo) {
        Long empleadoId = Long.valueOf(cuerpo.get("empleadoId").toString());
        LocalDate fecha = LocalDate.parse(cuerpo.get("fecha").toString());
        boolean presente = Boolean.parseBoolean(cuerpo.getOrDefault("presente", true).toString());
        String observacion = cuerpo.get("observacion") == null ? null : cuerpo.get("observacion").toString();
        return asistenciaService.registrar(empleadoId, fecha, presente, observacion);
    }
}
