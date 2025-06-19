// Refactor total del AuthService ajustado a la relación OneToOne
package com.saori.citas_medicas.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.saori.citas_medicas.dto.ActualizarUsuarioRequest;
import com.saori.citas_medicas.dto.RegistroRequest;
import com.saori.citas_medicas.dto.UsuarioResponseDTO;
import com.saori.citas_medicas.enums.Rol;
import com.saori.citas_medicas.models.*;
import com.saori.citas_medicas.repositories.*;
import com.saori.citas_medicas.valitator.CitaValidador;
import com.saori.citas_medicas.valitator.UsuariosValidador;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UsuariosValidador usuariosValidador;
    private final EmailVerificationCodeRepository emailVerificationRepository;
    private final DoctorRepository doctorRepository;
    private final PacienteRepository pacienteRepository;
    
            final String CLIENT_ID = "543444956020-0iuqm441m2q0biusgqc7jeiaa0ic20p8.apps.googleusercontent.com";

    EmailService servicioEmail = new EmailService();

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario con email '" + email + "' no encontrado."));

        return new User(usuario.getEmail(), usuario.getPassword(), Collections.emptyList());
    }

    public Usuario registrarUsuario(RegistroRequest request) {
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("El email '" + request.getEmail() + "' ya está registrado.");
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(hashedPassword);
        usuario.setRol(Rol.valueOf(request.getRol().toUpperCase()));

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        if (usuarioGuardado.getRol() == Rol.DOCTOR) {
            Doctor doctor = new Doctor();
            doctor.setEspecialidad(request.getEspecialidad());
            doctor.setUsuario(usuarioGuardado);
            doctorRepository.save(doctor);
        } else if (usuarioGuardado.getRol() == Rol.PACIENTE) {
            Paciente paciente = new Paciente();
            paciente.setTelefono(request.getTelefono());
            paciente.setUsuario(usuarioGuardado);
            pacienteRepository.save(paciente);
        }

        return usuarioGuardado;
    }

    public String authenticate(String email, String password) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario con email '" + email + "' no encontrado."));

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new BadCredentialsException("Contraseña incorrecta.");
        }

        return jwtUtil.generateToken(usuario);
    }



    
    public String solicitarRestablecimiento(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario con email '" + email + "' no encontrado."));

        String token = UUID.randomUUID().toString();
        PasswordResetToken tokenReset = new PasswordResetToken();
        tokenReset.setToken(token);
        tokenReset.setUsuario(usuario);
        tokenReset.setExpiracion(LocalDateTime.now().plusMinutes(15));
        tokenReset.setUsado(false);

        passwordResetTokenRepository.save(tokenReset);
        return "enlace de pagina";
    }

    public String aplicarCambioUsuario(ActualizarUsuarioRequest actualizarUsuarioRequest) {
        if (!"PASSWORD".equalsIgnoreCase(actualizarUsuarioRequest.getCambio())) {
            throw new IllegalArgumentException("Tipo de cambio no soportado: " + actualizarUsuarioRequest.getCambio());
        }

        PasswordResetToken tokenBuscado = usuariosValidador.validarToken(actualizarUsuarioRequest.getToken());
        Usuario usuario = tokenBuscado.getUsuario();

        CambioUsuarioStrategy estrategia = new CambioPasswordStrategy(passwordEncoder, usuarioRepository);

        if (!estrategia.validar(usuario, actualizarUsuarioRequest.getNuevoValor())) {
            throw new RuntimeException("La nueva contraseña no es válida.");
        }

        estrategia.ejecutarCambio(usuario, actualizarUsuarioRequest.getNuevoValor());
        tokenBuscado.setUsado(true);
        passwordResetTokenRepository.save(tokenBuscado);

        return "Cambio de contraseña exitoso";
    }

    public UsuarioResponseDTO obtenerDatosUsuario(String token) {
        String email = jwtUtil.extractUsername(token);

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario con email '" + email + "' no encontrado."));

        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());
        dto.setRol(usuario.getRol().name());

        return dto;
    }

    public boolean confirmarCodigoVerificacion(String email, String codigo) {
        EmailVerificationCode codigoEntity = emailVerificationRepository.findByEmailAndCodigo(email, codigo)
                .orElseThrow(() -> new RuntimeException("El código ingresado es incorrecto."));

        if (LocalDateTime.now().isAfter(codigoEntity.getExpiracion())) {
            throw new RuntimeException("El código ha expirado. Solicita uno nuevo.");
        }

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setVerificado(true);
        usuarioRepository.save(usuario);

        return true;
    }

    public String enviarCodigoVerificacion(String email) {
        usuarioRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("No existe una cuenta con este correo."));

        String codigo = String.format("%06d", new Random().nextInt(999999));
        EmailVerificationCode verificacion = new EmailVerificationCode();
        verificacion.setEmail(email);
        verificacion.setCodigo(codigo);
        verificacion.setExpiracion(LocalDateTime.now().plusMinutes(10));

        emailVerificationRepository.save(verificacion);

        servicioEmail.enviar(email, "Tu código de verificación", "Tu código es: " + codigo);

        return "Código enviado correctamente al correo.";
    }

    public String reenviarCodigoVerificacion(String email) {
        usuarioRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Correo no registrado"));

        Optional<EmailVerificationCode> codigoOpt = emailVerificationRepository.findTopByEmailOrderByExpiracionDesc(email);

        if (codigoOpt.isPresent() && LocalDateTime.now().isBefore(codigoOpt.get().getExpiracion())) {
            servicioEmail.enviar(email, "Tu código de verificación", "Tu código sigue vigente: " + codigoOpt.get().getCodigo());
            return "Se reenvió el mismo código al correo.";
        }

        return enviarCodigoVerificacion(email);
    }


    public String registerWithGoogle(Map<String, String> request){

        try{
             String idToken = request.get("id_token");

            HttpTransport transport = new NetHttpTransport();
            JsonFactory jsonFactory = new GsonFactory();
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                    .setAudience(Collections.singletonList(CLIENT_ID)).build();

            GoogleIdToken token = verifier.verify(idToken);
            
            if (token != null) {
                String email = token.getPayload().getEmail();
                String nombre = (String) token.getPayload().get("name");
               // usuarioRepository.findByEmail(email) regresa Optional<Usuario>
             if (usuarioRepository.findByEmail(email).isPresent()) {
                throw new RuntimeException("Ya existe un usuario registrado con este correo");
            }
            
                    Usuario nuevoUsuario = new Usuario();
                    nuevoUsuario.setEmail(email);
                    nuevoUsuario.setNombre(nombre);
                    usuarioRepository.saveAndFlush(nuevoUsuario);

                    return jwtUtil.generateToken(nuevoUsuario);
                }

                else {
                    return "Token de google inválido";
                }
            


        }   
            catch (Exception e) {
                return "Error en el servidor "+ e.getMessage();
            }

    }

    

    public String loginWithGoogle(Map<String, String> request){
    try{
        String idToken = request.get("id_token");

        HttpTransport transport = new NetHttpTransport();
        JsonFactory jsonFactory = new GsonFactory();
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(CLIENT_ID)).build();

        GoogleIdToken token = verifier.verify(idToken);

        if (token != null) {
            String email = token.getPayload().getEmail();

            Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario con email '" + email + "' no encontrado."));

            return jwtUtil.generateToken(usuario);

        } else {
            return "Token de google inválido";
        }

    } catch (Exception e) {
        return "Error en el servidor " + e.getMessage();
    }
}


}


