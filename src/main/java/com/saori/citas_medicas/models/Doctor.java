package com.saori.citas_medicas.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "doctores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {

   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID propio

   @OneToOne(cascade = CascadeType.ALL)

    @JoinColumn(name = "id")
    private Usuario usuario;

    private String especialidad;


   @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Cita> citasGuardadas = new ArrayList<>();

@OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
private List<HorarioDisponible> horariosDisponibles = new ArrayList<>();



public void añadirCita(Cita cita) {
    citasGuardadas.add(cita);
    cita.setDoctor(this);
}


}
