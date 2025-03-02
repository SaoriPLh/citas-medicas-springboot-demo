package com.saori.citas_medicas.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CitaResponseDTO {
    
    private Long id;
    private DoctorDTO doctor;
    private PacienteDTO paciente;
    private String fecha;
    private String hora;
    private String estado;

    @Getter @Setter
    public static class DoctorDTO {
        private Long id;
        private String nombre;
        private String email;
    }

    @Getter @Setter
    public static class PacienteDTO {
        private Long id;
        private String nombre;
        private String email;
    }
}
