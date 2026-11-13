package com.cineplus.negocio.bo;

import com.cineplus.dto.CarteleraDTO;
import com.cineplus.dto.PeliculaDTO;
import com.cineplus.servicio.IServicioPeliculas;

/**
 * Objeto de negocio de la cartelera. Solo conoce la interfaz del servicio de peliculas,
 * no sabe si los datos vienen de TMDB o de otro proveedor.
 *
 */
public class CarteleraBO {

    private final IServicioPeliculas servicio;

    public CarteleraBO(IServicioPeliculas servicio) {
        this.servicio = servicio;
    }

    public CarteleraDTO consultar() {
        return servicio.consultarCartelera();
    }

    public byte[] obtenerPoster(PeliculaDTO pelicula) {
        return servicio.obtenerPoster(pelicula);
    }
}