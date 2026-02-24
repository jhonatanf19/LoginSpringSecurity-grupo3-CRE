package com.grupo3.login.controller;

import com.grupo3.login.dto.CambioPasswordDTO;
import com.grupo3.login.dto.SolicitudRecuperacionDTO;
import com.grupo3.login.service.RecuperacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/recuperacion")
public class RecuperacionController {

    @Autowired
    private RecuperacionService recuperacionService;

    @PostMapping("/solicitar")
    public ResponseEntity<String> solicitar(@RequestBody SolicitudRecuperacionDTO solicitud) {
        recuperacionService.enviarCorreoRecuperacion(solicitud);
        return ResponseEntity.ok("Correo de recuperación enviado exitosamente a Mailtrap");
    }

    @PostMapping("/restablecer")
    public ResponseEntity<String> restablecer(@RequestBody CambioPasswordDTO datos) {
        recuperacionService.cambiarPassword(datos);
        return ResponseEntity.ok("La contraseña ha sido actualizada");
    }

}
