package com.cineplus.dto;

/**
 * Fila de "Peliculas en cartelera" del panel admin.
 *
 * @author luisf
 */
public record PeliculaAdminDTO(
        PeliculaDTO pelicula,
        long funcionesDisponibles) {
}