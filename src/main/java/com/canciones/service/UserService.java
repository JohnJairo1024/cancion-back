package com.canciones.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.canciones.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            log.debug("Buscando usuario por nombre de usuario: {}", username);
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> {
                        log.warn("Usuario no encontrado en la base de datos: {}", username);
                        return new UsernameNotFoundException("Usuario no encontrado: " + username);
                    });
        } catch (Exception e) {
            if (!(e instanceof UsernameNotFoundException)) {
                log.error("Error inesperado al buscar el usuario {}: {}", username, e.getMessage(), e);
            }
            throw e;
        }
    }
}
