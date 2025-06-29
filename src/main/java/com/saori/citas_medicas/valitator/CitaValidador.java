package com.saori.citas_medicas.valitator;

import com.saori.citas_medicas.exceptions.DoctorNotFoundException;
import com.saori.citas_medicas.exceptions.HorarioNoDisponibleException;
import com.saori.citas_medicas.exceptions.PacienteNotFoundException;
import com.saori.citas_medicas.models.Doctor;
import com.saori.citas_medicas.models.HorarioDisponible;
import com.saori.citas_medicas.models.Paciente;
import com.saori.citas_medicas.repositories.DoctorRepository;
import com.saori.citas_medicas.repositories.HorarioRepository;
import com.saori.citas_medicas.repositories.PacienteRepository;
import com.saori.citas_medicas.services.JwtUtil;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Component // Permite que Spring inyecte esta clase en otros servicios
public class CitaValidador {

    private final DoctorRepository doctorRepository;
    private final PacienteRepository pacienteRepository;
    private final HorarioRepository horarioRepository;
    private final JwtUtil jwtUtil; // Utilidad para manejar JWT

    public CitaValidador(DoctorRepository doctorRepository, PacienteRepository pacienteRepository,
                         HorarioRepository horarioRepository, JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        this.doctorRepository = doctorRepository;
        this.pacienteRepository = pacienteRepository;
        this.horarioRepository = horarioRepository;
    }

    // ✅ Validar que el doctor existe
    public Doctor validarDoctor(Long doctorId) {
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor no encontrado con ID: " + doctorId));
    }

    // ✅ Validar que el paciente existe
    public Paciente validarPaciente(Long pacienteId) {
        return pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new PacienteNotFoundException("Paciente no encontrado con ID: " + pacienteId));
    }

    // ✅ Validar que el horario está disponible, aca de cuardo al doctor me devuelve si esta disponinle el horario  SOLO ES PARA BUSCAR HORARIOS
    public HorarioDisponible validarHorarioDisponible(Doctor doctor, LocalDate fecha, LocalTime hora) {
        return horarioRepository.findByDoctorAndFechaAndHora(doctor, fecha, hora)
                .filter(h -> !h.isOcupado())
                .orElseThrow(() -> new HorarioNoDisponibleException("El doctor no está disponible en este horario"));
    }

    public HorarioDisponible buscarHorario(Doctor doctor, LocalDate fecha, LocalTime hora) {
        
        return horarioRepository.findByDoctorAndFechaAndHora(doctor, fecha, hora)
            .orElseThrow(() -> new RuntimeException("No se encontró el horario"));
    }

    public String obtenerEmailDesdeToken(String token) {
        // Usamos JwtUtil para extraer el email del token
        return jwtUtil.extractUsername(token);
    }
    
    public Doctor validarDoctorPorEmail(String email) {
        // Aquí validamos que el email pertenece a un doctor
        Optional<Doctor> doctor = doctorRepository.findByEmail(email);
        if (doctor.isEmpty()) {
            throw new IllegalArgumentException("El usuario no es un doctor válido.");
        }
        return doctor.get();
    }
    
}
