package com.grupo3.login.controller;

import com.grupo3.login.dto.UsuarioLoginDTO;
import com.grupo3.login.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody UsuarioLoginDTO loginDTO) {

        String resultado = usuarioService.autenticar(loginDTO.getEmail(), loginDTO.getPassword());

        // Si el resultado es el error de bloqueo, lo mostramos tal cual (RF-07/RF-08)
        if (resultado.contains("bloqueada")) {
            return ResponseEntity.status(403).body(resultado);
        }

        // Para cualquier otro error (email mal, clave mal, etc.), mensaje GENÉRICO (RF-05)
        if (resultado.contains("Error")) {
            return ResponseEntity.badRequest().body("Error: El correo o la contraseña son incorrectos.");
        }

        // Si todo sale bien
        return ResponseEntity.ok(resultado);
    }

    // RF-05: Captura errores de validación de campos (ej: email vacío o mal formato)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationErrors(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body("Error: El correo o la contraseña son incorrectos.");
    }

}
