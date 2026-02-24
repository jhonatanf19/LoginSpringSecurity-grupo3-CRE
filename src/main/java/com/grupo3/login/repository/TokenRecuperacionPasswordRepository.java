package com.grupo3.login.repository;

import com.grupo3.login.model.TokenRecuperacionPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository // Gestiona el acceso y las operaciones CRUD en la base de datos
public interface TokenRecuperacionPasswordRepository extends JpaRepository<TokenRecuperacionPassword, Long> {

    // Para buscar el registro cuando el usuario haga clic en el enlace del correo
    Optional<TokenRecuperacionPassword> findByTokenRecuperacion(String token);

    // Reemplaza el token viejo antes de crear uno nuevo por el OneToOne
    void deleteByUsuarioIdUsuario(Long idUsuario);

}
