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
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Component // Permite que Spring inyecte esta clase en otros servicios
public class CitaValidador {

    private final DoctorRepository doctorRepository;
    private final PacienteRepository pacienteRepository;
    private final HorarioRepository horarioRepository;

    public CitaValidador(DoctorRepository doctorRepository, PacienteRepository pacienteRepository,
                         HorarioRepository horarioRepository) {
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

    // ✅ Validar que el horario está disponible
    public HorarioDisponible validarHorarioDisponible(Doctor doctor, LocalDate fecha, LocalTime hora) {
        return horarioRepository.findByDoctorAndFechaAndHora(doctor, fecha, hora)
                .filter(h -> !h.isOcupado())
                .orElseThrow(() -> new HorarioNoDisponibleException("El doctor no está disponible en este horario"));
    }
}
