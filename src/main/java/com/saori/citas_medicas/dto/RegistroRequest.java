/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.saori.citas_medicas.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistroRequest {
    private String nombre;
    private String email;
    private String password;
    private String rol;
    private String especialidad; // Solo si es doctor
    private String telefono; // Solo si es paciente
}

