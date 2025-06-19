package com.saori.citas_medicas.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saori.citas_medicas.models.EmailVerificationCode;

public interface EmailVerificationCodeRepository extends JpaRepository<EmailVerificationCode, Long>{
    Optional<EmailVerificationCode> findByEmailAndCodigo(String email, String codigo);
    Optional<EmailVerificationCode> findTopByEmailOrderByExpiracionDesc(String email);


}
