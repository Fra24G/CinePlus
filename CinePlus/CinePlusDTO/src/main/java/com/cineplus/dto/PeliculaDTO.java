package com.cineplus.dto;

import java.time.LocalDate;

/**
 * Pelicula de la cartelera local.
 *
 * @author luisf
 */
public record PeliculaDTO(
        Integer idPelicula,
        String titulo,
        String director,
        String genero,
        int duracionMinutos,
        LocalDate fechaEstreno,
        String clasificacion,
        String sinopsis,
        String imagen,
        boolean disponible,
        LocalDate inicioCartelera,
        LocalDate finCartelera) {
}