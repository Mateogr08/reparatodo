package com.proyecto.backend.repository;

import com.proyecto.backend.model.CopiaSeguridad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CopiaSeguridadRepository extends JpaRepository<CopiaSeguridad, Long> {

    List<CopiaSeguridad> findAllByOrderByFechaHoraDesc();
}
