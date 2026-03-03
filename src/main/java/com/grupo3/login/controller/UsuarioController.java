package com.grupo3.login.controller;

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

    // Procesa el inicio de sesión validando los datos del DTO antes de entrar al método
    // http://localhost:8091/api/usuarios/login
    @PostMapping("/login")
    public ResponseEntity<String> login (@Valid @RequestBody UsuarioLoginDTO loginDTO) {
        // Ejecuta la lógica de validación de identidad y credenciales en la capa de servicio
        String resultado = usuarioService.autenticar(loginDTO.getEmail(), loginDTO.getPassword());
        // Si la cuenta está suspendida, se deniega el acceso con un estado 403 Forbidden
        if (resultado.contains("Cuenta bloqueada")) {
            return ResponseEntity.status(403).body(resultado);
        }
        // Si el mensaje NO contiene "Bienvenido", asumimos que es un error de login (400)
        // Esto atrapará tanto el "No estás registrado" como los "intentos restantes"
        if (!resultado.contains("Bienvenido")) {
            return ResponseEntity.badRequest().body(resultado);
        }
        // Autenticación exitosa: confirma el acceso del usuario con un estado 200 OK
        return ResponseEntity.ok(resultado);
    }

    // Endpoint para finalizar la sesión del usuario de forma segura
    // http://localhost:8091/api/usuarios/logout
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        // Mandamos la orden al servicio para limpiar la identidad del usuario
        usuarioService.cerrarSesionActiva();
        // Aplicamos una condición para verificar si existe una sesión web activa
        if (session != null) {
            session.invalidate(); // Rompe la sesión y borra todos sus datos vinculados
        }
        // Respondemos que todo salió bien
        return ResponseEntity.ok("Sesión cerrada exitosamente");
    }

}
