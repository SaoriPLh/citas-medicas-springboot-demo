package com.saori.citas_medicas.services;

import com.saori.citas_medicas.dto.ActualizarCitaRequest;
import com.saori.citas_medicas.models.Cita;

public interface CambioCItaStrategy {
   
    //crearemos el metodo que todos los cambios de todo tipo q implementen esta interfaz tendran 

    void ejecutarCambio(Cita cita, ActualizarCitaRequest actualizarCitaRequest);
}
