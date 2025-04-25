package com.saori.citas_medicas.dto;

import lombok.Data;

@Data
public class UsuarioResponseDTO {

    //vamos a deovlver los datos del usuario
    private long id;
    private String nombre;
    private String email;
    private String rol; //  Agregamos el rol al DTO


}
