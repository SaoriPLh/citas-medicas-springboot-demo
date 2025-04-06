package com.saori.citas_medicas.services;

import com.saori.citas_medicas.dto.RegistroRequest;
import com.saori.citas_medicas.enums.Rol;
import com.saori.citas_medicas.models.Doctor;
import com.saori.citas_medicas.models.Paciente;
import com.saori.citas_medicas.models.Usuario;
import com.saori.citas_medicas.repositories.UsuarioRepository;
import com.saori.citas_medicas.valitator.CitaValidador;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // Método requerido por UserDetailsService para Spring Security
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(" Usuario con email '" + email + "' no encontrado."));
        
        return new User(usuario.getEmail(), usuario.getPassword(), Collections.emptyList());
    }

    //  REGISTRO DE USUARIO
    public Usuario registrarUsuario(RegistroRequest request) {
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException(" El email '" + request.getEmail() + "' ya está registrado.");
        }
    

        // 🔹 Hasheamos la contraseña con BCrypt
        String rawPassword = request.getPassword();
        String hashedPassword = passwordEncoder.encode(rawPassword);

        
        Usuario usuario;
        if ("DOCTOR".equalsIgnoreCase(request.getRol())) {
            Doctor doctor = new Doctor();
            doctor.setNombre(request.getNombre());
            doctor.setEmail(request.getEmail());
            doctor.setPassword(hashedPassword);
            doctor.setRol(Rol.DOCTOR);
            doctor.setEspecialidad(request.getEspecialidad());
            usuario = doctor;
        } else if ("PACIENTE".equalsIgnoreCase(request.getRol())) {
            Paciente paciente = new Paciente();
            paciente.setNombre(request.getNombre());
            paciente.setEmail(request.getEmail());
            paciente.setPassword(hashedPassword);
            paciente.setRol(Rol.PACIENTE);
            paciente.setTelefono(request.getTelefono());
            usuario = paciente;
        } else {
            throw new RuntimeException(" Rol inválido. Debe ser 'DOCTOR' o 'PACIENTE'.");
        }

        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        
   
        return usuarioGuardado;
    }

    // LOGIN DE USUARIO
    public String authenticate(String email, String password) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(" Usuario con email '" + email + "' no encontrado."));
        
        logger.info(" Contraseña ingresada por el usuario: {}", password);
        logger.info(" Contraseña almacenada en BD: {}", usuario.getPassword());
        
        boolean passwordMatches = passwordEncoder.matches(password, usuario.getPassword());
        logger.info(" ¿Las contraseñas coinciden?: {}", passwordMatches);

        if (!passwordMatches) {
            throw new BadCredentialsException(" Contraseña incorrecta.");
        }

        String token = jwtUtil.generateToken(usuario);
        logger.info(" Token generado correctamente para usuario: {}", email);
        return token;
    }
  

    // recibimos y validamos el email 
    // generamos el token temporal (lo guarmos )

    public void solicitarRestablecimiento(String email){

        
        Optional<Usuario> usuarioBuscado = usuarioRepository.findByEmail(email);
        
        if(usuarioBuscado.isPresent()){
            Usuario usuario = usuarioBuscado.get();
        }
    }

     
}
