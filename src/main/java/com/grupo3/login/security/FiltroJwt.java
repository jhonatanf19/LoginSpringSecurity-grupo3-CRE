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

        // 1. Extraer el encabezado "Authorization" de la petición
        String encabezado = request.getHeader("Authorization");

        if (encabezado != null && encabezado.startsWith("Bearer ")) {
            String token = encabezado.substring(7);

            // 1. Usamos el método que acabamos de arreglar
            if (servicioJwt.validarToken(token)) {
                String email = servicioJwt.extraerEmail(token);
                String rol = servicioJwt.extraerRol(token);

                // COLOCO EL MENSAJE AQUÍ 🚀
                System.out.println("DEBUG - Email: " + email + " | Rol extraído: " + rol);

                // 2. IMPORTANTE: Agregamos "ROLE_" para que .hasRole("ADMINISTRADOR") funcione
                SimpleGrantedAuthority autoridad = new SimpleGrantedAuthority("ROLE_" + rol);

                UsernamePasswordAuthenticationToken autenticacion =
                        new UsernamePasswordAuthenticationToken(email, null, List.of(autoridad));

                // 3. Le decimos a Spring: "Este usuario es legal, déjalo pasar"
                SecurityContextHolder.getContext().setAuthentication(autenticacion);
            }
        }

        // Continuar con los demás filtros de seguridad
        filterChain.doFilter(request, response);
    }

}
