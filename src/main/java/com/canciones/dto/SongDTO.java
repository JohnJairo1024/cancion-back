package com.canciones.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SongDTO {
    private Long id;
    
    @NotBlank(message = "El título no puede estar vacío")
    private String titulo;
    
    @NotBlank(message = "El artista no puede estar vacío")
    private String artista;
    
    @NotBlank(message = "El álbum no puede estar vacío")
    private String album;
    
    @NotNull(message = "El año no puede estar vacío")
    private String anno;
    
    @NotBlank(message = "El género no puede estar vacío")
    private String genero;
}
