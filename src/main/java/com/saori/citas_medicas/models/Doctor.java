/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.saori.citas_medicas.models;
import jakarta.persistence.*;
import java.util.List;
import lombok.Setter;

@Entity
@Setter
@Table(name = "doctores")
public class Doctor extends Usuario {
    private String Especialidad;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    private List<HorarioDisponible> horariosDisponibles;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    private List<Cita> citas;

    
}
