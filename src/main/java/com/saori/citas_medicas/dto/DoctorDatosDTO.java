package com.saori.citas_medicas.dto;

import java.util.List;

import lombok.*;

@Data
@AllArgsConstructor
public class DoctorDatosDTO {
    private Long id;
    private String nombre;
    private String email;
    private String especialidad;
    private List<HorarioDisponibleDTO> horariosDisponibles;
}
