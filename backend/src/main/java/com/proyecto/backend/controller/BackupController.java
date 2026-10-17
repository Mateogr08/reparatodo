package com.proyecto.backend.controller;

import com.proyecto.backend.model.CopiaSeguridad;
import com.proyecto.backend.service.BackupService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/backups")
@CrossOrigin(origins = "http://localhost:5173")
public class BackupController {

    private final BackupService backupService;

    public BackupController(BackupService backupService) {
        this.backupService = backupService;
    }

    @GetMapping
    public List<CopiaSeguridad> listar() {
        return backupService.listar();
    }

    @PostMapping
    public CopiaSeguridad crear() {
        return backupService.crearManual();
    }
}
