package com.cineplus.dto;

import java.util.List;

/**
 * Lista de peliculas para la cartelera. enVivo indica si vino de TMDB o del catalogo de respaldo,
 * y estado es un mensaje para mostrar en pantalla.
 *
 */
public record CarteleraDTO(
        List<PeliculaDTO> peliculas,
        boolean enVivo,
        String estado) {

    public CarteleraDTO {
        peliculas = List.copyOf(peliculas);
    }
}