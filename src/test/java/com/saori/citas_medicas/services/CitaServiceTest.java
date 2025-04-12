package com.saori.citas_medicas.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.saori.citas_medicas.enums.EstadoCita;
import com.saori.citas_medicas.models.Cita;
import com.saori.citas_medicas.models.HorarioDisponible;
import com.saori.citas_medicas.repositories.CitaRepository;
import com.saori.citas_medicas.valitator.CitaValidador;

public class CitaServiceTest {
    @Test
    void testCancelarCita() {
        //simulamos repositorio
        CitaRepository citaRepositoryMock = Mockito.mock(CitaRepository.class);
        //simulamos validador
        CitaValidador citaValidadorMock = Mockito.mock(CitaValidador.class);
        
        CitaService citaService = new CitaService(citaRepositoryMock, citaValidadorMock);

          //simulamos el horario
          HorarioDisponible horario = new HorarioDisponible();
          //simulamos la cita
  
  
              Cita cita = new Cita();
              cita.setFecha(horario.getFecha());
              cita.setHora(horario.getHora());
  
   
        //aca estamos diciendo que cuando se ejecuten estos metodos mediante el citaService real, tendremos todo preparado para devolver lo mismo q ya tenemos  preparado para la simulacion 
    
        //por ejemplo esto en citaService real
        //citaValidator.validarHorarioDisponible(cita.getDoctor(), cita.getFecha(), cita.getHora());
        

        //se reemplaza por esto que retornamos lo que tenemos preparado 

        //Simulamos que obtenemos la cita por ejemplo por id
        Mockito.when(citaRepositoryMock.findById(1L)).thenReturn(Optional.of(cita));
        //simulamos la busqueda
        Mockito.when(citaValidadorMock.validarHorarioDisponible(Mockito.any(), Mockito.any(), Mockito.any()))
           .thenReturn(horario);

            //¿que queremos probar ?

            boolean resultado = citaService.cancelarCita(1);

            assertTrue(resultado);
            Mockito.verify(citaRepositoryMock).deleteById(1L);
          
            assertFalse(horario.isOcupado());
            assertEquals(EstadoCita.CANCELADA, cita.getEstado());


    }
}
