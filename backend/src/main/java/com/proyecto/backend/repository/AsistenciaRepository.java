package com.proyecto.backend.repository;

import com.proyecto.backend.model.Asistencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {

    Optional<Asistencia> findByEmpleadoIdAndFecha(Long empleadoId, LocalDate fecha);

    List<Asistencia> findByEmpleadoIdAndFechaBetween(Long empleadoId, LocalDate desde, LocalDate hasta);

    List<Asistencia> findByFechaBetween(LocalDate desde, LocalDate hasta);

    long countByEmpleadoIdAndFechaBetweenAndPresenteTrue(Long empleadoId, LocalDate desde, LocalDate hasta);
}
