package com.grupo3.login.security;

import com.grupo3.login.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;

@Service // Define esta clase como la capa de lógica de negocio.
public class ServicioJwt {

    // Esta es la "llave maestra". En un entorno real, no debe estar escrita aquí directamente.
    private final SecretKey LLAVE_SECRETA = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Tiempo de vida del token (ejemplo: 2 horas)
    private final long EXPIRACION = 7200000;

    public String generarToken(Usuario usuario) {
        return Jwts.builder()
                .setSubject(usuario.getEmail()) // Quién es el dueño
                .claim("Rol", usuario.getRol().name()) // Qué permisos tiene
                .setIssuedAt(new Date()) // Cuándo se creó
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRACION)) // Cuándo vence
                .signWith(LLAVE_SECRETA) // Firmamos con nuestra llave secreta
                .compact();
    }

    // 1. Extrae el email (subject) del token
    public String extraerEmail(String token) {
        return extraerTodosLosClaims(token).getSubject();
    }

    // Extrae el rol que guardamos en los claims personalizados
    public String extraerRol(String token) {
        return extraerTodosLosClaims(token).get("Rol", String.class);
    }

    // 2. Verifica si el token ha expirado comparando fechas
    public boolean esTokenExpirado(String token) {
        return extraerTodosLosClaims(token).getExpiration().before(new Date());
    }

    // 3. Método privado para abrir el token y leer su contenido (Payload)
    private Claims extraerTodosLosClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(LLAVE_SECRETA) // Usa tu llave maestra para validar la firma
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validarToken(String token) {
        try {
            // Esto intenta leer el token; si falla (por expiración o firma falsa), lanza excepción
            Jwts.parserBuilder()
                    .setSigningKey(LLAVE_SECRETA)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false; // El token no es válido
        }
    }

}
