package com.saori.citas_medicas.services;

import com.saori.citas_medicas.dto.ActualizarCitaRequest;
import com.saori.citas_medicas.models.Cita;
import com.saori.citas_medicas.models.Doctor;
import com.saori.citas_medicas.valitator.CitaValidador;

public class CambioDoctorStrategy implements CambioCItaStrategy {
    
     private final CitaValidador citaValidador;

     
    public CambioDoctorStrategy(CitaValidador citaValidador) {
        this.citaValidador = citaValidador;
    }


    @Override
    public void ejecutarCambio(Cita cita, ActualizarCitaRequest actualizarCitaRequest) {

         Doctor nuevoDoctor = citaValidador.validarDoctor(actualizarCitaRequest.getNuevoDoctorId());

         cita.setDoctor(nuevoDoctor);
    }

}
