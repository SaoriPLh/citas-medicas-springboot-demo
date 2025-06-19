package com.saori.citas_medicas.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

//se encarga de enviar correos
//necesitamos a q correo los vamos a enviar
//que le vamos a enviar 
@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void enviar(String to, String subject, String text){
        try{
            SimpleMailMessage mensaje = new SimpleMailMessage();

            mensaje.setTo(to);
            mensaje.setSubject(subject);
            mensaje.setText(text);
            mensaje.setFrom("correos.saori@gmail.com");
            mailSender.send(mensaje);
        }catch(Exception e){
            throw new RuntimeException("No se pudo enviar el correo: "+e.getMessage());
        }
        
    }
}
