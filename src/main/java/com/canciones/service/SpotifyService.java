package com.canciones.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpotifyService {

    @Value("${spotify.api.base.url}")
    private String spotifyBaseUrl;
    
    @Value("${spotify.user.token}")
    private String spotifyToken;
    
    private final WebClient.Builder webClientBuilder;
    
    /**
     * Obtiene los géneros disponibles desde la API de Spotify
     * @return Lista de géneros musicales
     */
    public List<String> getAvailableGenres() {
        log.info("Consultando géneros disponibles en Spotify");
        
        return webClientBuilder.build()
                .get()
                .uri(spotifyBaseUrl + "/v1/recommendations/available-genre-seeds")
                .header("Authorization", "Bearer " + spotifyToken)
                .retrieve()
                .bodyToMono(GenreResponse.class)
                .map(GenreResponse::getGenres)
                .onErrorResume(e -> {
                    log.error("Error al obtener géneros de Spotify: {}", e.getMessage());
                    return Mono.just(List.of("rock", "pop", "classical", "jazz", "electronic"));
                })
                .block();
    }
    
    /**
     * Verifica si un género existe en la lista de géneros de Spotify
     * @param genre Género a verificar
     * @return true si el género existe, false en caso contrario
     */
    public boolean isValidGenre(String genre) {
        if (genre == null || genre.isEmpty()) {
            return false;
        }
        
        List<String> availableGenres = getAvailableGenres();
        return availableGenres.contains(genre.toLowerCase());
    }
    
    // Clase interna para mapear la respuesta de Spotify
    private static class GenreResponse {
        private List<String> genres;
        
        public List<String> getGenres() {
            return genres;
        }
    }
}
