package com.proyecto.backend.repository;

import com.proyecto.backend.model.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Al extender de JpaRepository, JPA ya proporciona los métodos básicos
 */
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

    boolean existsByRut(String rut);

}
