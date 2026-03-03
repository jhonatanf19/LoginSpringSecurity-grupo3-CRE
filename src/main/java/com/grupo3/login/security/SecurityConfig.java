package com.grupo3.login.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // Indica que esta clase contiene definiciones de beans para el contexto de Spring
@EnableWebSecurity // Habilita la seguridad web personalizada para proteger los endpoints de la API
public class SecurityConfig {

    // Define el algoritmo de hash BCrypt para la protección y comparación de contraseñas
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
                .authorizeHttpRequests(auth -> auth
                        // Permite el acceso libre a los endpoints de administración de usuarios
                        .requestMatchers("/api/administrador/**").permitAll()
                        // Habilita el acceso público al endpoint de autenticación para el inicio de sesión
                        .requestMatchers("/api/usuarios/**").permitAll()
                        // Permitimos los endpoints de recuperación sin estar logueado
                        .requestMatchers("/api/recuperacion/**").permitAll()
                        // Restringe cualquier otra ruta del sistema; requiere una sesión autenticada
                        .anyRequest().authenticated()
                )
                // Desactiva el formulario de inicio de sesión por defecto de Spring para usar controladores personalizados
                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable());
        return http.build();
    }

}
