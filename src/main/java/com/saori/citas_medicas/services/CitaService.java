package com.saori.citas_medicas.services;

import com.saori.citas_medicas.dto.CitaRequest;

import com.saori.citas_medicas.enums.EstadoCita;
import com.saori.citas_medicas.models.Cita;
import com.saori.citas_medicas.models.Doctor;
import com.saori.citas_medicas.models.HorarioDisponible;
import com.saori.citas_medicas.models.Paciente;
import com.saori.citas_medicas.repositories.CitaRepository;
import com.saori.citas_medicas.dto.CitaResponseDTO;
import com.saori.citas_medicas.valitator.CitaValidador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CitaService {
   //Inyecciones de dependencia

    private final CitaRepository citaRepository;
    private final CitaValidador citaValidator; 

    @Autowired
    public CitaService(CitaRepository citaRepository, CitaValidador citaValidator) {
        this.citaRepository = citaRepository;
        this.citaValidator = citaValidator;
    }

    @Transactional
    //recibimos el formato de como debe ser una cita osea CitaRequest
    public CitaResponseDTO reservarCita(CitaRequest request) {
        //validaciones de citaValidador validar que existen ...
        Doctor doctor = citaValidator.validarDoctor(request.getDoctorId());
        Paciente paciente = citaValidator.validarPaciente(request.getPacienteId());
        HorarioDisponible horario = citaValidator.validarHorarioDisponible(doctor, request.getFecha(), request.getHora());

    
        horario.setOcupado(true);

        // Crear y guardar la cita
        Cita cita = new Cita();
        cita.setDoctor(doctor);
        cita.setPaciente(paciente);
        cita.setFecha(request.getFecha());
        cita.setHora(request.getHora());
        cita.setEstado(EstadoCita.PENDIENTE);
        cita = citaRepository.save(cita);

        return convertirCitaADTO(cita);
    }

    // Metodo para convertir cita en DTO
    private CitaResponseDTO convertirCitaADTO(Cita cita) {
        CitaResponseDTO responseDTO = new CitaResponseDTO();
        responseDTO.setId(cita.getId());
        responseDTO.setFecha(cita.getFecha());
        responseDTO.setHora(cita.getHora());
        responseDTO.setEstado(cita.getEstado().toString());

        responseDTO.setDoctor(new CitaResponseDTO.DoctorDTO(
                cita.getDoctor().getId(), 
                cita.getDoctor().getNombre(), 
                cita.getDoctor().getEmail()));

        responseDTO.setPaciente(new CitaResponseDTO.PacienteDTO(
                cita.getPaciente().getId(), 
                cita.getPaciente().getNombre(), 
                cita.getPaciente().getEmail()));

        return responseDTO;
    }
}
