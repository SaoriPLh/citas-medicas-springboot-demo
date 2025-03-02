package com.saori.citas_medicas.models;

import com.saori.citas_medicas.enums.EstadoCita;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "citas")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Cita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    private String fecha;
    private String hora;

    @Enumerated(EnumType.STRING)
    private EstadoCita estado;
}
