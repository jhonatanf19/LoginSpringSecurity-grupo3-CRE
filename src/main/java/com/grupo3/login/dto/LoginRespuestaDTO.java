package com.grupo3.login.dto;

import com.grupo3.login.model.Roles;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Genera automáticamente getters, setters, toString, equals y hashCode
@AllArgsConstructor // Crea un constructor con todos los atributos
@NoArgsConstructor // Crea un constructor vacío
public class LoginRespuestaDTO {

    @NotBlank(message = "El token es obligatorio")
    // Es la "llave" JWT que el frontend guardará para identificarse en cada petición
    private String token;

    @NotBlank(message = "El email es obligatorio para entrar")
    @Email(message = "Formato de correo inválido")
    // Validamos que el usuario ingrese un correo registrado en la BD
    private String email;

    // Define el nivel de acceso del usuario para mostrar u ocultar dashboards en React
    private Roles rol;

}
