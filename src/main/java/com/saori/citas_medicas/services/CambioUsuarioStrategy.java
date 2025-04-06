package com.saori.citas_medicas.services;

import com.saori.citas_medicas.models.Usuario;

public interface CambioUsuarioStrategy {
     void ejecutarCambio(Usuario usuario, Object data);
     default boolean validar(Usuario usuario, Object data) { return true; }
    
} 