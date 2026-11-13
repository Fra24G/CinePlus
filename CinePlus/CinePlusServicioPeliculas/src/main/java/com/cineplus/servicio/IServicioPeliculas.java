package com.cineplus.servicio;

import com.cineplus.dto.CarteleraDTO;
import com.cineplus.dto.PeliculaDTO;

/**
 * Contrato del servicio de peliculas. Negocio solo conoce esta interfaz,
 * no sabe si los datos vienen de TMDB o de otro proveedor.
 *
 */
public interface IServicioPeliculas {

    /** Peliculas en cartelera. Si TMDB no responde, devuelve el catalogo de respaldo. */
    CarteleraDTO consultarCartelera();

    /** Bytes de la imagen del poster, o un arreglo vacio si no se pudo obtener. */
    byte[] obtenerPoster(PeliculaDTO pelicula);
}