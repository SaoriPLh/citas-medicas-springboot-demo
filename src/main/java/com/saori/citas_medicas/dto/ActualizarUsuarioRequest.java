package com.saori.citas_medicas.dto;
import lombok.*;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarUsuarioRequest {
    private String cambio;
    private String token;
    private String nuevoValor;
}
