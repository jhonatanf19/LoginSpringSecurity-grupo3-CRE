package com.grupo3.login.controller;

import com.grupo3.login.dto.LoginRespuestaDTO;
import com.grupo3.login.dto.UsuarioLoginDTO;
import com.grupo3.login.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // Define esta clase como un controlador de API que devuelve datos en formato JSON
@CrossOrigin(origins = "http://localhost:3000") // Permite la comunicación con el frontend React
@RequestMapping("/api/usuarios") // Define la ruta base para todas las peticiones de este controlador
public class UsuarioController {

    @Autowired // Inyecta la lógica de negocio para procesar las peticiones
    private UsuarioService usuarioService;

    // Procesa el inicio de sesión devolviendo el Token, Email y Rol en un objeto JSON
    // http://localhost:8091/api/usuarios/login
    @PostMapping("/login")
    public ResponseEntity<?> login (@Valid @RequestBody UsuarioLoginDTO loginDTO) {
        try {
            // Intentamos autenticar; si tiene éxito, recibimos el DTO con el Token
            LoginRespuestaDTO respuesta = usuarioService.autenticar(loginDTO.getEmail(), loginDTO.getPassword());
            // Retornamos el objeto con estado 200 OK. Spring lo convierte automáticamente a JSON
            return ResponseEntity.ok(respuesta);
        } catch (RuntimeException e) {
            // Si el servicio lanzó una excepción (usuario no encontrado, clave incorrecta o bloqueo)
            // capturamos el mensaje y lo enviamos con un estado 400 Bad Request
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint para finalizar la sesión del usuario de forma segura
    // http://localhost:8091/api/usuarios/logout
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        // En JWT, el logout real lo hace el Frontend borrando el token de su memoria.
        // Aquí solo notificamos al servicio si necesitas hacer alguna limpieza interna.
        usuarioService.cerrarSesionActiva();
        // Respondemos que todo salió bien
        return ResponseEntity.ok("Sesión cerrada exitosamente");
    }

}
