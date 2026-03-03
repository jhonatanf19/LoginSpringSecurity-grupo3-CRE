package com.grupo3.login.controller;

import com.grupo3.login.dto.CambioPasswordDTO;
import com.grupo3.login.dto.SolicitudRecuperacionDTO;
import com.grupo3.login.service.RecuperacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // Define esta clase como un controlador de API que devuelve datos en formato JSON
@CrossOrigin(origins = "http://localhost:3000") // Permite la comunicación con el frontend React
@RequestMapping("/api/recuperacion") // Define la ruta base para todas las peticiones de este controlador
public class RecuperacionController {

    @Autowired // Inyecta la lógica de negocio para procesar las peticiones
    private RecuperacionService recuperacionService;

    // Endpoint para iniciar el flujo enviando un token al correo del usuario
    // http://localhost:8091/api/recuperacion/solicitar
    @PostMapping("/solicitar")
    public ResponseEntity<String> solicitar(@RequestBody SolicitudRecuperacionDTO solicitud) {
        // Ejecuta la creación del token y el envío del mensaje mediante Mailtrap
        recuperacionService.enviarCorreoRecuperacion(solicitud);
        // Confirma que el proceso de envío se completó con éxito
        return ResponseEntity.ok("Correo de recuperación enviado exitosamente a Mailtrap");
    }

    // Endpoint para validar el token recibido y establecer la nueva contraseña
    // http://localhost:8091/api/recuperacion/restablecer
    @PostMapping("/restablecer")
    public ResponseEntity<String> restablecer(@RequestBody CambioPasswordDTO datos) {
        // Ejecuta la validación de expiración y actualiza la clave en la base de datos
        recuperacionService.cambiarPassword(datos);
        // Responde con un estado 200 OK tras realizar el cambio de forma segura
        return ResponseEntity.ok("La contraseña ha sido actualizada");
    }

}
