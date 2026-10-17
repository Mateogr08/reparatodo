package com.proyecto.backend.repository;

import com.proyecto.backend.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByLiquidacionId(Long liquidacionId);

    List<Pago> findAllByOrderByFechaEmisionDesc();
}
