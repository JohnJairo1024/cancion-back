package com.canciones.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import com.canciones.dto.PlaylistDTO;
import com.canciones.dto.SongDTO;
import com.canciones.model.Playlist;
import com.canciones.model.Song;
import com.canciones.repository.PlaylistRepository;
import com.canciones.repository.SongRepository;

@ExtendWith(MockitoExtension.class)
class PlaylistServiceTest {

    @Mock
    private PlaylistRepository playlistRepository;
    
    @Mock
    private SongRepository songRepository;
    
    @Mock
    private SpotifyService spotifyService;
    
    @InjectMocks
    private PlaylistService playlistService;
    
    private Playlist playlist;
    private Song song;
    private PlaylistDTO playlistDTO;
    private SongDTO songDTO;
    
    @BeforeEach
    void setUp() {
        // Setup Song
        song = new Song();
        song.setId(1L);
        song.setTitulo("Bohemian Rhapsody");
        song.setArtista("Queen");
        song.setAlbum("A Night at the Opera");
        song.setAnno("1975");
        song.setGenero("rock");
        
        // Setup SongDTO
        songDTO = new SongDTO();
        songDTO.setId(1L);
        songDTO.setTitulo("Bohemian Rhapsody");
        songDTO.setArtista("Queen");
        songDTO.setAlbum("A Night at the Opera");
        songDTO.setAnno("1975");
        songDTO.setGenero("rock");
        
        // Setup Playlist
        playlist = new Playlist();
        playlist.setId(1L);
        playlist.setNombre("Mis Favoritas");
        playlist.setDescripcion("Las mejores canciones de rock");
        Set<Song> songs = new HashSet<>();
        songs.add(song);
        playlist.setCanciones(songs);
        
        // Setup PlaylistDTO
        playlistDTO = new PlaylistDTO();
        playlistDTO.setId(1L);
        playlistDTO.setNombre("Mis Favoritas");
        playlistDTO.setDescripcion("Las mejores canciones de rock");
        Set<SongDTO> songDTOs = new HashSet<>();
        songDTOs.add(songDTO);
        playlistDTO.setCanciones(songDTOs);
        
        // Setup Spotify genre validation
        when(spotifyService.isValidGenre(any())).thenReturn(true);
    }
    
    @Test
    void getAllPlaylists_ShouldReturnListOfPlaylists() {
        // Given
        when(playlistRepository.findAll()).thenReturn(Arrays.asList(playlist));
        
        // When
        List<PlaylistDTO> result = playlistService.getAllPlaylists();
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Mis Favoritas", result.get(0).getNombre());
        assertEquals(1, result.get(0).getCanciones().size());
    }
    
    @Test
    void getPlaylistByName_WhenExists_ShouldReturnPlaylist() {
        // Given
        when(playlistRepository.findByNombre("Mis Favoritas")).thenReturn(Optional.of(playlist));
        
        // When
        PlaylistDTO result = playlistService.getPlaylistByName("Mis Favoritas");
        
        // Then
        assertNotNull(result);
        assertEquals("Mis Favoritas", result.getNombre());
        assertEquals("Las mejores canciones de rock", result.getDescripcion());
    }
    
    @Test
    void getPlaylistByName_WhenNotExists_ShouldThrowException() {
        // Given
        when(playlistRepository.findByNombre("No Existe")).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ResponseStatusException.class, () -> {
            playlistService.getPlaylistByName("No Existe");
        });
    }
    
    @Test
    void createPlaylist_WithValidData_ShouldReturnCreatedPlaylist() {
        // Given
        when(playlistRepository.existsByNombre("Mis Favoritas")).thenReturn(false);
        when(playlistRepository.save(any(Playlist.class))).thenReturn(playlist);
        
        // When
        PlaylistDTO result = playlistService.createPlaylist(playlistDTO);
        
        // Then
        assertNotNull(result);
        assertEquals("Mis Favoritas", result.getNombre());
        verify(playlistRepository, times(1)).save(any(Playlist.class));
    }
    
    @Test
    void createPlaylist_WithExistingName_ShouldThrowException() {
        // Given
        when(playlistRepository.existsByNombre("Mis Favoritas")).thenReturn(true);
        
        // When & Then
        assertThrows(ResponseStatusException.class, () -> {
            playlistService.createPlaylist(playlistDTO);
        });
    }
    
    @Test
    void createPlaylist_WithNullName_ShouldThrowException() {
        // Given
        playlistDTO.setNombre(null);
        
        // When & Then
        assertThrows(ResponseStatusException.class, () -> {
            playlistService.createPlaylist(playlistDTO);
        });
    }
    
    @Test
    void deletePlaylist_WhenExists_ShouldCallRepository() {
        // Given
        when(playlistRepository.existsByNombre("Mis Favoritas")).thenReturn(true);
        
        // When
        playlistService.deletePlaylist("Mis Favoritas");
        
        // Then
        verify(playlistRepository, times(1)).deleteByNombre("Mis Favoritas");
    }
    
    @Test
    void deletePlaylist_WhenNotExists_ShouldThrowException() {
        // Given
        when(playlistRepository.existsByNombre("No Existe")).thenReturn(false);
        
        // When & Then
        assertThrows(ResponseStatusException.class, () -> {
            playlistService.deletePlaylist("No Existe");
        });
    }
}
