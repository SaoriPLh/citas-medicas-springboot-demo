package com.saori.citas_medicas.controllers;

import com.saori.citas_medicas.dto.ActualizarUsuarioRequest;
import com.saori.citas_medicas.dto.AuthResponse;
import com.saori.citas_medicas.dto.LoginRequest;
import com.saori.citas_medicas.dto.RegistroRequest;
import com.saori.citas_medicas.dto.UsuarioResponseDTO;
import com.saori.citas_medicas.models.Usuario;
import com.saori.citas_medicas.services.AuthService;
import com.saori.citas_medicas.exceptions.AuthenticationFailedException;

import javax.naming.AuthenticationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Usuario> registrarUsuario(@RequestBody RegistroRequest request) {
        System.out.println("🚀 Llegó al endpoint /auth/register");
        System.out.println("📨 Datos recibidos: " + request.getEmail() + ", rol: " + request.getRol());
    
        Usuario usuario = authService.registrarUsuario(request);
        return ResponseEntity.ok(usuario);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        try {
            String token = authService.authenticate(request.getEmail(), request.getPassword());
    
            // 🟢 Construimos respuesta solo con el token (mensajeError será null)
            AuthResponse response = new AuthResponse();
            response.setToken(token);
    
            return ResponseEntity.ok(response);
    
        } catch (AuthenticationFailedException e) {
            //Solo en error llenamos el mensajeError
            AuthResponse response = new AuthResponse();
            response.setMensajeError(e.getMessage());
    
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
    

     //vamos a llamar al auth Service de recuperar contraseña
     //Podemos devolve run auth responde
     //

        @PostMapping("/forgetPassword")
        public ResponseEntity<String> requestPassword(@RequestParam String email) {
            try {
                // Llamamos al servicio para solicitar el restablecimiento de contraseña
                String response = authService.solicitarRestablecimiento(email);
                return ResponseEntity.ok(response);
            } catch (RuntimeException e) {
                // Devolvemos un mensaje de error claro
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            } 
        }


        //vamos a recibir un dto request 
        @PostMapping("/resetPassword")
        public ResponseEntity<String> resetPassword(@RequestBody ActualizarUsuarioRequest actualizarUsuarioRequest){

                //manejamos excepciones
                try{
                    String respuesta = authService.aplicarCambioUsuario(actualizarUsuarioRequest);

                    return ResponseEntity.ok(respuesta);
                }catch (RuntimeException e){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
                }
}


    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> obtenerUsuario(@RequestHeader("Authorization") String token) {
        try {
            // Extraemos el token sin el prefijo "Bearer " por que si no no lo reconoce
            String jwtToken = token.replace("Bearer ", "");
            UsuarioResponseDTO usuario = authService.obtenerDatosUsuario(jwtToken);
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }


}


