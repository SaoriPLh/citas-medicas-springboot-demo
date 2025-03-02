package com.saori.citas_medicas.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CitaRequest {
    private Long doctorId;
    private Long pacienteId;
    private String fecha;
    private String hora;
}
