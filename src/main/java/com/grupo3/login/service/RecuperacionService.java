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

@Service
public class RecuperacionService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private TokenRecuperacionPasswordRepository tokenRepository;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // PASO 1: Generar token y enviar correo
    @Transactional
    public void enviarCorreoRecuperacion(SolicitudRecuperacionDTO solicitud) {
        Usuario usuario = usuarioRepository.findByEmail(solicitud.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ese email."));

        // Limpiamos tokens viejos antes de crear el nuevo (por el OneToOne)
        tokenRepository.deleteByUsuarioIdUsuario(usuario.getIdUsuario());

        String tokenUUID = UUID.randomUUID().toString();
        TokenRecuperacionPassword nuevoToken = new TokenRecuperacionPassword();
        nuevoToken.setTokenRecuperacion(tokenUUID);
        nuevoToken.setUsuario(usuario);
        nuevoToken.setFechaExpiracion(LocalDateTime.now().plusMinutes(3));

        tokenRepository.save(nuevoToken);
        enviarEmail(usuario.getEmail(), tokenUUID);
    }

    // PASO 2: Validar token y cambiar la clave
    @Transactional
    public void cambiarPassword(CambioPasswordDTO datos) {
        TokenRecuperacionPassword tokenBD = tokenRepository.findByTokenRecuperacion(datos.getToken())
                .orElseThrow(() -> new RuntimeException("Token inválido o no encontrado."));

        if (tokenBD.estaExpirado()) {
            tokenRepository.delete(tokenBD);
            throw new RuntimeException("El código ha expirado. Solicite uno nuevo.");
        }

        Usuario usuario = tokenBD.getUsuario();
        usuario.setPassword(passwordEncoder.encode(datos.getNuevaPassword()));
        usuarioRepository.save(usuario);

        // Al terminar, borramos el token para que no se use dos veces
        tokenRepository.delete(tokenBD);
    }

    private void enviarEmail(String destino, String token) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destino);
        mensaje.setSubject("Recuperación de contraseña");
        mensaje.setText("Tu código de recuperación es: " + token + "\nExpira en 3 minutos.");
        mailSender.send(mensaje);
    }

}
