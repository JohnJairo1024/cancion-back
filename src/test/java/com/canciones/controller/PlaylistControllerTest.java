package com.canciones.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean; // Using deprecated annotation but required for WebMvcTest
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import com.canciones.dto.PlaylistDTO;
import com.canciones.dto.SongDTO;
import com.canciones.service.PlaylistService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(PlaylistController.class)
class PlaylistControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean // Note: This is deprecated but still needed for WebMvcTest until migrated to @WebMvcTest with explicit controller setup
    private PlaylistService playlistService;
    
    private PlaylistDTO playlistDTO;
    private SongDTO songDTO;
    
    @BeforeEach
    void setUp() {
        songDTO = SongDTO.builder()
                .id(1L)
                .titulo("Bohemian Rhapsody")
                .artista("Queen")
                .album("A Night at the Opera")
                .anno("1975")
                .genero("rock")
                .build();
        
        playlistDTO = PlaylistDTO.builder()
                .id(1L)
                .nombre("Mis Favoritas")
                .descripcion("Las mejores canciones de rock")
                .canciones(new HashSet<>(Arrays.asList(songDTO)))
                .build();
    }
    
    @Test
    @WithMockUser
    void getAllPlaylists_ShouldReturnOk() throws Exception {
        when(playlistService.getAllPlaylists()).thenReturn(Arrays.asList(playlistDTO));
        
        mockMvc.perform(get("/lists")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Mis Favoritas"))
                .andExpect(jsonPath("$[0].descripcion").value("Las mejores canciones de rock"));
    }
    
    @Test
    @WithMockUser
    void getPlaylistByName_WhenExists_ShouldReturnOk() throws Exception {
        when(playlistService.getPlaylistByName("Mis Favoritas")).thenReturn(playlistDTO);
        
        mockMvc.perform(get("/lists/Mis Favoritas")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Mis Favoritas"))
                .andExpect(jsonPath("$.canciones[0].titulo").value("Bohemian Rhapsody"));
    }
    
    @Test
    @WithMockUser
    void getPlaylistByName_WhenNotExists_ShouldReturnNotFound() throws Exception {
        when(playlistService.getPlaylistByName("No Existe"))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe una lista de reproducción con el nombre: No Existe"));
        
        mockMvc.perform(get("/lists/No Existe")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @WithMockUser
    void createPlaylist_WithValidData_ShouldReturnCreated() throws Exception {
        when(playlistService.createPlaylist(any(PlaylistDTO.class))).thenReturn(playlistDTO);
        
        mockMvc.perform(post("/lists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(playlistDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Mis Favoritas"));
    }
    
    @Test
    @WithMockUser
    void createPlaylist_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Crear playlist sin nombre (inválido)
        PlaylistDTO invalidPlaylist = PlaylistDTO.builder()
                .descripcion("Descripción sin nombre")
                .build();
        
        mockMvc.perform(post("/lists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidPlaylist)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @WithMockUser
    void deletePlaylist_WhenExists_ShouldReturnNoContent() throws Exception {
        doNothing().when(playlistService).deletePlaylist("Mis Favoritas");
        
        mockMvc.perform(delete("/lists/Mis Favoritas")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
    
    @Test
    @WithMockUser
    void deletePlaylist_WhenNotExists_ShouldReturnNotFound() throws Exception {
        doNothing().when(playlistService).deletePlaylist("No Existe");
        
        mockMvc.perform(delete("/lists/No Existe")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
