package com.saori.citas_medicas.services;

import com.saori.citas_medicas.enums.EstadoCita;
import com.saori.citas_medicas.models.Cita;

public interface EstadoCitaStrategy {

    //solo modificara la cita entonces le pasamos la cita y void
    void cambiarEstado(Cita cita, EstadoCita nuevoEstado);
}
