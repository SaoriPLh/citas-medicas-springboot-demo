package com.saori.citas_medicas.controllers;

import org.springframework.web.bind.annotation.*; // ✅ Importa todas las anotaciones de Spring Web
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import com.saori.citas_medicas.services.CitaService;

import com.saori.citas_medicas.dto.CitaRequest;
import com.saori.citas_medicas.dto.CitaResponseDTO;

@RestController
@RequestMapping("/citas")
public class CitaController {
    private final CitaService citaService;

    @Autowired
    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    //responseentyty nos ayuda a estructurar y personalizar respuestas 
    //Ejemplos: ResponseEntiry.status(HttpStatus.UNAUTHORIZED)
    //Cuando extraemos por ejemplo el token de un usuario desde los headers de la petición y lo validamos entonces responseEntity nos ayuda a dar la estructura de la respuesta
   
    @PostMapping("/reservar")
    public ResponseEntity<CitaResponseDTO> reservarCita(@RequestBody CitaRequest request) {
        try{
            CitaResponseDTO responseDTO = citaService.reservarCita(request);
            return ResponseEntity.ok(responseDTO);
        }
       catch(Exception e){
             e.printStackTrace();
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new CitaResponseDTO("Error: "+e));
       }
    }

    @GetMapping("/usuario/citas")
public ResponseEntity<List<CitaResponseDTO>> obtenerCitasDelDoctor(@RequestHeader("Authorization") String token) {
    try {
        // Extraemos el token sin el prefijo "Bearer "
        String jwtToken = token.replace("Bearer ", "");
        List<CitaResponseDTO> citas = citaService.obtenerCitasUsuario(jwtToken);
        return ResponseEntity.ok(citas);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
}

//falta agregar notacion para solo rol de doctor puede cancelar la cita
@PostMapping("cancelarCita")
public ResponseEntity<?> cancelarCita(@RequestParam long id){
    try {
        boolean citaCancelada = citaService.cancelarCita(id);

        if (!citaCancelada) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La cita no se pudo cancelar.");
        }

        return ResponseEntity.ok("Cita cancelada correctamente");
    } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al cancelar la cita.");
    }
}

//actualizar el estado de la cita
@PostMapping("/actualizarEstado")
public ResponseEntity<String> actualizarEstadoCita(
        @RequestParam long id,
        @RequestParam String nuevoEstado) {
    try {
        // Llamamos al servicio para actualizar el estado de la cita
        Boolean actualizado = citaService.actualizarEstadoCita(id, nuevoEstado);

        if (actualizado) {
            return ResponseEntity.ok("El estado de la cita se actualizó correctamente.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se pudo actualizar el estado de la cita.");
        }
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrió un error al actualizar el estado de la cita.");
    }
}

}
