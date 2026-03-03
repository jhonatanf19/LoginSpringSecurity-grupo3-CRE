package com.grupo3.login.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data // Genera automáticamente getters, setters, toString, equals y hashCode
public class UsuarioLoginDTO {

    @NotBlank(message = "El email es obligatorio para entrar")
    @Email(message = "Formato de correo inválido")
    // Validamos que el usuario ingrese un correo registrado en la BD
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    // Campo requerido para comparar contra el hash almacenado en la base de datos
    private String password;

}
