package com.grupo3.login.controller;

import com.grupo3.login.dto.UsuarioLoginDTO;
import com.grupo3.login.model.Usuario;
import com.grupo3.login.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

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

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        // Mandamos la orden al servicio para limpiar la identidad del usuario
        usuarioService.cerrarSesionActiva();
        if (session != null) {
            session.invalidate(); // Rompe la sesión y borra todos sus datos vinculados
        }
        // Respondemos que todo salió bien
        return ResponseEntity.ok("Sesión cerrada exitosamente");
    }

}
