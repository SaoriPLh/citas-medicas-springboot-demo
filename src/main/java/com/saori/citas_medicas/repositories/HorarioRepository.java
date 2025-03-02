/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.saori.citas_medicas.repositories;

import com.saori.citas_medicas.models.Doctor;
import com.saori.citas_medicas.models.HorarioDisponible;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author saori
 */
@Repository
public interface HorarioRepository extends JpaRepository<HorarioDisponible, Long> {
    Optional<HorarioDisponible> findByDoctorAndFechaAndHora(Doctor doctor, String fecha, String hora);
}
