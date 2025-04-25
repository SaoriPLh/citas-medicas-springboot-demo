package com.saori.citas_medicas.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saori.citas_medicas.models.PasswordResetToken;
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {

    //Obtener por el token 
    //servira para validar q exista o sea valido el token
    Optional<PasswordResetToken> findByToken(String string);
}