package com.saori.citas_medicas.services;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.saori.citas_medicas.models.Doctor;
import com.saori.citas_medicas.models.Usuario;
import com.saori.citas_medicas.repositories.UsuarioRepository;

public class CambioPasswordStrategyTestTest {

 @Test
    public void testEjecutarCambio_CambiaYGuardaPassword() {
        // Arrange (preparar objetos simulados)
        PasswordEncoder encoderMock = Mockito.mock(PasswordEncoder.class);
        UsuarioRepository usuarioRepoMock = Mockito.mock(UsuarioRepository.class);
        //EL usuario simulado 
        Usuario usuario = new Doctor();
        //nueva contraseña simulada
        String nuevaPassword = "123456";
        //esto mira, primero declaramos lo q devolveremos ya que no ocuparemos directamente el hasheo 
        String passwordHasheada = "hashedPassword";
        //aca decimos que cuando se ejecute endermokcn encode con la nueva contraseña, devuelva el hasheo que definimos 
        Mockito.when(encoderMock.encode(nuevaPassword)).thenReturn(passwordHasheada);
        //entonces hacemos la accion y supongo q internamente lee lo anterior y lo ejecuta
        CambioPasswordStrategy estrategia = new CambioPasswordStrategy(encoderMock, usuarioRepoMock);

        // Act (ejecutar el método que quieres probar)
        estrategia.ejecutarCambio(usuario, nuevaPassword);

        
        // Assert (verificar resultados)
        assert usuario.getPassword().equals(passwordHasheada);
        Mockito.verify(usuarioRepoMock).save(usuario);
    }
}
