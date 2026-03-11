package com.grupo3.login.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // Indica que esta clase contiene definiciones de beans para el contexto de Spring
@EnableWebSecurity // Habilita la seguridad web personalizada para proteger los endpoints de la API
public class SecurityConfig {

    @Autowired // Inyectamos el filtro que creamos para validar los tokens JWT
    private FiltroJwt filtroJwt;

    // Bean para encriptar contraseñas. Esto asegura que en la BD no se vea la clave real, sino un hash.
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configura la cadena de filtros de seguridad para gestionar el acceso a las rutas
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Deshabilita la protección CSRF para facilitar el envío de peticiones desde Postman/React
                .csrf(csrf -> csrf.disable())
                // Política Stateless: El servidor no guarda "recuerdos" del usuario. Cada petición debe validarse con el Token.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // Rutas Abiertas: Habilita el acceso público al endpoint de autenticación para el inicio de sesión
                        .requestMatchers("/api/usuarios/**").permitAll()
                        // Recuperación: Acceso libre ya que el usuario no tiene sesión activa si olvidó su clave.
                        .requestMatchers("/api/recuperacion/**").permitAll()
                        // Protección por Rol: Solo si el Token procesado internamente tiene el rol "ADMINISTRADOR".
                        .requestMatchers("/api/administrador/**").hasRole("ADMINISTRADOR")
                        // Restricción general: Cualquier otra petición exige que el proceso de validación del Token sea exitoso.
                        .anyRequest().authenticated()
                )

                // Bloqueamos las interfaces visuales por defecto de Spring para usar nuestras propias rutas internas.
                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable())
                /* Conexión del Filtro: Insertamos nuestro validador de Tokens en la cadena de seguridad
                 * para que analice cada petición de forma invisible antes de llegar a los controladores. */
                .addFilterBefore(filtroJwt, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
