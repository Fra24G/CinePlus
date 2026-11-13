package com.cineplus.dto;

import java.time.LocalDateTime;

/**
 * Resena para mostrar. autor = nombre mostrado del usuario.
 *
 * @author luisf
 */
public record ResenaDTO(
        Integer idResena,
        Integer idPelicula,
        String tituloPelicula,
        String autor,
        int calificacion,
        String comentario,
        LocalDateTime fecha) {
}