package com.grupo3.login.service;

import com.grupo3.login.dto.CambioPasswordDTO;
import com.grupo3.login.dto.SolicitudRecuperacionDTO;
import com.grupo3.login.model.TokenRecuperacionPassword;
import com.grupo3.login.model.Usuario;
import com.grupo3.login.repository.TokenRecuperacionPasswordRepository;
import com.grupo3.login.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service // Define esta clase como la capa de lógica de negocio.
public class RecuperacionService {

    @Autowired // Inyecta el repositorio para interactuar con la base de datos
    private UsuarioRepository usuarioRepository;
    @Autowired // Inyecta el repositorio para interactuar con la base de datos
    private TokenRecuperacionPasswordRepository tokenRepository;
    @Autowired // Servicio para envío de correos (Mailtrap en pruebas).
    private JavaMailSender mailSender;
    @Autowired // Inyecta el codificador para manejar contraseñas de forma segura
    private BCryptPasswordEncoder passwordEncoder;

    // PASO 1: Generar token y enviar correo
    @Transactional
    public void enviarCorreoRecuperacion(SolicitudRecuperacionDTO solicitud) {
        // 1. Verifica que el usuario exista por su email.
        Usuario usuario = usuarioRepository.findByEmail(solicitud.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ese email."));

        // 2. Limpia tokens anteriores para evitar duplicados.
        tokenRepository.deleteByUsuarioIdUsuario(usuario.getIdUsuario());

        // 3. Genera un identificador único (UUID) con validez de 3 minutos.
        String tokenUUID = UUID.randomUUID().toString();
        TokenRecuperacionPassword nuevoToken = new TokenRecuperacionPassword();
        nuevoToken.setTokenRecuperacion(tokenUUID);
        nuevoToken.setUsuario(usuario);
        nuevoToken.setFechaExpiracion(LocalDateTime.now().plusMinutes(3));

        // 4. Guarda en BD y dispara el correo.
        tokenRepository.save(nuevoToken);
        enviarEmail(usuario.getEmail(), tokenUUID);
    }

    // PASO 2: Validar token y cambiar la clave
    @Transactional
    public void cambiarPassword(CambioPasswordDTO datos) {
        // 1. Busca el token enviado por el usuario en la BD.
        TokenRecuperacionPassword tokenBD = tokenRepository.findByTokenRecuperacion(datos.getToken())
                .orElseThrow(() -> new RuntimeException("Token inválido o no encontrado."));

        // 2. Valida si el tiempo de vida (3 min) ya pasó.
        if (tokenBD.estaExpirado()) {
            tokenRepository.delete(tokenBD);
            throw new RuntimeException("El código ha expirado. Solicite uno nuevo.");
        }

        // 3. Encripta la nueva clave y actualiza el usuario.
        Usuario usuario = tokenBD.getUsuario();
        usuario.setPassword(passwordEncoder.encode(datos.getNuevaPassword()));
        usuarioRepository.save(usuario);

        // 4. Elimina el token para que no se pueda reutilizar.
        tokenRepository.delete(tokenBD);
    }

    // Método privado para configurar y enviar el mensaje.
    private void enviarEmail(String destino, String token) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destino);
        mensaje.setSubject("Recuperación de contraseña");
        mensaje.setText("Tu código de recuperación es: " + token + "\nExpira en 3 minutos.");
        mailSender.send(mensaje);
    }

}
