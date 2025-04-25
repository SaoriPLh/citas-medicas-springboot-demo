package com.saori.citas_medicas.valitator;

import org.springframework.stereotype.Component;

import com.saori.citas_medicas.exceptions.PacienteNotFoundException;

import com.saori.citas_medicas.models.PasswordResetToken;
import com.saori.citas_medicas.repositories.PasswordResetTokenRepository;

@Component
public class UsuariosValidador {
     private final PasswordResetTokenRepository passwordResetTokenRepository;

        public UsuariosValidador(PasswordResetTokenRepository passwordResetTokenRepository) {
            this.passwordResetTokenRepository = passwordResetTokenRepository;
        }

    public PasswordResetToken validarToken(String token) {
        return passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new PacienteNotFoundException("Token no encontrado con Token: " + token));
    }
}
