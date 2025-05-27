package com.canciones.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.canciones.model.Role;
import com.canciones.model.User;
import com.canciones.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Inicializa datos de prueba para la aplicación
     */
    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // Crear usuario de prueba si no existe
            if (!userRepository.existsByUsername("admin")) {
                User admin = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("password"))
                        .email("admin@example.com")
                        .role(Role.ADMIN)
                        .build();
                
                userRepository.save(admin);
                log.info("Usuario de prueba creado: {}", admin.getUsername());
            }
            
            // Aquí se podrían crear playlists de prueba si fuera necesario
        };
    }
}
