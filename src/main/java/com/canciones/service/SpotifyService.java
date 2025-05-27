package com.canciones.service;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.canciones.exception.SpotifyApiException;
import com.canciones.exception.SpotifyApiException.AuthorizationException;
import com.canciones.exception.SpotifyApiException.ConnectionException;
import com.canciones.exception.SpotifyApiException.ResponseFormatException;

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
     * @throws SpotifyApiException sí hay problemas en la comunicación con Spotify
     */
    public List<String> getAvailableGenres() {
        log.info("Consultando géneros disponibles en Spotify");
        
        try {
            return webClientBuilder.build()
                    .get()
                    .uri(spotifyBaseUrl + "/v1/recommendations/available-genre-seeds")
                    .header("Authorization", "Bearer " + spotifyToken)
                    .retrieve()
                    .onStatus(status -> status.value() == 401,
                             response -> {
                                 String message = "Token de Spotify no válido o expirado (401 Unauthorized)";
                                 log.warn(message);
                                 return Mono.error(new AuthorizationException(message));
                             })
                    .bodyToMono(GenreResponse.class)
                    .map(GenreResponse::getGenres)
                    .onErrorResume(e -> {
                        if (e instanceof AuthorizationException) {
                            // Ya está registrado en el handler de onStatus, solo pasamos a usar géneros por defecto
                            log.debug("Usando géneros por defecto debido a error de autorización");
                        } else if (e instanceof WebClientResponseException.Unauthorized) {
                            String message = "Error de autorización con Spotify: Token inválido o expirado";
                            log.warn(message);
                        } else if (e instanceof java.net.SocketException || 
                                 e.getMessage() != null && (
                                 e.getMessage().contains("Connection reset") ||
                                 e.getMessage().contains("Connection refused"))) {
                            String message = "Problema de conexión con la API de Spotify. Usando géneros por defecto.";
                            log.warn(message);
                            throw new ConnectionException(message, e);
                        } else {
                            String message = "Error al obtener géneros de Spotify: " + e.getMessage();
                            log.error(message, e);
                            throw new ResponseFormatException(message, e);
                        }
                        
                        // Retornamos géneros por defecto en caso de error
                        return Mono.just(getDefaultGenres());
                    })
                    .block();
        } catch (SpotifyApiException e) {
            // Registramos el error y continuamos con géneros por defecto
            return getDefaultGenres();
        } catch (Exception e) {
            String message = "Error no controlado al comunicarse con Spotify: " + e.getMessage();
            log.error(message, e);
            return getDefaultGenres();
        }
    }
    
    /**
     * Proporciona una lista de géneros por defecto en caso de error con la API
     * @return Lista de géneros por defecto
     */
    private List<String> getDefaultGenres() {
        return List.of("rock", "pop", "classical", "jazz", "electronic", "rap", "reggae", "indie", "metal", "dance");
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
        
        try {
            List<String> availableGenres = getAvailableGenres();
            return availableGenres.contains(genre.toLowerCase());
        } catch (SpotifyApiException e) {
            log.warn("Error al validar el género '{}': {}", genre, e.getMessage());
            // En caso de error con Spotify, aceptamos el género para no bloquear al usuario
            return true;
        } catch (Exception e) {
            log.error("Error inesperado al validar el género '{}': {}", genre, e.getMessage());
            return true;
        }
    }

    @Getter
    @Setter
    private static class GenreResponse {
        private List<String> genres;
    }
}
