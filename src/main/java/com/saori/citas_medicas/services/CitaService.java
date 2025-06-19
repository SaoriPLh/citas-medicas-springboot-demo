package com.saori.citas_medicas.services;

import com.saori.citas_medicas.dto.ActualizarCitaRequest;
import com.saori.citas_medicas.dto.CitaRequest;
import com.saori.citas_medicas.dto.CitaResponseDTO;
import com.saori.citas_medicas.enums.EstadoCita;
import com.saori.citas_medicas.enums.Rol;
import com.saori.citas_medicas.models.*;
import com.saori.citas_medicas.repositories.CitaRepository;
import com.saori.citas_medicas.repositories.DoctorRepository;
import com.saori.citas_medicas.repositories.PacienteRepository;
import com.saori.citas_medicas.repositories.UsuarioRepository;
import com.saori.citas_medicas.valitator.CitaValidador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CitaService {

    private final CitaRepository citaRepository;
    private final CitaValidador citaValidator;
    private final UsuarioRepository usuarioRepository;
    private final DoctorRepository doctorRepository;
    private final PacienteRepository pacienteRepository;

    @Autowired
    public CitaService(CitaRepository citaRepository, CitaValidador citaValidator, UsuarioRepository usuarioRepository, DoctorRepository doctorRepository, PacienteRepository pacienteRepository) {
        this.doctorRepository = doctorRepository;
        this.pacienteRepository = pacienteRepository;
        this.citaRepository = citaRepository;
        this.citaValidator = citaValidator;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public CitaResponseDTO reservarCita(CitaRequest request) {
        Doctor doctor = citaValidator.validarDoctor(request.getDoctorId());
        Paciente paciente = citaValidator.validarPaciente(request.getPacienteId());
        HorarioDisponible horario = citaValidator.validarHorarioDisponible(doctor, request.getFecha(), request.getHora());

        horario.setOcupado(true);

        Cita cita = new Cita();
      
        cita.setFecha(request.getFecha());
        cita.setHora(request.getHora());
        cita.setEstado(EstadoCita.PENDIENTE);
        cita.setDescripcion(request.getDescripcion());

        doctor.añadirCita(cita);
        paciente.añadirCita(cita);
        cita = citaRepository.save(cita);

        return convertirCitaADTO(cita);
    }

@Transactional(readOnly = true)
public List<CitaResponseDTO> obtenerCitasUsuario(String token) {
    String email = citaValidator.obtenerEmailDesdeToken(token);
    Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    List<Cita> citas;

    if (usuario.getRol() == Rol.DOCTOR) {
        Doctor doctor = doctorRepository.findById(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Doctor no encontrado"));
        citas = doctor.getCitasGuardadas();
    } else if (usuario.getRol() == Rol.PACIENTE) {
        Paciente paciente = pacienteRepository.findById(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        citas = paciente.getCitasGuardadas();
    } else {
        throw new RuntimeException("Rol no reconocido.");
    }

    return citas.stream()
            .map(this::convertirCitaADTO)
            .toList();
}




    public Boolean actualizarEstadoCita(long id, String nuevoEstado) {

        Optional<Cita> citaOpt = citaRepository.findById(id);

        if (citaOpt.isEmpty()) return false;

        Cita cita = citaOpt.get();
        EstadoCita estadoActual = cita.getEstado();
        EstadoCita nuevo = EstadoCita.valueOf(nuevoEstado);

        if (estadoActual == EstadoCita.PENDIENTE && (nuevo == EstadoCita.CONFIRMADA || nuevo == EstadoCita.CANCELADA)) {
            cita.setEstado(nuevo);
        } else if (estadoActual == EstadoCita.CONFIRMADA && nuevo == EstadoCita.CANCELADA) {
            cita.setEstado(nuevo);
        } else {
            throw new IllegalStateException("Cambio de estado no permitido desde: " + estadoActual);
        }

        citaRepository.save(cita);
        return true;
    }

    public CitaResponseDTO actualizarCita(Long id, ActualizarCitaRequest req) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        CambioCItaStrategy estrategia = switch (req.getCambio()) {
            case "DOCTOR" -> new CambioDoctorStrategy(citaValidator);
            case "HORARIO" -> new CambioHorarioStrategy(citaValidator);
            default -> throw new IllegalArgumentException("Cambio no soportado");
        };

        estrategia.ejecutarCambio(cita, req);
        return convertirCitaADTO(cita);
    }

  @Transactional
public Boolean cancelarCita(long id) {
    Cita cita = citaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

    if (cita.getEstado() == EstadoCita.CANCELADA) {
        return false; 
    }

    HorarioDisponible horario = citaValidator.validarHorarioDisponible(
            cita.getDoctor(), cita.getFecha(), cita.getHora());

    horario.setOcupado(false);
    cita.setEstado(EstadoCita.CANCELADA);

    citaRepository.save(cita); 
    return true;
}


    public Boolean cambiarEstadoCita(long id, EstadoCita nuevoEstado) {
        Optional<Cita> citaOpt = citaRepository.findById(id);
        if (citaOpt.isEmpty()) return false;

        Cita cita = citaOpt.get();
        cita.setEstado(nuevoEstado);
        citaRepository.save(cita);
        return true;
    }

    private CitaResponseDTO convertirCitaADTO(Cita cita) {
        CitaResponseDTO dto = new CitaResponseDTO();
        dto.setId(cita.getId());
        dto.setFecha(cita.getFecha());
        dto.setHora(cita.getHora());
        dto.setEstado(cita.getEstado().toString());
        dto.setDescripcion(cita.getDescripcion());

      dto.setDoctor(new CitaResponseDTO.DoctorDTO(
    cita.getDoctor().getId(),
    cita.getDoctor().getUsuario().getNombre(),
    cita.getDoctor().getUsuario().getEmail()
));

dto.setPaciente(new CitaResponseDTO.PacienteDTO(
    cita.getPaciente().getId(),
    cita.getPaciente().getUsuario().getNombre(),
    cita.getPaciente().getUsuario().getEmail()
));


        return dto;
    }
}
