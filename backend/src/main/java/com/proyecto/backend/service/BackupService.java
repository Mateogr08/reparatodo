package com.proyecto.backend.service;

import com.proyecto.backend.model.CopiaSeguridad;
import com.proyecto.backend.model.Rol;
import com.proyecto.backend.repository.AsistenciaRepository;
import com.proyecto.backend.repository.CopiaSeguridadRepository;
import com.proyecto.backend.repository.EmpleadoRepository;
import com.proyecto.backend.repository.LiquidacionRepository;
import com.proyecto.backend.repository.PagoRepository;
import com.proyecto.backend.repository.UsuarioRepository;
import com.proyecto.backend.security.SesionActual;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class BackupService {

    private final EmpleadoRepository empleadoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AsistenciaRepository asistenciaRepository;
    private final LiquidacionRepository liquidacionRepository;
    private final PagoRepository pagoRepository;
    private final CopiaSeguridadRepository copiaSeguridadRepository;
    private final Path directorio;
    private final ObjectMapper mapper;

    public BackupService(
            EmpleadoRepository empleadoRepository,
            UsuarioRepository usuarioRepository,
            AsistenciaRepository asistenciaRepository,
            LiquidacionRepository liquidacionRepository,
            PagoRepository pagoRepository,
            CopiaSeguridadRepository copiaSeguridadRepository,
            ObjectMapper mapper,
            @Value("${app.backups.dir}") String directorio
    ) {
        this.empleadoRepository = empleadoRepository;
        this.usuarioRepository = usuarioRepository;
        this.asistenciaRepository = asistenciaRepository;
        this.liquidacionRepository = liquidacionRepository;
        this.pagoRepository = pagoRepository;
        this.copiaSeguridadRepository = copiaSeguridadRepository;
        this.mapper = mapper;
        this.directorio = Path.of(directorio);
    }

    public CopiaSeguridad crear(String origen) {
        try {
            Files.createDirectories(directorio);
            String nombre = "backup-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".json";
            Path archivo = directorio.resolve(nombre);

            Map<String, Object> contenido = new LinkedHashMap<>();
            contenido.put("fechaHora", LocalDateTime.now().toString());
            contenido.put("empleados", empleadoRepository.findAll());
            contenido.put("usuarios", usuarioRepository.findAll());
            contenido.put("asistencias", asistenciaRepository.findAll());
            contenido.put("liquidaciones", liquidacionRepository.findAll());
            contenido.put("pagos", pagoRepository.findAll());
            Files.writeString(archivo, mapper.writerWithDefaultPrettyPrinter().writeValueAsString(contenido));

            CopiaSeguridad copia = new CopiaSeguridad();
            copia.setNombreArchivo(nombre);
            copia.setRuta(archivo.toAbsolutePath().toString());
            copia.setTamanoBytes(Files.size(archivo));
            copia.setFechaHora(LocalDateTime.now());
            copia.setOrigen(origen);
            return copiaSeguridadRepository.save(copia);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo crear la copia de seguridad");
        }
    }

    public CopiaSeguridad crearManual() {
        SesionActual.exigirRol(Rol.ADMINISTRADOR);
        return crear("MANUAL");
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void crearAutomatica() {
        crear("AUTOMATICA");
    }

    public List<CopiaSeguridad> listar() {
        SesionActual.exigirRol(Rol.ADMINISTRADOR);
        return copiaSeguridadRepository.findAllByOrderByFechaHoraDesc();
    }
}
