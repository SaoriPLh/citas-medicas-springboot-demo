package com.saori.citas_medicas.services;

import com.saori.citas_medicas.models.Usuario;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtUtil {
    private static final String SECRET_KEY = "CHOLIJYM789CHOLIJYM789CHOLIJYM789"; // 🔑 Asegúrate de que la clave sea de al menos 32 caracteres
    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes()); // ✅ Usa `Keys.hmacShaKeyFor()`

    //  Generar un token JWT
    public String generateToken(Usuario usuario) {
        return Jwts.builder()
                .setSubject(usuario.getEmail()) //  Usa el email como identificador
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 horas de expiración
                .signWith(key, SignatureAlgorithm.HS256) // Usa `key` correctamente
                .compact();
    }

    //  Validar un token JWT
    public boolean validateToken(String token, String username) {
        return extractUsername(token).equals(username) && !isTokenExpired(token);
    }

    // Extraer el nombre de usuario del token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extraer la fecha de expiración
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    //  Método genérico para extraer información del token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    //  Extraer todos los Claims con el nuevo `Jwts.parserBuilder()`
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key) // ✅ Usa `setSigningKey(key)`
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    //  Verificar si el token ha expirado
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}
