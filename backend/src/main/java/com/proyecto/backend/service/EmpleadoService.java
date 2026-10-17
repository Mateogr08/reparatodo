package com.proyecto.backend.service;

import com.proyecto.backend.model.AuditoriaCambio;
import com.proyecto.backend.model.Empleado;
import com.proyecto.backend.model.Rol;
import com.proyecto.backend.repository.AuditoriaCambioRepository;
import com.proyecto.backend.repository.EmpleadoRepository;
import com.proyecto.backend.security.SesionActual;
import com.proyecto.backend.validation.ValidadorEmpleado;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Service
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final AuditoriaCambioRepository auditoriaCambioRepository;

    public EmpleadoService(EmpleadoRepository empleadoRepository, AuditoriaCambioRepository auditoriaCambioRepository) {
        this.empleadoRepository = empleadoRepository;
        this.auditoriaCambioRepository = auditoriaCambioRepository;
    }

    public Empleado registrarEmpleado(Empleado empleado) {
        exigirEscritura();
        ValidadorEmpleado.validar(empleado);

        if (empleadoRepository.existsByRut(empleado.getRut())) {
            throw new IllegalArgumentException("Ya existe un empleado con el RUT ingresado");
        }
        if (empleadoRepository.existsByCorreoIgnoreCase(empleado.getCorreo())) {
            throw new IllegalArgumentException("Ya existe un empleado con el correo ingresado");
        }

        empleado.setFechaRegistro(LocalDateTime.now());
        return empleadoRepository.save(empleado);
    }

    @Transactional
    public Empleado actualizarEmpleado(Long id, Empleado datos) {
        exigirEscritura();
        Empleado existente = empleadoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Empleado no encontrado"));

        ValidadorEmpleado.validar(datos);

        if (empleadoRepository.existsByRutAndIdNot(datos.getRut(), id)) {
            throw new IllegalArgumentException("Ya existe un empleado con el RUT ingresado");
        }
        if (empleadoRepository.existsByCorreoIgnoreCaseAndIdNot(datos.getCorreo(), id)) {
            throw new IllegalArgumentException("Ya existe un empleado con el correo ingresado");
        }

        registrarCambio(existente, "rut", existente.getRut(), datos.getRut());
        registrarCambio(existente, "nombre", existente.getNombre(), datos.getNombre());
        registrarCambio(existente, "fechaNacimiento", String.valueOf(existente.getFechaNacimiento()), String.valueOf(datos.getFechaNacimiento()));
        registrarCambio(existente, "direccion", existente.getDireccion(), datos.getDireccion());
        registrarCambio(existente, "telefono", existente.getTelefono(), datos.getTelefono());
        registrarCambio(existente, "correo", existente.getCorreo(), datos.getCorreo());
        registrarCambio(existente, "cargo", existente.getCargo(), datos.getCargo());
        registrarCambio(existente, "jornada", existente.getJornada(), datos.getJornada());
        registrarCambio(existente, "sueldoBruto", String.valueOf(existente.getSueldoBruto()), String.valueOf(datos.getSueldoBruto()));
        registrarCambio(existente, "descuentos", String.valueOf(existente.getDescuentos()), String.valueOf(datos.getDescuentos()));
        registrarCambio(existente, "banco", existente.getBanco(), datos.getBanco());
        registrarCambio(existente, "numeroCuenta", existente.getNumeroCuenta(), datos.getNumeroCuenta());

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
        existente.setBanco(datos.getBanco());
        existente.setNumeroCuenta(datos.getNumeroCuenta());

        return empleadoRepository.save(existente);
    }

    public void eliminarEmpleado(Long id, String rutConfirmado) {
        exigirEscritura();
        Empleado existente = empleadoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Empleado no encontrado"));
        ValidadorEmpleado.validarConfirmacionEliminacion(existente.getRut(), rutConfirmado);
        empleadoRepository.deleteById(id);
    }

    public List<Empleado> listarEmpleados() {
        exigirLectura();
        return empleadoRepository.findAll();
    }

    public Optional<Empleado> buscarPorId(Long id) {
        exigirLectura();
        return empleadoRepository.findById(id);
    }

    public List<AuditoriaCambio> historial(Long empleadoId) {
        exigirLectura();
        if (!empleadoRepository.existsById(empleadoId)) {
            throw new NoSuchElementException("Empleado no encontrado");
        }
        return auditoriaCambioRepository.findByEmpleadoIdOrderByFechaHoraDesc(empleadoId);
    }

    private void exigirEscritura() {
        SesionActual.exigirRol(Rol.ADMINISTRADOR, Rol.ENCARGADO_PERSONAL);
    }

    private void exigirLectura() {
        SesionActual.exigirRol(Rol.ADMINISTRADOR, Rol.ENCARGADO_PERSONAL, Rol.EMPLEADO, Rol.USUARIO);
    }

    private void registrarCambio(Empleado empleado, String campo, String anterior, String nuevo) {
        if (Objects.equals(nullSafe(anterior), nullSafe(nuevo))) {
            return;
        }
        AuditoriaCambio cambio = new AuditoriaCambio();
        cambio.setEmpleado(empleado);
        cambio.setCampo(campo);
        cambio.setValorAnterior(nullSafe(anterior));
        cambio.setValorNuevo(nullSafe(nuevo));
        cambio.setUsuario(SesionActual.nombre());
        cambio.setFechaHora(LocalDateTime.now());
        auditoriaCambioRepository.save(cambio);
    }

    private String nullSafe(String valor) {
        return valor == null || "null".equals(valor) ? "" : valor;
    }
}
