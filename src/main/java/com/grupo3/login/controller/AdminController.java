package com.grupo3.login.controller;

import com.grupo3.login.dto.UsuarioRegistroDTO;
import com.grupo3.login.model.Usuario;
import com.grupo3.login.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/usuarios")
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/registrar")
    public ResponseEntity<String> registrarColaborador(@Valid @RequestBody UsuarioRegistroDTO dto) {

        // 1. Verificamos si el correo ya existe (GRUP-13)
        if (usuarioService.buscarPorEmail(dto.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: Este colaborador ya está registrado.");
        }

        // 2. Creamos el objeto Usuario
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setEmail(dto.getEmail());
        nuevoUsuario.setPassword(dto.getPassword()); // El Service aplicará BCrypt
        nuevoUsuario.setRol(dto.getRol()); // El Admin asigna el rol aquí
        usuarioService.registrarUsuario(nuevoUsuario);

        return ResponseEntity.ok("Colaborador registrado exitosamente por el Administrador.");
    }

    // Este método captura errores cuando el JSON enviado tiene datos que no coinciden (como el Rol)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleInvalidRole(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body("Error: Rol no válido. Los únicos roles disponibles son: USUARIO o ADMINISTRADOR");
    }
}
