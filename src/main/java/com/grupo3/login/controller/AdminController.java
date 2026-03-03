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

@RestController // Define esta clase como un controlador de API que devuelve datos en formato JSON
@CrossOrigin(origins = "http://localhost:3000") // Permite la comunicación con el frontend React
@RequestMapping("/api/administrador") // Define la ruta base para todas las peticiones de este controlador
public class AdminController {

    @Autowired // Inyecta la lógica de negocio para procesar las peticiones
    private UsuarioService usuarioService;

    // Endpoint para que el administrador cree nuevas cuentas de usuario o admin
    // http://localhost:8091/api/administrador/registrar
    @PostMapping("/registrar")
    public ResponseEntity<String> registrarUsuario(@Valid @RequestBody UsuarioRegistroDTO dto) {
        try {
            // Mapea los datos del DTO a la entidad Usuario antes de procesar
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setEmail(dto.getEmail());
            nuevoUsuario.setPassword(dto.getPassword());
            nuevoUsuario.setRol(dto.getRol());
            // Procesa el registro y persiste la información en la base de datos
            usuarioService.registrar(nuevoUsuario);
            // Confirma la creación exitosa con un estado 200 OK
            return ResponseEntity.ok("El usuario ha sido registrado correctamente por el Admin");
        } catch (RuntimeException e) {
            // Muestra un error 400 si falla al registrarse
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Maneja el error que ocurre cuando se envía un rol que no existe en el sistema
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> manejarRolInvalido(HttpMessageNotReadableException ex) {
        // Notifica al cliente que el rol enviado no coincide con las opciones del sistema
        return ResponseEntity.badRequest().body("Los roles permitidos son ADMINISTRADOR y USUARIO");
    }

    // Endpoint para obtener la nómina completa de usuarios registrados
    // http://localhost:8091/api/administrador/listarUsuarios
    @GetMapping("/listarUsuarios")
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        // Muestra la lista de usuarios para supervisar roles y bloqueos
        return ResponseEntity.ok(usuarioService.listarTodosUsuarios());
    }

    // Endpoint para reactivar el acceso de un colaborador cuya cuenta fue suspendida
    // http://localhost:8091/api/administrador/desbloquear/{id}
    @PutMapping("/desbloquear/{id}")
    public ResponseEntity<String> desbloquearUsuarioporID(@PathVariable Long id) {
        try {
            // Llama al servicio para resetear intentos fallidos y cambiar el estado de la cuenta
            Usuario usuarioActivo = usuarioService.desbloquearUsuario(id);
            // Retorna una respuesta exitosa incluyendo el correo del usuario reactivado
            return ResponseEntity.ok("La cuenta de " + usuarioActivo.getEmail() + " ha sido reactivada exitosamente");
        } catch (RuntimeException e) {
            // Retorna un error 400 si el ID no corresponde a ningún usuario registrado
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
