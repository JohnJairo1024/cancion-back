package com.canciones.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.canciones.dto.PlaylistDTO;
import com.canciones.service.PlaylistService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/lists")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;

    /**
     * Obtiene todas las listas de reproducción
     * @return Lista de playlists
     */
    @GetMapping
    public ResponseEntity<List<PlaylistDTO>> getAllPlaylists() {
        List<PlaylistDTO> playlists = playlistService.getAllPlaylists();
        return ResponseEntity.ok(playlists);
    }
    
    /**
     * Obtiene una lista de reproducción por su nombre
     * @param listName Nombre de la lista
     * @return Datos de la playlist
     */
    @GetMapping("/{listName}")
    public ResponseEntity<PlaylistDTO> getPlaylistByName(@PathVariable String listName) {
        PlaylistDTO playlist = playlistService.getPlaylistByName(listName);
        return ResponseEntity.ok(playlist);
    }
    
    /**
     * Crea una nueva lista de reproducción
     * @param playlistDTO Datos de la playlist a crear
     * @return Respuesta con la playlist creada
     */
    @PostMapping
    public ResponseEntity<PlaylistDTO> createPlaylist(@Valid @RequestBody PlaylistDTO playlistDTO) {
        PlaylistDTO createdPlaylist = playlistService.createPlaylist(playlistDTO);
        
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{name}")
                .buildAndExpand(createdPlaylist.getNombre())
                .toUri();
        
        return ResponseEntity.created(location).body(createdPlaylist);
    }
    
    /**
     * Elimina una lista de reproducción
     * @param listName Nombre de la lista a eliminar
     * @return Respuesta sin contenido
     */
    @DeleteMapping("/{listName}")
    public ResponseEntity<Void> deletePlaylist(@PathVariable String listName) {
        playlistService.deletePlaylist(listName);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
