package com.grupo3.login.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Mantener deshabilitado para Postman
                .authorizeHttpRequests(auth -> auth
                        // 1. Permitimos al Admin registrar (Tu AdminController)
                        .requestMatchers("/api/admin/usuarios/**").permitAll()

                        // 2. Permitimos a los usuarios loguearse (Tu nuevo UsuarioController)
                        .requestMatchers("/api/usuarios/login/**").permitAll()

                        // 3. Cualquier otra ruta requerirá estar autenticado
                        .anyRequest().authenticated()
                )
                // Como estamos usando controladores manuales para el login,
                // podemos simplificar o comentar el formLogin por ahora si solo usas Postman
                .formLogin(form -> form.disable())
                .logout(logout -> logout.permitAll());

        return http.build();
    }

}
