package com.proyecto.backend.repository;

import com.proyecto.backend.model.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

    Optional<Empleado> findByCorreoIgnoreCase(String correo);

    boolean existsByRut(String rut);

    boolean existsByRutAndIdNot(String rut, Long id);

    boolean existsByCorreoIgnoreCase(String correo);

    boolean existsByCorreoIgnoreCaseAndIdNot(String correo, Long id);
}
