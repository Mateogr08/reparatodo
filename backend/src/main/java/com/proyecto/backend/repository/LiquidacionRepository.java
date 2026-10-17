package com.proyecto.backend.repository;

import com.proyecto.backend.model.Liquidacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LiquidacionRepository extends JpaRepository<Liquidacion, Long> {

    Optional<Liquidacion> findByEmpleadoIdAndPeriodo(Long empleadoId, String periodo);

    List<Liquidacion> findByEmpleadoIdOrderByPeriodoDesc(Long empleadoId);

    List<Liquidacion> findByFechaCalculoBetween(LocalDateTime desde, LocalDateTime hasta);

    List<Liquidacion> findByPeriodoBetween(String desde, String hasta);
}
