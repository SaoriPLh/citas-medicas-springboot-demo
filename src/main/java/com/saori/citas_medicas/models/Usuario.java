package com.saori.citas_medicas.models;

import com.saori.citas_medicas.enums.Rol;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name= "usuarios")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String email;
    private String password; // ❌ Ya no se alterará automáticamente
    
    @Enumerated(EnumType.STRING)
    private Rol rol;
}
