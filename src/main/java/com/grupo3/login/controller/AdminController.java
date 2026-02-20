package com.grupo3.login.controller;

import com.grupo3.login.dto.UsuarioRegistroDTO;
import com.grupo3.login.model.Usuario;
import com.grupo3.login.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/administrador")
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/registrar")
    public ResponseEntity<String> registrarUsuario(@Valid @RequestBody UsuarioRegistroDTO dto) {
        try {
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setEmail(dto.getEmail());
            nuevoUsuario.setPassword(dto.getPassword());
            nuevoUsuario.setRol(dto.getRol());
            // Procesa el registro y persiste la información en la base de datos
            usuarioService.registrar(nuevoUsuario);
            return ResponseEntity.ok("El usuario ha sido registrado correctamente por el Admin");
        } catch (RuntimeException e) {
            // Muestra un error 400 si falla al registrarse
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Maneja el error que ocurre cuando se envía un rol que no existe en el sistema
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> manejarRolInvalido(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body("Los roles permitidos son ADMINISTRADOR y USUARIO");
    }

    @GetMapping("/listarUsuarios")
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        // Muestra la lista de usuarios para supervisar roles y bloqueos
        return ResponseEntity.ok(usuarioService.listarTodosUsuarios());
    }

    // Permite al Administrador reactivar el acceso de un colaborador bloqueado
    @PutMapping("/desbloquear/{id}")
    public ResponseEntity<String> desbloquearUsuarioporID(@PathVariable Long id) {
        try {
            // Ejecuta la lógica de desbloqueo y recupera los datos del usuario actualizado
            Usuario usuarioActivo = usuarioService.desbloquearUsuario(id);
            // Retorna una respuesta exitosa incluyendo el correo del usuario reactivado
            return ResponseEntity.ok("La cuenta de " + usuarioActivo.getEmail() + " ha sido reactivada exitosamente");
        } catch (RuntimeException e) {
            // Retorna un error 400 si el ID no corresponde a ningún usuario registrado
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
