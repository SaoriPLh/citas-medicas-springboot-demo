package com.saori.citas_medicas.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CitaRequest {
    private Long doctorId;
    private Long pacienteId;
    private LocalDate fecha;
    private LocalTime hora;
    private String descripcion;
}
