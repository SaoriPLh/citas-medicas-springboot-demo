/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.saori.citas_medicas.models;
import java.util.List;

import jakarta.persistence.*;
import lombok.Setter;
@Entity
@Setter
@Table(name = "pacientes")
public class Paciente extends Usuario {
    private String telefono;
    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cita> citas; //aca decimos que el doctor tiene una lista de citas

    public void añadirCita(Cita cita){
        citas.add(cita);
        cita.setPaciente(this); // Establece la relacion
    }

    @Override
    public List<Cita> getCitasGuardadas() {
        return citas;
    }

    
}
