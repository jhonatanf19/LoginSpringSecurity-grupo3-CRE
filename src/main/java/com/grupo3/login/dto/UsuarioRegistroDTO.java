package com.grupo3.login.dto;

import com.grupo3.login.model.Roles;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsuarioRegistroDTO {

    @Email(message = "Por favor, ingrese un correo electrónico válido")
    @NotBlank(message = "El email no puede estar vacío")
    private String email;

    // Podemos poner reglas que no están en la BD, como un mínimo de caracteres
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    @NotNull // El Admin DEBE elegir si es USUARIO o ADMINISTRADOR
    private Roles rol;

}
