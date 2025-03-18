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

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HorarioDisponible> horariosDisponibles;
   //cascadetype all nos permite que cuando se elimine un doctor eliminemos todaas sus citas y horarios por ejemplo
   //mientras que orphanremoval nos dice que si eliminamos una cita de ese doctor, lo eliminemos correctamente de la bd, ya que si eliminamos a la cita de la lista aca en java, se elimina pero sigue huerfano en la base de datos
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cita> citas;

    
}
