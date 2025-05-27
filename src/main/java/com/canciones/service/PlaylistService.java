package com.canciones.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.canciones.dto.PlaylistDTO;
import com.canciones.dto.SongDTO;
import com.canciones.model.Playlist;
import com.canciones.model.Song;
import com.canciones.repository.PlaylistRepository;
import com.canciones.repository.SongRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final SongRepository songRepository;
    private final SpotifyService spotifyService;
    
    /**
     * Obtiene todas las listas de reproducción
     * @return Lista de todas las playlists
     */
    public List<PlaylistDTO> getAllPlaylists() {
        log.info("Obteniendo todas las listas de reproducción");
        return playlistRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene una lista de reproducción por su nombre
     * @param listName Nombre de la lista
     * @return DTO de la lista de reproducción
     */
    public PlaylistDTO getPlaylistByName(String listName) {
        log.info("Buscando lista de reproducción: {}", listName);
        Playlist playlist = findPlaylistByName(listName);
        return mapToDTO(playlist);
    }
    
    /**
     * Crea una nueva lista de reproducción
     * @param playlistDTO Datos de la lista a crear
     * @return DTO de la lista creada
     */
    @Transactional
    public PlaylistDTO createPlaylist(PlaylistDTO playlistDTO) {
        log.info("Creando nueva lista de reproducción: {}", playlistDTO.getNombre());
        
        // Validar que el nombre no sea nulo
        if (playlistDTO.getNombre() == null || playlistDTO.getNombre().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de la lista no puede estar vacío");
        }
        
        // Validar que no exista una lista con el mismo nombre
        if (playlistRepository.existsByNombre(playlistDTO.getNombre())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Ya existe una lista de reproducción con el nombre: " + playlistDTO.getNombre());
        }
        
        // Crear la entidad Playlist
        Playlist playlist = new Playlist();
        playlist.setNombre(playlistDTO.getNombre());
        playlist.setDescripcion(playlistDTO.getDescripcion());
        
        // Procesar las canciones si existen
        if (playlistDTO.getCanciones() != null && !playlistDTO.getCanciones().isEmpty()) {
            for (SongDTO songDTO : playlistDTO.getCanciones()) {
                validateSong(songDTO);
                Song song = mapToEntity(songDTO);
                playlist.addSong(song);
            }
        }
        
        // Guardar la playlist
        Playlist savedPlaylist = playlistRepository.save(playlist);
        return mapToDTO(savedPlaylist);
    }
    
    /**
     * Elimina una lista de reproducción por su nombre
     * @param listName Nombre de la lista a eliminar
     */
    @Transactional
    public void deletePlaylist(String listName) {
        log.info("Eliminando lista de reproducción: {}", listName);
        
        // Verificar que la lista exista
        if (!playlistRepository.existsByNombre(listName)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "No existe una lista de reproducción con el nombre: " + listName);
        }
        
        playlistRepository.deleteByNombre(listName);
    }
    
    /**
     * Busca una playlist por su nombre y lanza excepción si no existe
     * @param listName Nombre de la lista
     * @return Entidad Playlist
     */
    private Playlist findPlaylistByName(String listName) {
        return playlistRepository.findByNombre(listName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "No existe una lista de reproducción con el nombre: " + listName));
    }
    
    /**
     * Valida los datos de una canción
     * @param songDTO DTO de la canción a validar
     */
    private void validateSong(SongDTO songDTO) {
        // Validar título
        if (songDTO.getTitulo() == null || songDTO.getTitulo().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El título de la canción no puede estar vacío");
        }
        
        // Validar artista
        if (songDTO.getArtista() == null || songDTO.getArtista().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El artista de la canción no puede estar vacío");
        }
        
        // Validar álbum
        if (songDTO.getAlbum() == null || songDTO.getAlbum().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El álbum de la canción no puede estar vacío");
        }
        
        // Validar año
        if (songDTO.getAnno() == null || songDTO.getAnno().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El año de la canción no puede estar vacío");
        }
        
        // Validar género con Spotify
        if (songDTO.getGenero() == null || songDTO.getGenero().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El género de la canción no puede estar vacío");
        }
        
        // Opcional: validar que el género exista en Spotify
        if (!spotifyService.isValidGenre(songDTO.getGenero())) {
            log.warn("El género '{}' no está en la lista de géneros de Spotify", songDTO.getGenero());
            // Aquí podríamos lanzar una excepción o simplemente loguearlo como advertencia
        }
    }
    
    /**
     * Mapea una entidad Playlist a su DTO
     * @param playlist Entidad Playlist
     * @return DTO de la playlist
     */
    private PlaylistDTO mapToDTO(Playlist playlist) {
        PlaylistDTO dto = new PlaylistDTO();
        dto.setId(playlist.getId());
        dto.setNombre(playlist.getNombre());
        dto.setDescripcion(playlist.getDescripcion());
        
        // Mapear canciones
        if (playlist.getCanciones() != null) {
            dto.setCanciones(playlist.getCanciones().stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toSet()));
        }
        
        return dto;
    }
    
    /**
     * Mapea un DTO de canción a su entidad
     * @param songDTO DTO de la canción
     * @return Entidad Song
     */
    private Song mapToEntity(SongDTO songDTO) {
        // Buscar canción por ID si existe, de lo contrario crear una nueva
        Song song;
        if (songDTO.getId() != null) {
            song = songRepository.findById(songDTO.getId())
                    .orElseGet(Song::new);
        } else {
            song = new Song();
        }
        
        song.setTitulo(songDTO.getTitulo());
        song.setArtista(songDTO.getArtista());
        song.setAlbum(songDTO.getAlbum());
        song.setAnno(songDTO.getAnno());
        song.setGenero(songDTO.getGenero());
        return song;
    }
    
    /**
     * Mapea una entidad Song a su DTO
     * @param song Entidad Song
     * @return DTO de la canción
     */
    private SongDTO mapToDTO(Song song) {
        SongDTO dto = new SongDTO();
        dto.setId(song.getId());
        dto.setTitulo(song.getTitulo());
        dto.setArtista(song.getArtista());
        dto.setAlbum(song.getAlbum());
        dto.setAnno(song.getAnno());
        dto.setGenero(song.getGenero());
        return dto;
    }
}
