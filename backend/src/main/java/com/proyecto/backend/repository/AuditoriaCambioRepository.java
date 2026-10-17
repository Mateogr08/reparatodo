package com.proyecto.backend.repository;

import com.proyecto.backend.model.AuditoriaCambio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditoriaCambioRepository extends JpaRepository<AuditoriaCambio, Long> {

    List<AuditoriaCambio> findByEmpleadoIdOrderByFechaHoraDesc(Long empleadoId);
}
