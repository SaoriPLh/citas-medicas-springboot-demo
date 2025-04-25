package com.saori.citas_medicas.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarCitaRequest {
        private String cambio;
        private Long nuevoDoctorId;
        private LocalDate nuevaFecha;
        private LocalTime nuevaHora;
    }
    
