/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.saori.citas_medicas.models;
import jakarta.persistence.*;
import lombok.Setter;
@Entity
@Setter
@Table(name = "pacientes")
public class Paciente extends Usuario {
    private String telefono;
}
