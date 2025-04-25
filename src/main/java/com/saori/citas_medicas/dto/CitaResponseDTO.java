package com.saori.citas_medicas.dto;
import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class CitaResponseDTO {
    private String mensaje;
    private Long id;
    private DoctorDTO doctor;
    private PacienteDTO paciente;
    private LocalDate fecha;
    private LocalTime hora;
    private String estado;
    private String descripcion;


    public CitaResponseDTO(String mensaje) {
        this.mensaje = mensaje;
    }

  

   

    
    @Getter @Setter
    @NoArgsConstructor //  Genera constructor vacío
    @AllArgsConstructor //  Genera constructor con todos los parámetros
    public static class DoctorDTO {
        private Long id;
        private String nombre;
        private String email;
    }





    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PacienteDTO {
        private Long id;
        private String nombre;
        private String email;
    }
}
