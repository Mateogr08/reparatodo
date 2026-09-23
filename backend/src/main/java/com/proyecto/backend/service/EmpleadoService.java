package com.proyecto.backend.service;

import com.proyecto.backend.model.Empleado;
import com.proyecto.backend.repository.EmpleadoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;

    public EmpleadoService(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    public Empleado registrarEmpleado(Empleado empleado) {

        if (empleadoRepository.existsByRut(empleado.getRut())) {
            throw new RuntimeException("Ya existe un empleado con el RUT ingresado");
        }

        return empleadoRepository.save(empleado);
    }

    public List<Empleado> listarEmpleados() {
        return empleadoRepository.findAll();
    }

    public Optional<Empleado> buscarPorId(Long id){
        return empleadoRepository.findById(id);
    }
}
