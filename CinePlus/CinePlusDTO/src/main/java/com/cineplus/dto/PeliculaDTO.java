package com.cineplus.dto;

import java.time.LocalDate;

/**
 * Pelicula del catalogo. Las de TMDB llevan idPelicula = null y su id en idExterno;
 * las de la base de datos llevan los dos.
 *
 * @author luisf
 */
public record PeliculaDTO(
        Integer idPelicula,
        String idExterno,
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