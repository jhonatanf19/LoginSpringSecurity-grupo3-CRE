package com.grupo3.login.repository;

import com.grupo3.login.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // Gestiona el acceso y las operaciones CRUD en la base de datos
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Tarea GRUP-13: Lógica de búsqueda de usuario por email
    // Esto es lo que usaremos para el Login y para Mailtrap
    Optional<Usuario> findByEmail(String email);

    // Esto nos servirá para la HU06 (Recuperación)
    Optional<Usuario> findByTokenRecuperacion(String token);

}
