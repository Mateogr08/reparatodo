package com.proyecto.backend.service;

import com.proyecto.backend.model.Asistencia;
import com.proyecto.backend.model.Empleado;
import com.proyecto.backend.model.Rol;
import com.proyecto.backend.repository.AsistenciaRepository;
import com.proyecto.backend.repository.EmpleadoRepository;
import com.proyecto.backend.security.SesionActual;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AsistenciaService {

    private final AsistenciaRepository asistenciaRepository;
    private final EmpleadoRepository empleadoRepository;

    public AsistenciaService(AsistenciaRepository asistenciaRepository, EmpleadoRepository empleadoRepository) {
        this.asistenciaRepository = asistenciaRepository;
        this.empleadoRepository = empleadoRepository;
    }

    public Asistencia registrar(Long empleadoId, LocalDate fecha, boolean presente, String observacion) {
        SesionActual.exigirRol(Rol.ADMINISTRADOR, Rol.ENCARGADO_PERSONAL);
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha de asistencia es obligatoria");
        }
        if (fecha.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("No se puede registrar asistencia futura");
        }

        Empleado empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new NoSuchElementException("Empleado no encontrado"));

        Asistencia asistencia = asistenciaRepository.findByEmpleadoIdAndFecha(empleadoId, fecha)
                .orElseGet(Asistencia::new);
        asistencia.setEmpleado(empleado);
        asistencia.setFecha(fecha);
        asistencia.setPresente(presente);
        asistencia.setObservacion(observacion);
        return asistenciaRepository.save(asistencia);
    }

    public List<Asistencia> listar(Long empleadoId, LocalDate desde, LocalDate hasta) {
        SesionActual.exigirRol(Rol.ADMINISTRADOR, Rol.ENCARGADO_PERSONAL, Rol.EMPLEADO, Rol.USUARIO);
        if (empleadoId != null && desde != null && hasta != null) {
            return asistenciaRepository.findByEmpleadoIdAndFechaBetween(empleadoId, desde, hasta);
        }
        if (desde != null && hasta != null) {
            return asistenciaRepository.findByFechaBetween(desde, hasta);
        }
        return asistenciaRepository.findAll();
    }
}
