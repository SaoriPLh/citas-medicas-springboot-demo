// Usuario.java
package com.saori.citas_medicas.models;

import com.saori.citas_medicas.enums.Rol;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String nombre;
    private String password;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    private boolean verificado;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    private Doctor doctor;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    private Paciente paciente;

}
