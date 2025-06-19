package com.saori.citas_medicas.controllers;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.saori.citas_medicas.dto.*;
import com.saori.citas_medicas.enums.Rol;
import com.saori.citas_medicas.exceptions.AuthenticationFailedException;
import com.saori.citas_medicas.models.*;
import com.saori.citas_medicas.repositories.DoctorRepository;
import com.saori.citas_medicas.repositories.PacienteRepository;
import com.saori.citas_medicas.repositories.UsuarioRepository;
import com.saori.citas_medicas.services.AuthService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
 

    @Autowired
    private DoctorRepository doctorRepository;

 private final PasswordEncoder passwordEncoder;
  
    @Autowired
    private PacienteRepository pacienteRepository;
    @PersistenceContext
    private EntityManager entityManager;
    private final UsuarioRepository usuarioRepository;

    public AuthController(AuthService authService, UsuarioRepository usuarioRepository,  PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/validate-token-login") 
    public ResponseEntity<?> validateToken(@RequestBody Map<String, String> request) {

        try {

            String token = authService.loginWithGoogle(request);

            AuthResponse authResponse = new AuthResponse();
            authResponse.setToken(token);

            return ResponseEntity.ok(authResponse);
         
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al validar el token");
        }
    }

    @PostMapping("/validate-token-register")
    public ResponseEntity<?> validateTokenRegister(@RequestBody Map<String, String> request) {
        
        try {
            String token = authService.registerWithGoogle(request);
            AuthResponse response = new AuthResponse();
            response.setToken(token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al validar el token" + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registrarUsuario(@RequestBody RegistroRequest request) {
        try {
            Usuario usuario = authService.registrarUsuario(request);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("mensajeError", e.getMessage()));
        }
    }





    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        try {
            String token = authService.authenticate(request.getEmail(), request.getPassword());
            AuthResponse response = new AuthResponse();
            response.setToken(token);
            return ResponseEntity.ok(response);
        } catch (AuthenticationFailedException e) {
            AuthResponse response = new AuthResponse();
            response.setMensajeError(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PostMapping("/forgetPassword")
    public ResponseEntity<String> requestPassword(@RequestParam String email) {
        try {
            String response = authService.solicitarRestablecimiento(email);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestBody ActualizarUsuarioRequest actualizarUsuarioRequest) {
        try {
            String respuesta = authService.aplicarCambioUsuario(actualizarUsuarioRequest);
            return ResponseEntity.ok(respuesta);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> obtenerUsuario(@RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            UsuarioResponseDTO usuario = authService.obtenerDatosUsuario(jwtToken);
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("/verificarCorreo")
    public ResponseEntity<?> enviarCodigo(@RequestParam String email) {
        try {
            String mensaje = authService.enviarCodigoVerificacion(email);
            return ResponseEntity.ok(Collections.singletonMap("mensaje", mensaje));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("mensajeError", e.getMessage()));
        }
    }

    @PostMapping("/reenviarCodigo")
    public ResponseEntity<?> reenviarCodigo(@RequestParam String email) {
        try {
            String mensaje = authService.reenviarCodigoVerificacion(email);
            return ResponseEntity.ok(Collections.singletonMap("mensaje", mensaje));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("mensajeError", e.getMessage()));
        }
    }

    @PostMapping("/confirmarCodigo")
    public ResponseEntity<?> confirmarCodigo(@RequestParam String email, @RequestParam String codigo) {
        try {
            boolean confirmado = authService.confirmarCodigoVerificacion(email, codigo);
            if (confirmado) {
                return ResponseEntity.ok(Collections.singletonMap("mensaje", "Correo verificado exitosamente."));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("mensajeError", "No se pudo verificar el correo."));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("mensajeError", e.getMessage()));
        }
    }

@Transactional
@PostMapping("/llenarDatosRegisterPostGoogle")
public ResponseEntity<?> llenarDatos(@RequestBody RegistroRequest request) {
    Logger logger = LoggerFactory.getLogger(AuthController.class);

    logger.info(" Iniciando llenado de datos para usuario con email: {}", request.getEmail());

    try {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario con email '" + request.getEmail() + "' no encontrado."));

        logger.debug("👤 Usuario encontrado: ID={}, Email={}", usuario.getId(), usuario.getEmail());

        entityManager.flush();
        logger.debug(" flush() ejecutado para asegurar sincronización del usuario");

        if (request.getRol().equalsIgnoreCase("doctor")) {
            if (doctorRepository.existsById(usuario.getId())) {
                logger.warn(" El usuario con ID {} ya es doctor", usuario.getId());
                return ResponseEntity.badRequest().body(Collections.singletonMap("mensajeError", "El usuario ya es doctor."));
            }
               String hashedPassword = passwordEncoder.encode(request.getPassword());
            Doctor doctor = new Doctor();
            doctor.setUsuario(usuario);
            doctor.setEspecialidad(request.getEspecialidad());
            usuario.setRol(Rol.DOCTOR);
            usuario.setPassword(hashedPassword);
            logger.info(" Guardando nuevo doctor con ID: {}, Especialidad: {}", usuario.getId(), request.getEspecialidad());
            doctorRepository.save(doctor);

        } else if (request.getRol().equalsIgnoreCase("paciente")) {
            if (pacienteRepository.existsById(usuario.getId())) {
                logger.warn(" El usuario con ID {} ya es paciente", usuario.getId());
                return ResponseEntity.badRequest().body(Collections.singletonMap("mensajeError", "El usuario ya es paciente."));
            }
        String hashedPassword = passwordEncoder.encode(request.getPassword());
            Paciente paciente = new Paciente();
            paciente.setUsuario(usuario);
            paciente.setTelefono(request.getTelefono());
            usuario.setRol(Rol.PACIENTE);
            usuario.setPassword(hashedPassword);
            logger.info("🔄 Guardando nuevo paciente con ID: {}, Teléfono: {}", usuario.getId(), request.getTelefono());
            pacienteRepository.save(paciente);

        } else {
            logger.error(" Rol inválido recibido: {}", request.getRol());
            return ResponseEntity.badRequest().body("Rol inválido: " + request.getRol());
        }

        logger.info(" Llenado de información completado correctamente para el usuario con ID: {}", usuario.getId());
        return ResponseEntity.ok(Collections.singletonMap("mensaje", "Llenado de informacion salió correctamente."));

    } catch (Exception e) {
        logger.error(" Error al guardar datos del usuario con email {}: ", request.getEmail(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("mensajeError", "Error al guardar datos: " + e.getMessage()));
    }
}



}
