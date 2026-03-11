package com.grupo3.login.dto;

import com.grupo3.login.model.Roles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Genera automáticamente getters, setters, toString, equals y hashCode
@AllArgsConstructor // Crea un constructor con todos los atributos
@NoArgsConstructor // Crea un constructor vacío
public class LoginRespuestaDTO {

    // Es la "llave" JWT que el frontend guardará para identificarse en cada petición
    private String token;

    // Devuelve el correo del usuario registrado en la BD
    private String email;

    // Define el nivel de acceso del usuario para mostrar u ocultar dashboards en React
    private Roles rol;

}

/* Nota: No usamos anotaciones de validación (como @NotBlank) porque este es un DTO de salida.
 * Su único propósito es entregar al Frontend el Token JWT para mantener la sesión activa,
 * junto con el email y el rol para que React evalúe a qué Dashboard redirigir al usuario. */