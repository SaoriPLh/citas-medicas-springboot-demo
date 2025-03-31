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
        //validaciones de citaValidador validar que existen ...
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


    public CitaResponseDTO actualizarCita(Long id,ActualizarCitaRequest actualizarCitaRequest){
        //primer buscaremos la cita
        //luego deberiamamos buscar el horario y cambiarlo pero al mismo tiempo validar que este disponible, claramente debemos entonces buscar tambien al doctor y paciente
        String camnbio = actualizarCitaRequest.getCambio();
        Long nuevoDoctorId= actualizarCitaRequest.getNuevoDoctorId();
        LocalDate nuevaDate = actualizarCitaRequest.getNuevaFecha();

        LocalTime nuevaHora = actualizarCitaRequest.getNuevaHora();
       
        //optional puede  o no haber algo en esa cajita que es opcional (cita)

        Optional<Cita> citaBuscada = citaRepository.findById(id);  
        //verificamos que si exista algo dentro
        if (citaBuscada.isPresent()){

            //seria cita encontrada 
            Cita citaEncontrada = citaBuscada.get(); // y lo obtenemos
            switch (camnbio.toUpperCase()) {
                case "DOCTOR":
                //validar que no sea nulo?
                    Doctor doctor = citaValidator.validarDoctor(nuevoDoctorId);
                    citaEncontrada.setDoctor(doctor);
                    
                    break;
                case "HORARIO":

                
                //Verificar que el horario este disponible
                HorarioDisponible horario = citaValidator.validarHorarioDisponible(citaEncontrada.getDoctor(),  nuevaDate, nuevaHora);
               
                //aca seria  otro metodo ya que esto me devuelve si el horario esta disponible osea me devuelve el horario si esta disponible
                //pero como obviamente si esta disponible fallarà

                HorarioDisponible horarioAntiguo = citaValidator.buscarHorario(citaEncontrada.getDoctor(), citaEncontrada.getFecha(), citaEncontrada.getHora());
                horarioAntiguo.setOcupado(false);
                

                horario.setOcupado(true);
                citaEncontrada.setFecha(nuevaDate);
                citaEncontrada.setHora(nuevaHora);
                
                
            
                default:
                    break;
            }

            return convertirCitaADTO(citaEncontrada);

        }
        return null;
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
        
                // Validar que la cita esté en estado PENDIENTE
                if (cita.getEstado() == EstadoCita.PENDIENTE) {
                    cita.setEstado(EstadoCita.CONFIRMADA); // Cambiar el estado
        
                    citaRepository.save(cita); // Guardar la actualización
                    return true;
                }
        
                // Si la cita no está pendiente, no la podemos marcar como atendida
                return false;
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
