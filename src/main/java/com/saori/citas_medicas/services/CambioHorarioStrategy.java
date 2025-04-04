package com.saori.citas_medicas.services;

import com.saori.citas_medicas.dto.ActualizarCitaRequest;
import com.saori.citas_medicas.models.Cita;
import com.saori.citas_medicas.models.HorarioDisponible;
import com.saori.citas_medicas.valitator.CitaValidador;

public class CambioHorarioStrategy implements CambioCItaStrategy{
    private final CitaValidador citaValidador;

    public CambioHorarioStrategy(CitaValidador citaValidador) {
        this.citaValidador = citaValidador;
    }

    
    @Override
    public void ejecutarCambio(Cita cita, ActualizarCitaRequest actualizarCitaRequest) {

        HorarioDisponible horarioNuevo = citaValidador.validarHorarioDisponible(cita.getDoctor(), actualizarCitaRequest.getNuevaFecha(), actualizarCitaRequest.getNuevaHora());

        cita.setFecha(horarioNuevo.getFecha());
        cita.setHora(horarioNuevo.getHora());
    }




}
