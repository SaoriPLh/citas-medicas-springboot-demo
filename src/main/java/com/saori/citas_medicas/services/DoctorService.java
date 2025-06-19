package com.saori.citas_medicas.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.saori.citas_medicas.dto.CitaResponseDTO.DoctorDTO;
import com.saori.citas_medicas.dto.DoctorDatosDTO;
import com.saori.citas_medicas.dto.HorarioDisponibleDTO;
import com.saori.citas_medicas.models.Doctor;
import com.saori.citas_medicas.repositories.DoctorRepository;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository; // añadimpos inyeccion de dependencias

    public List<DoctorDatosDTO> obtenerDoctoresPorEspecialidad(String especialidad) {
        
    List<Doctor> doctores = doctorRepository.findByEspecialidad(especialidad);
    List<DoctorDatosDTO> resultado = new ArrayList<>();

    for (Doctor doctor : doctores) {
        DoctorDatosDTO dto = convertirADTO(doctor);
        resultado.add(dto);
    }

    return resultado;
}


    public DoctorDatosDTO convertirADTO(Doctor doctor){
         List<HorarioDisponibleDTO> horariosDTO = doctor.getHorariosDisponibles().stream()
        .map(h -> new HorarioDisponibleDTO(
            h.getId(),
            h.getFecha(),
            h.getHora(),
            h.isOcupado()
        ))
        .collect(Collectors.toList());

        return new DoctorDatosDTO(
            doctor.getUsuario().getId(),
            doctor.getUsuario().getNombre(),
            doctor.getUsuario().getEmail(),
            doctor.getEspecialidad(),
            horariosDTO
        );

    }

    //metodo para convertir un objeto doctor en un doctorresponseDTO
}
