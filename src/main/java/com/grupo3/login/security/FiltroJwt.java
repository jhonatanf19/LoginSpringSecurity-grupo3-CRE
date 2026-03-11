package com.grupo3.login.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Component // Permite que Spring reconozca y gestione este filtro automáticamente
public class FiltroJwt extends OncePerRequestFilter {

    @Autowired // Inyectamos tu servicio para validar los tokens
    private ServicioJwt servicioJwt;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Analizamos de forma interna si la petición entrante incluye el encabezado de autorización HTTP
        String encabezado = request.getHeader("Authorization");

        // Verificamos que el encabezado exista y que empiece con la palabra "Bearer "
        if (encabezado != null && encabezado.startsWith("Bearer ")) {
            // Extraemos únicamente la cadena del Token, eliminando el prefijo "Bearer " (7 caracteres)
            String token = encabezado.substring(7);

            // Usamos nuestra "llave secreta" para verificar si el token es auténtico y no ha expirado
            if (servicioJwt.validarToken(token)) {
                // Si es válido, sacamos la información que guardamos dentro (Email y Rol)
                String email = servicioJwt.extraerEmail(token);
                String rol = servicioJwt.extraerRol(token);

                // Imprimimos en consola para confirmar que el filtro leyó el Token correctamente
                System.out.println("Autenticación exitosa - Email: " + email + " | Rol: " + rol);

                /* Spring Security exige que los roles empiecen con "ROLE_".
                 * Si en la BD el rol es "ADMINISTRADOR", aquí lo convertimos en "ROLE_ADMINISTRADOR"
                 * para que las reglas de acceso funcionen correctamente. */
                SimpleGrantedAuthority autoridad = new SimpleGrantedAuthority("ROLE_" + rol);

                // Creamos un objeto de "Identidad" con el email y el rol verificado
                UsernamePasswordAuthenticationToken autenticacion =
                        new UsernamePasswordAuthenticationToken(email, null, List.of(autoridad));

                /* Registramos al cliente (ya sea Administrador o Usuario) en la "sesión actual" de Spring.
                 * A partir de aquí, el sistema reconoce quién está operando y le otorga acceso a los
                 * métodos protegidos según los permisos de su rol. */
                SecurityContextHolder.getContext().setAuthentication(autenticacion);
            }
        }

        // Continúa con los demás filtros de seguridad en el controller
        filterChain.doFilter(request, response);
    }

}
