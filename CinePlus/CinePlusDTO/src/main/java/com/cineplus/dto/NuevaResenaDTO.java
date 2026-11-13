package com.cineplus.dto;

/**
 * Resena que se va a publicar. Si idPelicula es null se usa idExterno
 * para importar la pelicula al catalogo.
 *
 * @author luisf
 */
public record NuevaResenaDTO(
        Integer idPelicula,
        String idExterno,
        int calificacion,
        String comentario) {
}