package com.proyecto.backend.service;

import com.proyecto.backend.model.Empleado;
import com.proyecto.backend.repository.EmpleadoRepository;
import com.proyecto.backend.validation.ValidadorEmpleado;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;

    public EmpleadoService(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    public Empleado registrarEmpleado(Empleado empleado) {
        ValidadorEmpleado.validar(empleado);

        if (empleadoRepository.existsByRut(empleado.getRut())) {
            throw new IllegalArgumentException("Ya existe un empleado con el RUT ingresado");
        }
        if (empleadoRepository.existsByCorreoIgnoreCase(empleado.getCorreo())) {
            throw new IllegalArgumentException("Ya existe un empleado con el correo ingresado");
        }

        return empleadoRepository.save(empleado);
    }

    public Empleado actualizarEmpleado(Long id, Empleado datos) {
        Empleado existente = empleadoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Empleado no encontrado"));

        ValidadorEmpleado.validar(datos);

        if (empleadoRepository.existsByRutAndIdNot(datos.getRut(), id)) {
            throw new IllegalArgumentException("Ya existe un empleado con el RUT ingresado");
        }
        if (empleadoRepository.existsByCorreoIgnoreCaseAndIdNot(datos.getCorreo(), id)) {
            throw new IllegalArgumentException("Ya existe un empleado con el correo ingresado");
        }

        existente.setRut(datos.getRut());
        existente.setNombre(datos.getNombre());
        existente.setFechaNacimiento(datos.getFechaNacimiento());
        existente.setDireccion(datos.getDireccion());
        existente.setTelefono(datos.getTelefono());
        existente.setCorreo(datos.getCorreo());
        existente.setCargo(datos.getCargo());
        existente.setJornada(datos.getJornada());
        existente.setSueldoBruto(datos.getSueldoBruto());
        existente.setDescuentos(datos.getDescuentos());

        return empleadoRepository.save(existente);
    }

    public void eliminarEmpleado(Long id, String rutConfirmado) {
        Empleado existente = empleadoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Empleado no encontrado"));
        ValidadorEmpleado.validarConfirmacionEliminacion(existente.getRut(), rutConfirmado);
        empleadoRepository.deleteById(id);
    }

    public List<Empleado> listarEmpleados() {
        return empleadoRepository.findAll();
    }

    public Optional<Empleado> buscarPorId(Long id) {
        return empleadoRepository.findById(id);
    }
}
