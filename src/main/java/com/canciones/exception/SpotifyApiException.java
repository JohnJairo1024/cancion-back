package com.canciones.exception;

/**
 * Excepción personalizada para errores relacionados con la API de Spotify
 */
public class SpotifyApiException extends RuntimeException {

    public SpotifyApiException(String message) {
        super(message);
    }

    public SpotifyApiException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Excepción para problemas de autorización con Spotify (401 Unauthorized)
     */
    public static class AuthorizationException extends SpotifyApiException {
        public AuthorizationException(String message) {
            super(message);
        }
    }

    /**
     * Excepción para problemas de conexión a la API de Spotify
     */
    public static class ConnectionException extends SpotifyApiException {
        public ConnectionException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Excepción para problemas con el formato de la respuesta
     */
    public static class ResponseFormatException extends SpotifyApiException {
        public ResponseFormatException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
