package com.saori.citas_medicas.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HorarioDisponibleDTO {
    private Long id;
    private LocalDate fecha;
    private LocalTime hora;
    private boolean ocupado;
}
