package com.grupo3.login.security;

import com.grupo3.login.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;

@Service // Clase encargada de la gestión de tokens para la seguridad del sistema.
public class ServicioJwt {

    // Firma digital única del servidor para garantizar que los tokens no sean manipulados
    private final SecretKey LLAVE_SECRETA = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Tiempo de validez del token (2 horas antes de que el usuario deba loguearse de nuevo)
    private final long EXPIRACION = 7200000;

    /* Genera un token JWT tras una autenticación exitosa.
     * Incluye el email del usuario y su nivel de acceso (Rol). */
    public String generarToken(Usuario usuario) {
        return Jwts.builder()
                .setSubject(usuario.getEmail()) // AQUI MEJORALO
                .claim("Rol", usuario.getRol().name()) // AQUI MEJORALO
                .setIssuedAt(new Date()) // Fecha de creación del Token
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRACION)) // Fecha de expiración del Token
                .signWith(LLAVE_SECRETA) // Cifrado de seguridad con la llave secreta
                .compact(); // Construcción final de la cadena de texto (Token)
    }

    // 1. Extrae el email del dueño del token para identificarlo en las peticiones
    public String extraerEmail(String token) {
        return extraerTodosLosClaims(token).getSubject();
    }

    // 2. Extrae el rol que guardamos en el token para validar permisos de Administrador o Usuario
    public String extraerRol(String token) {
        return extraerTodosLosClaims(token).get("Rol", String.class);
    }

    // 3. Verifica si el token ha expirado comparando fechas
    public boolean esTokenExpirado(String token) {
        return extraerTodosLosClaims(token).getExpiration().before(new Date());
    }

    /* Proceso de decodificación: Abre el token usando la llave secreta para leer su contenido.
     * Si la firma no coincide o el token es falso, este proceso fallará automáticamente. */
    private Claims extraerTodosLosClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(LLAVE_SECRETA) // Usamos la llave para validar la firma
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /* Validador de integridad: Verifica que el token sea auténtico y vigente.
     * Devuelve true si el servidor reconoce la firma, de lo contrario devuelve false. */
    public boolean validarToken(String token) {
        try {
            // Esto intenta leer el token; si falla (por expiración o firma falsa), lanza excepción
            Jwts.parserBuilder()
                    .setSigningKey(LLAVE_SECRETA)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
