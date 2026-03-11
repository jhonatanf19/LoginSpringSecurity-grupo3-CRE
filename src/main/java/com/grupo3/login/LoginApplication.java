package com.grupo3.login;

import com.grupo3.login.model.Roles;
import com.grupo3.login.model.Usuario;
import com.grupo3.login.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class LoginApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoginApplication.class, args);
	}

    @Bean
    public CommandLineRunner adminCreadoAlIniciar(UsuarioRepository repository, BCryptPasswordEncoder encoder) {
        return args -> {
            // Verificamos si la tabla de usuarios está vacía para evitar duplicados
            if (repository.count() == 0) {
                Usuario admin = new Usuario();
                // Definimos el correo electrónico del administrador maestro
                admin.setEmail("admin@gmail.com");
                // Encriptamos la contraseña inicial antes de guardarla en la base de datos
                admin.setPassword(encoder.encode("12345678"));
                // Asignamos el nivel de acceso ADMINISTRADOR usando el Enum de Roles
                admin.setRol(Roles.ADMINISTRADOR);
                // Guardamos al usuario en la base de datos
                repository.save(admin);
                // Mensaje de confirmación en la consola del servidor
                System.out.println("Usuario administrador creado al iniciar ✅");
                System.out.println("Acceso inicial -> Email: admin@gmail.com | Password: 12345678");
            }
        };
    }

}
