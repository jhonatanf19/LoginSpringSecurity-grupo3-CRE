package com.grupo3.login.service;

import com.grupo3.login.model.Usuario;
import com.grupo3.login.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    public Optional<Usuario> buscarPorEmail (String email) {
        // Busca el email del usuario para autenticarse o registrarse
        return usuarioRepository.findByEmail(email);
    }

    public Usuario registrar (Usuario usuario) {
        // Validamos la existencia previa del correo para evitar duplicados en el sistema
        if (buscarPorEmail(usuario.getEmail()).isPresent()) {
            throw new RuntimeException("El usuario " + usuario.getEmail() + " ya está registrado");
        }
        // Usamos el algoritmo de hash BCrypt a la contraseña ingresada antes de guardarla
        String passwordEncriptada = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(passwordEncriptada);
        // Guarda el nuevo registro en la base de datos con sus valores iniciales
        return usuarioRepository.save(usuario);
    }

    public String autenticar (String email, String password) {
        // Utilizamos el método buscarPorEmail del servicio para buscar al usuario
        Optional<Usuario> usuarioEncontrado = buscarPorEmail(email);
        // Si el Optional está vacío, significa que el correo no existe en el sistema
        if (usuarioEncontrado.isEmpty()) {
            return "No estás registrado en el sistema";
        }
        // Extraemos el objeto Usuario del contenedor Optional para trabajar con él
        Usuario usuario = usuarioEncontrado.get();
        // Valida el estado de la cuenta; si está bloqueada, detiene el proceso
        if (usuario.isCuentaBloqueada()) {
            return "Tu cuenta está bloqueada, contacte con el administrador";
        }
        // Compara la contraseña ingresada con la versión encriptada de la base de datos
        if (passwordEncoder.matches(password, usuario.getPassword())) {
            // En caso de éxito, reinicia el contador de errores y guarda los cambios
            usuario.setIntentosFallidos(0);
            usuarioRepository.save(usuario);
            return "Bienvenido " + usuario.getEmail() + ", tu rol es " + usuario.getRol();
        } else {
            // Incrementa el historial de fallos y calcula los intentos restantes antes del bloqueo
            incrementarIntentos(usuario);
            int intentosRestantes = 3 - usuario.getIntentosFallidos();
            // Si el contador llegó a 3, informamos el bloqueo inmediato
            if (usuario.isCuentaBloqueada()) {
                return "Superastes el límite de intentos, tu cuenta ha sido bloqueada";
            }
            return "Revise sus credenciales, le quedán " + intentosRestantes + " intentos";
        }

    }

    public void incrementarIntentos (Usuario usuario) {
        // Incrementa el contador de fallos tras una autenticación fallida
        usuario.setIntentosFallidos(usuario.getIntentosFallidos() + 1);
        // Si el usuario llega a 3 fallos, la cuenta cambia su estado a bloqueada
        if (usuario.getIntentosFallidos() >= 3) {
            usuario.setCuentaBloqueada(true);
        }
        // Actualiza el estado de la cuenta del usuario
        usuarioRepository.save(usuario);
    }

    public List<Usuario> listarTodosUsuarios() {
        // Muestra los campos de todos los usuarios registrados
        return usuarioRepository.findAll();
    }

    public Usuario desbloquearUsuario(Long id) {
        // Busca al usuario por su ID para proceder con la reactivación de su cuenta
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se econtró al usuario con su ID"));
        // Verifica si el usuario no está bloqueado para lanzar una excepción
        if (!usuario.isCuentaBloqueada()) {
            throw new RuntimeException("Esta cuenta ya se encuentra activa y no requiere desbloqueo");
        }
        // Restablece los valores de seguridad para permitir el acceso nuevamente al sistema
        usuario.setCuentaBloqueada(false);
        usuario.setIntentosFallidos(0);
        // Guarda el cambio del registro en la base de datos y retorna el usuario habilitado
        return usuarioRepository.save(usuario);
    }

}
