package com.saori.citas_medicas.services;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.saori.citas_medicas.models.Usuario;
import com.saori.citas_medicas.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CambioPasswordStrategy implements CambioUsuarioStrategy {

    private final PasswordEncoder passwordEncoder; 
    private final UsuarioRepository usuarioRepository;

    @Override
    public void ejecutarCambio(Usuario usuario, Object data) {
        String hashedPassword = passwordEncoder.encode((String) data);
        usuario.setPassword(hashedPassword);
        usuarioRepository.save(usuario);
    }
}
