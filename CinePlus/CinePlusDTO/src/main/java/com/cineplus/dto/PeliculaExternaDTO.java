package com.cineplus.dto;

import java.time.LocalDate;

/**
 * Pelicula tal como llega del catalogo externo (TMDB), antes de importarla.
 *
 * @author luisf
 */
public record PeliculaExternaDTO(
        String idExterno,
        String titulo,
        String director,
        String genero,
        int duracionMinutos,
        LocalDate fechaEstreno,
        String clasificacion,
        String sinopsis,
        String imagen) {
}