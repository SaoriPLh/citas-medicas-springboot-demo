package com.saori.citas_medicas.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor // ✅ Agrega este para permitir `new AuthResponse()`
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String mensajeError;
}
