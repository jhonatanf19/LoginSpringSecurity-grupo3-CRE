package com.grupo3.login.service;

import com.grupo3.login.model.Usuario;
import com.grupo3.login.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Tarea GRUP-13: Buscar por email (usado en login y para evitar duplicados)
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    // Esta es la función que usará el Admin para registrar empleados
    public Usuario registrarUsuario(Usuario usuario) {
        // Encriptamos la contraseña antes de guardarla (Seguridad obligatoria)
        String passwordEncriptada = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(passwordEncriptada);

        return usuarioRepository.save(usuario);
    }

    // Lógica para el RF-07 (Bloqueo de cuenta)
    public void incrementarIntentos(Usuario usuario) {
        usuario.setIntentosFallidos(usuario.getIntentosFallidos() + 1);
        if (usuario.getIntentosFallidos() >= 3) {
            usuario.setCuentaBloqueada(true);
        }
        usuarioRepository.save(usuario);
    }

    // Lógica para el RF-08 (El Admin desbloquea)
    public void desbloquearUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setCuentaBloqueada(false);
        usuario.setIntentosFallidos(0);
        usuarioRepository.save(usuario);
    }

    public String autenticar(String email, String password) {
        return usuarioRepository.findByEmail(email).map(usuario -> {

            if (usuario.isCuentaBloqueada()) {
                return "Error: Cuenta bloqueada. Contacte al administrador."; // RF-08
            }

            // Comparamos la clave de Postman con la encriptada en DB
            if (passwordEncoder.matches(password, usuario.getPassword())) {
                usuario.setIntentosFallidos(0); // Reset si acierta
                usuarioRepository.save(usuario);
                return "Bienvenido " + usuario.getEmail() + ", tu rol es: " + usuario.getRol();
            } else {
                // Si falla, disparamos la lógica de bloqueo (RF-07)
                incrementarIntentos(usuario);
                return "Error: Credenciales inválidas"; // Mensaje Genérico RF-05
            }
        }).orElse("Error: Credenciales inválidas"); // Si el correo no existe, mensaje genérico también
    }

}
