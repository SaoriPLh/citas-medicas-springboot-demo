package com.saori.citas_medicas.services;

import com.saori.citas_medicas.dto.ActualizarCitaRequest;
import com.saori.citas_medicas.dto.CitaRequest;

import com.saori.citas_medicas.enums.EstadoCita;
import com.saori.citas_medicas.models.Cita;
import com.saori.citas_medicas.models.Doctor;
import com.saori.citas_medicas.models.HorarioDisponible;
import com.saori.citas_medicas.models.Paciente;
import com.saori.citas_medicas.repositories.CitaRepository;
import com.saori.citas_medicas.dto.CitaResponseDTO;
import com.saori.citas_medicas.valitator.CitaValidador;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;


@Service
public class CitaService {
   //Inyecciones de dependencia

    private final CitaRepository citaRepository;
    private final CitaValidador citaValidator; 

    @Autowired
    public CitaService(CitaRepository citaRepository, CitaValidador citaValidator) {
        this.citaRepository = citaRepository;
        this.citaValidator = citaValidator;
    }

    @Transactional
    //recibimos el formato de como debe ser una cita osea CitaRequest
    public CitaResponseDTO reservarCita(CitaRequest request) {
        //validaciones de citaValidador validar que existen o disponibilidad 
        Doctor doctor = citaValidator.validarDoctor(request.getDoctorId());
        Paciente paciente = citaValidator.validarPaciente(request.getPacienteId());
        HorarioDisponible horario = citaValidator.validarHorarioDisponible(doctor, request.getFecha(), request.getHora());

    
        horario.setOcupado(true);
        
        // Crear y guardar la cita
        Cita cita = new Cita();
        cita.setDoctor(doctor);
        cita.setPaciente(paciente);
        cita.setFecha(request.getFecha());
        cita.setHora(request.getHora());
        cita.setEstado(EstadoCita.PENDIENTE);
        cita = citaRepository.save(cita);

        return convertirCitaADTO(cita);
    }


    //Hacer NUEVA CLASE DONDE DEVOLDAMOS LA LISTA DE LAS CITAS DEL DOCTOR


    public CitaResponseDTO actualizarCita(Long id,ActualizarCitaRequest actualizarCitaRequest){
        //recibitemos el tipo de cambio sregun el request
        //validaremos q la cita exista

        

        
        Optional<Cita> citaBuscada = citaRepository.findById(id);

        if(citaBuscada.isPresent()){
            Cita cita = citaBuscada.get();

            //nulo porque tendremos varias opciones a implementar
            //no lo pasamos como parametro ya q necesitamos el actualizarcitaRequest
            CambioCItaStrategy estrategia = null;
            switch (actualizarCitaRequest.getCambio().toUpperCase()) {
                case "DOCTOR":
                    // Si el cambio es de doctor, usamos la estrategia de CambioDoctorStrategy
                    estrategia = new CambioDoctorStrategy(citaValidator);
                    break;
                case "HORARIO":
                    // Si el cambio es de horario, usamos la estrategia de CambioHorarioStrategy
                    estrategia = new CambioHorarioStrategy(citaValidator);
                    break;
                default:
                    throw new IllegalArgumentException("Cambio no soportado");
            }

        // Ejecutamos el cambio utilizando la estrategia seleccionada
        estrategia.ejecutarCambio(cita, actualizarCitaRequest);

        // Devolvemos la cita actualizada en el formato adecuado
        return convertirCitaADTO(cita);
    } else {
        // Si no encontramos la cita, devolvemos null (o puedes manejarlo de otra forma)
        return null;
    }
}

    //eliminar una cita, devolveremos un boolean? 

    @Transactional
    public Boolean cancelarCita(long id){
        //Primero buscamos la cita a traves de optional para manejar erores mejor
        Optional<Cita> citaEncontrada = citaRepository.findById(id);

        //si la cita existe debemos cancelar la cita, es decir, dejar los horarios y tod lobre, es como borrar la cita xd
        if(citaEncontrada.isPresent()){

            Cita cita = citaEncontrada.get();
            
            //h
            HorarioDisponible horario = citaValidator.validarHorarioDisponible(cita.getDoctor(), cita.getFecha(), cita.getHora());
            
            horario.setOcupado(false);
            cita.setEstado(EstadoCita.CANCELADA);

            citaRepository.deleteById(id);

            return true;


        }

        return false;
        }


        

    
        public Boolean marcarCitaComoAtendida(long id) {

            Optional<Cita> citaEncontrada = citaRepository.findById(id);
        
            if (citaEncontrada.isPresent()) {
                Cita cita = citaEncontrada.get();
        
                EstadoCitaStrategy estrategia = new ConfirmarCitaStrategy();
                estrategia.cambiarEstado(cita);
                
                return true;
            }
        
            // Si la cita no existe
            return false;
        }



        

        //verificar que exista la cita
        //VERIFICAR QUE ESTE  PENDIENTE
        //cambiar el estado de la cita como CONFIRMADA
        //GUARDAR LA CITA ACTUALIZADA
    
    // Metodo para convertir cita en DTO
    private CitaResponseDTO convertirCitaADTO(Cita cita) {
        CitaResponseDTO responseDTO = new CitaResponseDTO();
        responseDTO.setId(cita.getId());
        responseDTO.setFecha(cita.getFecha());
        responseDTO.setHora(cita.getHora());
        responseDTO.setEstado(cita.getEstado().toString());

        responseDTO.setDoctor(new CitaResponseDTO.DoctorDTO(
                cita.getDoctor().getId(), 
                cita.getDoctor().getNombre(), 
                cita.getDoctor().getEmail()));

        responseDTO.setPaciente(new CitaResponseDTO.PacienteDTO(
                cita.getPaciente().getId(), 
                cita.getPaciente().getNombre(), 
                cita.getPaciente().getEmail()));

        return responseDTO;
    }
    
}
