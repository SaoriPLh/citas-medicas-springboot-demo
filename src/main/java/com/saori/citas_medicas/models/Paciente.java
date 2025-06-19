package com.saori.citas_medicas.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pacientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Paciente {

   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID propio

 @OneToOne(cascade = CascadeType.ALL)

    @JoinColumn(name = "usuario_id",  nullable = false, unique = true)
    private Usuario usuario;

    private String telefono;

       @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Cita>citasGuardadas = new ArrayList<>();

public void añadirCita(Cita cita) {
    citasGuardadas.add(cita);
    cita.setPaciente(this);
}

}
