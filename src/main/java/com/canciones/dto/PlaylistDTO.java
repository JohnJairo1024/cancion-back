package com.canciones.dto;

import java.util.HashSet;
import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistDTO {
    private Long id;
    
    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;
    
    private String descripcion;
    
    @Builder.Default
    private Set<SongDTO> canciones = new HashSet<>();
}
