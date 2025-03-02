package com.saori.citas_medicas.controllers;

import org.springframework.web.bind.annotation.*; // ✅ Importa todas las anotaciones de Spring Web
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import com.saori.citas_medicas.services.CitaService;
import com.saori.citas_medicas.models.Cita;
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

    @PostMapping("/reservar")
    public ResponseEntity<CitaResponseDTO> reservarCita(@RequestBody CitaRequest request) {
         CitaResponseDTO responseDTO = citaService.reservarCita(request);
        return ResponseEntity.ok(responseDTO);
    }
}
