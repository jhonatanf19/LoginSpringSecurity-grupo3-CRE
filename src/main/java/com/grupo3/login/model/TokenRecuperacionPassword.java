package com.grupo3.login.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data // Genera automáticamente getters, setters, toString, equals y hashCode
@Entity // Indica que esta clase será una tabla en la base de datos
@Table(name = "tokens_recuperacion_password") // Nombre de la tabla en la base de datos
public class TokenRecuperacionPassword {

    @Id // Define la clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrementa el ID en la BD

    @Column(name = "id_token") // Nombre de la columna en la tabla
    private Long idToken;

    // Un código único con muchos caracteres que se enviará al usuario
    @Column(name = "token_recuperacion", nullable = false, unique = true)
    private String tokenRecuperacion;

    // Relación Uno a Uno: Un usuario solo puede tener un token activo
    // Si genera uno nuevo, el anterior debe ser reemplazado
    @OneToOne(targetEntity = Usuario.class, fetch = FetchType.EAGER) // Busca el token y trae los datos del usuario
    @JoinColumn(nullable = false, name = "usuario_id", unique = true) // Nombre de Llave Foránea (FK) que conecta con la tabla tbl_usuario
    private Usuario usuario;

    // Guarda el momento exacto en que el token deja de ser válido.
    // Se usa LocalDateTime para manejar fechas y horas con precisión de segundos
    @Column(name = "fecha_expiracion", nullable = false)
    private LocalDateTime fechaExpiracion;

    // Compara la hora actual del servidor con la fecha de expiración guardada
    public boolean estaExpirado() {
        return LocalDateTime.now().isAfter(this.fechaExpiracion);
    }

}
