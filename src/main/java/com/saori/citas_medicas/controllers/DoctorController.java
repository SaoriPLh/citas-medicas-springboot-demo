package com.saori.citas_medicas.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.saori.citas_medicas.dto.DoctorDatosDTO;

import com.saori.citas_medicas.services.DoctorService;

@RestController
@RequestMapping("/doctores")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @GetMapping("/porEspecialidad")
    public ResponseEntity<List<DoctorDatosDTO>> obtenerPorEspecialidad(@RequestParam String especialidad) {
        List<DoctorDatosDTO> doctores = doctorService.obtenerDoctoresPorEspecialidad(especialidad);
        return ResponseEntity.ok(doctores);
    }
}
