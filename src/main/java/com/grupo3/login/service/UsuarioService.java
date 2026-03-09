package com.grupo3.login.service;

import com.grupo3.login.dto.LoginRespuestaDTO;
import com.grupo3.login.model.Usuario;
import com.grupo3.login.repository.UsuarioRepository;
import com.grupo3.login.security.ServicioJwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service // Define esta clase como la capa de lógica de negocio.
public class UsuarioService {

    @Autowired // Inyecta el repositorio para interactuar con la base de datos
    private UsuarioRepository usuarioRepository;
    @Autowired // Inyecta el codificador para manejar contraseñas de forma segura
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired // Inyecta el servicio especializado en la generación y gestión de tokens JWT
    private ServicioJwt servicioJwt;


    public Optional<Usuario> buscarPorEmail (String email) {
        // Busca el email del usuario para autenticarse o registrarse
        // Devuelve un Optional para evitar errores de nulos
        return usuarioRepository.findByEmail(email);
    }

    public Usuario registrar (Usuario usuario) {
        // Validamos la existencia previa del correo para evitar duplicados en el sistema
        if (buscarPorEmail(usuario.getEmail()).isPresent()) {
            throw new RuntimeException("El usuario " + usuario.getEmail() + " ya está registrado");
        }
        // Usamos el algoritmo de hash BCrypt a la contraseña para que no sea legible en la BD
        String passwordEncriptada = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(passwordEncriptada);
        // Guarda el nuevo registro en la base de datos con sus valores iniciales
        return usuarioRepository.save(usuario);
    }

    public LoginRespuestaDTO autenticar (String email, String password) {
        // Buscamos al usuario; si no existe, lanzamos una excepción de inmediato
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No estás registrado en el sistema"));
        // Verificamos si la cuenta está bloqueada antes de intentar validar la contraseña
        if (usuario.isCuentaBloqueada()) {
            throw new RuntimeException("Tu cuenta está bloqueada, contacte con el administrador");
        }
        // Compara la contraseña ingresada con la versión encriptada de la base de datos
        if (passwordEncoder.matches(password, usuario.getPassword())) {
            // En caso de éxito, reinicia el contador de errores y guarda los cambios
            usuario.setIntentosFallidos(0);
            usuarioRepository.save(usuario);
            // PASO CLAVE: Generamos el token JWT usando nuestro servicio especializado
            String token = servicioJwt.generarToken(usuario);
            // Retornamos el DTO con toda la información que el Frontend (React) necesita guardar
            return new LoginRespuestaDTO(token, usuario.getEmail(), usuario.getRol());
        } else {
            // Incrementa el historial de fallos y calcula los intentos restantes antes del bloqueo
            incrementarIntentos(usuario);
            int intentosRestantes = 3 - usuario.getIntentosFallidos();
            // Si el contador llegó a 3, informamos el bloqueo inmediato
            if (usuario.isCuentaBloqueada()) {
                throw new RuntimeException("Superaste el límite de intentos, tu cuenta ha sido bloqueada");
            }
            // Si aún le quedan intentos, lanzamos el error con la cantidad restante
            throw new RuntimeException("Revise sus credenciales, le quedan " + intentosRestantes + " intentos");
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

    public List<Usuario> listarTodosUsuarios () {
        // Muestra los campos de todos los usuarios registrados
        return usuarioRepository.findAll();
    }

    public Usuario desbloquearUsuario (Long id) {
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

    public void cerrarSesionActiva() {
        // Elimina la identidad del usuario del sistema para invalidar su acceso
        SecurityContextHolder.clearContext();
    }

}
