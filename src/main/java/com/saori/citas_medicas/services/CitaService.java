package com.saori.citas_medicas.services;

import com.saori.citas_medicas.dto.CitaRequest;
import com.saori.citas_medicas.dto.CitaResponseDTO;
import com.saori.citas_medicas.enums.EstadoCita;
import com.saori.citas_medicas.models.Cita;
import com.saori.citas_medicas.models.Doctor;
import com.saori.citas_medicas.models.HorarioDisponible;
import com.saori.citas_medicas.models.Paciente;
import com.saori.citas_medicas.repositories.CitaRepository;
import com.saori.citas_medicas.repositories.DoctorRepository;
import com.saori.citas_medicas.repositories.HorarioRepository;
import com.saori.citas_medicas.repositories.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CitaService {
    private final CitaRepository citaRepository;
    private final HorarioRepository horarioRepository;
    private final DoctorRepository doctorRepository;
    private final PacienteRepository pacienteRepository;

    @Autowired
    public CitaService(CitaRepository citaRepository, HorarioRepository horarioRepository,
                       DoctorRepository doctorRepository, PacienteRepository pacienteRepository) {
        this.citaRepository = citaRepository;
        this.horarioRepository = horarioRepository;
        this.doctorRepository = doctorRepository;
        this.pacienteRepository = pacienteRepository;
    }

    @Transactional
    public CitaResponseDTO reservarCita(CitaRequest request) {
        // Buscar el doctor y el paciente
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor no encontrado"));
        Paciente paciente = pacienteRepository.findById(request.getPacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        // Verificar si el horario está disponible
        String fecha = request.getFecha();
        String hora = request.getHora();

        System.out.println("Buscando horario disponible para el doctor en la fecha " + fecha + " y hora " + hora);
        Optional<HorarioDisponible> horarioOpt = horarioRepository.findByDoctorAndFechaAndHora(doctor, fecha, hora);

        if (horarioOpt.isEmpty()) {
            System.out.println("No se encontró un horario disponible en la base de datos.");
            throw new RuntimeException("El doctor no está disponible en este horario.");
        }

        HorarioDisponible horario = horarioOpt.get();
        System.out.println("Horario encontrado: " + horario.getId() + " | Ocupado: " + horario.isOcupado());

        if (horario.isOcupado()) {
            System.out.println("Este horario ya está ocupado.");
            throw new RuntimeException("El doctor no está disponible en este horario.");
        }

        // Marcar el horario como ocupado
        horario.setOcupado(true);
        horarioRepository.save(horario);
        System.out.println("Marcando horario como ocupado en la base de datos.");

        // Crear y guardar la cita
        Cita cita = new Cita();
        cita.setDoctor(doctor);
        cita.setPaciente(paciente);
        cita.setFecha(fecha);
        cita.setHora(hora);
        cita.setEstado(EstadoCita.PENDIENTE);

        System.out.println("Guardando la cita en la base de datos...");
        cita = citaRepository.save(cita);

        // Convertir la entidad Cita a CitaResponseDTO
        return convertToResponseDTO(cita);
    }

    private CitaResponseDTO convertToResponseDTO(Cita cita) {
        CitaResponseDTO responseDTO = new CitaResponseDTO();
        responseDTO.setId(cita.getId());
        responseDTO.setFecha(cita.getFecha());
        responseDTO.setHora(cita.getHora());
        responseDTO.setEstado(cita.getEstado().toString());

        // Convertir Doctor a DoctorDTO
        CitaResponseDTO.DoctorDTO doctorDTO = new CitaResponseDTO.DoctorDTO();
        doctorDTO.setId(cita.getDoctor().getId());
        doctorDTO.setNombre(cita.getDoctor().getNombre());
        doctorDTO.setEmail(cita.getDoctor().getEmail());
        responseDTO.setDoctor(doctorDTO);

        // Convertir Paciente a PacienteDTO
        CitaResponseDTO.PacienteDTO pacienteDTO = new CitaResponseDTO.PacienteDTO();
        pacienteDTO.setId(cita.getPaciente().getId());
        pacienteDTO.setNombre(cita.getPaciente().getNombre());
        pacienteDTO.setEmail(cita.getPaciente().getEmail());
        responseDTO.setPaciente(pacienteDTO);

        return responseDTO;
    }
}