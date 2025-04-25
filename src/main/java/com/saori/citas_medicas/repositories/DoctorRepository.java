/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.saori.citas_medicas.repositories;

import com.saori.citas_medicas.models.Doctor;
import com.saori.citas_medicas.models.HorarioDisponible;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author saori
 */
@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
     Optional<Doctor> findByEmail(String email);
     List<Doctor> findByEspecialidad(String especialidad);


}
