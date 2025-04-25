package com.saori.citas_medicas.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "password_reset_tokens")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PasswordResetToken {
    //Cremos esta clase para saber el token temporal y a quien pertenece 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String token;
    //relacion uno a uno 
    @OneToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;
    private LocalDateTime expiracion;

    private boolean usado = false;





}
