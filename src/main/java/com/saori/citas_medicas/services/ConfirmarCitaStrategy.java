package com.saori.citas_medicas.services;

import com.saori.citas_medicas.enums.EstadoCita;
import com.saori.citas_medicas.models.Cita;

public class ConfirmarCitaStrategy implements EstadoCitaStrategy {
    @Override
    public void cambiarEstado(Cita cita) {
        if (cita.getEstado() != EstadoCita.CONFIRMADA) {
            cita.setEstado(EstadoCita.CONFIRMADA);
        } else {
            throw new IllegalStateException("Solo se puede confirmar una cita pendiente.");
        }
    }
}
